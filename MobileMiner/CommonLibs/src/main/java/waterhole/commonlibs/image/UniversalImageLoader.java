package waterhole.commonlibs.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import waterhole.commonlibs.ContextWrapper;
import waterhole.commonlibs.R;
import waterhole.commonlibs.utils.APIUtils;
import waterhole.commonlibs.utils.AnimationUtils;

import static waterhole.commonlibs.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.commonlibs.utils.LogUtils.printStackTrace;
import static waterhole.commonlibs.utils.ThreadUtils.getCoreThreadSize;

/**
 * 对{@link com.nostra13.universalimageloader.core.ImageLoader}的包装，包括初始化，构造{@link DisplayImageOptions}
 * 用以显示正常图片，圆角图片，同时封装了图片的渐变加载，缓存清除的接口.
 *
 * @author kzw on 2017/07/22.
 */
public final class UniversalImageLoader {

    // 上下文对象
    private final Context mContext = ContextWrapper.getInstance().obtainContext();

    // 图片加载淡入动画时长
    private static final int IMAGE_LOAD_ALPHA_DURATION = 500;

    // 圆角度数
    public final static int CIRCLE_CORNER_DEGRESS = 150;
    // 矩形度数 - 5
    public final static int ROUND_RECT_DEGRESS_5 = 5;
    // 矩形度数 - 10
    public final static int ROUND_RECT_DEGRESS_10 = 10;

    // Bitmap generationId缓存，避免图片刷新加载执行渐变动画（如已执行过渐变）
    private List<Long> mBitmapGenerationIds = Collections.synchronizedList(new ArrayList<Long>());

    // ImageLoader实例
    private final ImageLoader mImageLoader = ImageLoader.getInstance();

    private UniversalImageLoader() {
    }

    private static final class UniversalImageLoaderHolder {
        private static UniversalImageLoader instance = new UniversalImageLoader();
    }

    private Object readResolve() throws ObjectStreamException {
        return getInstance();
    }

    public static UniversalImageLoader getInstance() {
        return UniversalImageLoaderHolder.instance;
    }

    /**
     * @return 获取{@link ImageLoader}的磁盘缓存对象
     */
    public DiskCache getDiskCache() {
        return mImageLoader.getDiskCache();
    }

    /**
     * 获取{@link ImageLoader}的缓存文件
     *
     * @param url 图片url地址
     */
    public File getCacheFile(String url) {
        return getDiskCache().get(url);
    }

    /**
     * @return 获取{@link ImageLoader}的缓存目录
     */
    public File getCacheDirectory() {
        return getDiskCache().getDirectory();
    }

    /**
     * 初始化{@link ImageLoader}相关的配置，使用{@link ImageLoader}提供的默认缓存目录和图片解码器
     */
    public void init() {
        init(null, null);
    }

    /**
     * 初始化{@link ImageLoader}相关的配置，外部传入缓存目录，使用{@link ImageLoader}提供的图片解码器
     */
    public void init(File cacheDir) {
        init(cacheDir, null);
    }

    /**
     * 初始化{@link ImageLoader}相关的配置，外部传入图片解码器，使用{@link ImageLoader}提供的缓存目录
     */
    public void init(ImageDecoder imageDecoder) {
        init(null, imageDecoder);
    }

    /**
     * 初始化{@link ImageLoader}相关的配置，外部传入缓存目录，图片解码器，如果不用这个配置去设置，
     * 可以自己获取{@link ImageLoader}去自定义配置，但是不建议这么做，目前的配置可以满足大部分的需求.
     *
     * @param cacheDir     缓存目录
     * @param imageDecoder 图片解码，如果不传入则使用默认的
     */
    public void init(File cacheDir, ImageDecoder imageDecoder) {
        if (!mImageLoader.isInited()) {
            // 如果没有初始化过，则去初始化
            // ImageLoader加载配置
            if (cacheDir == null) {
                cacheDir = StorageUtils.getCacheDirectory(mContext);
            }
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(mContext)
                    // 核心线程数
                    .threadPoolSize(getCoreThreadSize())
                    .memoryCacheExtraOptions(metrics.widthPixels, metrics.heightPixels)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    // 加载图片时后进先出
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .diskCacheExtraOptions(metrics.widthPixels, metrics.heightPixels, null)
                    .diskCache(new UnlimitedDiskCache(cacheDir, cacheDir, new Md5FileNameGenerator()))
                    .memoryCache(new FIFOLimitedMemoryCache(100));
            if (imageDecoder != null) {
                // 图片解码器
                builder.imageDecoder(imageDecoder);
            }
            ImageLoaderConfiguration imageLoaderConfiguration = builder.build();
            mImageLoader.init(imageLoaderConfiguration);
        }
    }

    /**
     * 获取通用的{@link DisplayImageOptions}的builder
     *
     * @param defaultRes 默认图资源id
     */
    private static Builder getUniversalDisplayImageOptionsBuilder(int defaultRes) {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .showImageOnLoading(defaultRes)
                .showImageOnFail(defaultRes)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565);
    }

    /**
     * 获取圆角相关的{@link DisplayImageOptions}，传入圆角度数和默认图，内部处理了正圆和一般椭圆
     *
     * @param corner     圆角度数
     * @param defaultRes 默认图资源id
     */
    public DisplayImageOptions getRoundOptions(int corner, int defaultRes) {
        if (corner < 0) {
            corner = 0;
        }
        DisplayImageOptions options;
        try {
            // 如果为正圆
            if (corner == CIRCLE_CORNER_DEGRESS) {
                options = getUniversalDisplayImageOptionsBuilder(defaultRes).displayer(new CircleBitmapDisplayer()).build();
            } else {
                options = getUniversalDisplayImageOptionsBuilder(defaultRes).displayer(new RoundedBitmapDisplayer(corner)).build();
            }
        } catch (Exception e) {
            printStackTrace(e);
            return null;
        }
        return options;
    }

    public void displayImage(ImageView imageView, String url) {
        displayImage(imageView, url, R.drawable.ic_image_default);
    }

    public void displayImage(ImageView imageView, String url, int defaultRes) {
        displayImage(imageView, url, defaultRes, null);
    }

    public void displayImage(ImageView imageView, String url, int defaultRes, ImageLoadingListener imageLoadingListener) {
        displayImage(imageView, url, defaultRes, imageLoadingListener, null);
    }

    public void displayImage(ImageView imageView, String url, DisplayImageOptions options, ImageLoadingListener imageLoadingListener) {
        displayImage(imageView, url, options, imageLoadingListener, null);
    }

    public void displayImage(ImageView imageView, String url, int defaultRes, ImageLoadingListener imageLoadingListener, ImageLoadingProgressListener progressListener) {
        displayImage(imageView, url, getUniversalDisplayImageOptionsBuilder(defaultRes).build(), imageLoadingListener, progressListener);
    }

    /**
     * 将显示图像任务添加到执行池。图像将被设置为ImageAware.<br />
     * <b>NOTE:</b> {@link #init(File, ImageDecoder)}} or {@link ImageLoader#init(ImageLoaderConfiguration)}初始化方法必须先调用
     *
     * @param imageView            显示的图片控件
     * @param url                  图片的url地址
     * @param options              {@link DisplayImageOptions}
     * @param imageLoadingListener 图片加载监听
     * @param progressListener     加载进度监听
     */
    public void displayImage(ImageView imageView, String url, DisplayImageOptions options, ImageLoadingListener imageLoadingListener, ImageLoadingProgressListener progressListener) {
        if (imageView != null && !TextUtils.isEmpty(url)) {
            ImageAware imageAware = new ImageViewAware(imageView, true);
            if (!mImageLoader.isInited()) {
                init();
            }
            mImageLoader.displayImage(url, imageAware, options, imageLoadingListener, progressListener);
        }
    }

    /**
     * 将显示图像任务添加到执行池。图像将被设置为ImageAware.<br />
     * <b>NOTE:</b> {@link #init(File, ImageDecoder)}} or {@link ImageLoader#init(ImageLoaderConfiguration)}初始化方法必须先调用
     *
     * @param url                  图片的url地址
     * @param targetImageSize      目标图片的大小
     * @param imageLoadingListener 图片加载监听
     */
    public void loadImage(String url, ImageSize targetImageSize, ImageLoadingListener imageLoadingListener) {
        if (!mImageLoader.isInited()) {
            init();
        }
        mImageLoader.loadImage(url, targetImageSize, null, imageLoadingListener, null);
    }

    /**
     * 取消图片显示任务
     *
     * @param imageView 显示的图片控件
     */
    public void cancelDisplayTask(ImageView imageView) {
        mImageLoader.cancelDisplayTask(imageView);
    }

    public void fadeLoadBitmap(ImageAware imageAware, Bitmap bitmap) {
        fadeLoadBitmap(imageAware, bitmap, IMAGE_LOAD_ALPHA_DURATION);
    }

    /**
     * 渐变加载位图，其实就是给图片控件加上渐变动画
     *
     * @param imageAware {@link ImageAware}
     * @param bitmap     位图
     * @param duration   渐变动画时长
     */
    public void fadeLoadBitmap(ImageAware imageAware, Bitmap bitmap, int duration) {
        if (imageAware == null || imageAware.getWrappedView() == null || bitmap == null) {
            return;
        }
        if (APIUtils.hasHoneycombMR1()) {
            long generationId = bitmap.getGenerationId();
            if (!mBitmapGenerationIds.contains(generationId)) {
                mBitmapGenerationIds.add(generationId);
                AnimationUtils.animate(imageAware.getWrappedView(), duration);
            }
        }
    }

    /**
     * 清除内存缓存，包括清除{@link ImageLoader}的内存缓存，以及{@link DisplayImageOptions}的缓存，
     * BitmapGenerationIds的缓存，一般在内存告急时调用.
     */
    public void clearMemoryCache() {
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                synchronized (UniversalImageLoader.this) {
                    try {
                        mImageLoader.clearMemoryCache();
                        mBitmapGenerationIds.clear();
                    } catch (Exception e) {
                        printStackTrace(e);
                    }
                }
            }
        });
    }

    /**
     * 清除硬盘缓存，这边清除是{@link ImageLoader}相关的硬盘缓存
     */
    public void clearDiskCache() {
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                synchronized (UniversalImageLoader.this) {
                    try {
                        mImageLoader.clearDiskCache();
                    } catch (Exception e) {
                        printStackTrace(e);
                    }
                }
            }
        });
    }

    /**
     * 停止ImageLoader，清除当前的配置
     */
    public void destroy() {
        mImageLoader.destroy();
    }
}
