/*
 * Copyright (C) 2014 Zlianjie Inc. All rights reserved.
 */
package waterhole.commonlibs.image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import waterhole.commonlibs.utils.DeviceUtils;
import waterhole.commonlibs.utils.FileUtils;
import waterhole.commonlibs.utils.IOUtils;

import static waterhole.commonlibs.utils.LogUtils.printStackTrace;

/**
 * 图片处理utils
 * <p>
 * decodeFile, decodeResource, decodeStream, deocdeByteArray,
 * decodeFile与decodeResource内部调用了decodeStream
 * <p>
 * options必须是大于1的数,小于1系统默认也是等于1.
 * options最好是2的指数,如2,4,8,否则会向下取整,如3,则会取2,但经测试不是在所有版本上都生效
 * options如为n,那么采样后,宽高均为原图的 1 / n, 最终的像素为原图的 1 / n平方, 如2, 则为
 * 原图的 1 / 4
 * <p>
 * 最终大小为像素值 * 每像素占用的内存,和色彩模式相关。
 * <p>
 * Bitmap.Config ARGB_4444：每个像素占四位，即A=4，R=4，G=4，B=4，那么一个像素点占4+4+4+4=16位
 * Bitmap.Config ARGB_8888：每个像素占八位，即A=8，R=8，G=8，B=8，那么一个像素点占8+8+8+8=32位
 * Bitmap.Config RGB_565：即R=5，G=6，B=5，没有透明度，那么一个像素点占5+6+5=16位
 * Bitmap.Config ALPHA_8：每个像素占四位，只有透明度，没有颜色。
 * <p>
 * 一个字节为8位,所有ARGB_8888 32位 = 4字节
 *
 * @author kzw on 2014/04/10
 */
public final class ImageUtils {

    public ImageUtils() {
        throw new AssertionError();
    }

    public static class ImageSize {

        int width;
        int height;

        public ImageSize() {
        }

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "ImageSize{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    /**
     * 根据InputStream获取图片实际的宽度和高度
     */
    public static ImageSize getImageSize(InputStream imageStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);
        return new ImageSize(options.outWidth, options.outHeight);
    }

    public static int calculateInSampleSize(ImageSize srcSize, ImageSize targetSize) {
        // 源图片的宽度
        int width = srcSize.width;
        int height = srcSize.height;
        int inSampleSize = 1;

        int reqWidth = targetSize.width;
        int reqHeight = targetSize.height;
        if (width > reqWidth && height > reqHeight) {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) height / (float) reqHeight);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    /**
     * 根据ImageView获适当的压缩的宽和高
     */
    public static ImageSize getImageViewSize(View view) {
        ImageSize imageSize = new ImageSize();
        imageSize.width = getExpectWidth(view);
        imageSize.height = getExpectHeight(view);
        return imageSize;
    }

    /**
     * 根据view获得期望的高度
     */
    private static int getExpectHeight(View view) {
        int height = 0;
        if (view == null) return 0;

        final ViewGroup.LayoutParams params = view.getLayoutParams();
        //如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
        if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            // 获得实际的宽度
            height = view.getWidth();
        }
        if (height <= 0 && params != null) {
            // 获得布局文件中的声明的宽度
            height = params.height;
        }
        if (height <= 0) {
            // 获得设置的最大的宽度
            height = getImageViewFieldValue(view, "mMaxHeight");
        }
        //如果宽度还是没有获取到，憋大招，使用屏幕的宽度
        if (height <= 0) {
            DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
            height = displayMetrics.heightPixels;
        }
        return height;
    }

    /**
     * 根据view获得期望的宽度
     */
    private static int getExpectWidth(View view) {
        int width = 0;
        if (view == null) return 0;
        final ViewGroup.LayoutParams params = view.getLayoutParams();
        //如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
        if (params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
            // 获得实际的宽度
            width = view.getWidth();
        }
        if (width <= 0 && params != null) {
            // 获得布局文件中的声明的宽度
            width = params.width;
        }
        if (width <= 0) {
            // 获得设置的最大的宽度
            width = getImageViewFieldValue(view, "mMaxWidth");
        }
        //如果宽度还是没有获取到，憋大招，使用屏幕的宽度
        if (width <= 0) {
            DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
            width = displayMetrics.widthPixels;
        }
        return width;
    }

    /**
     * 通过反射获取imageview的某个属性值
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            printStackTrace(e);
        }
        return value;

    }

    public static Bitmap revitionImageSize(String path) throws IOException {
        return decodeSampledBitmapFromFd(path);
    }

    static Bitmap decodeSampledBitmapFromFd(String pathName) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        int reqHeight = 256;
        int reqWidth = (int) ((double) options.outWidth / (double) options.outHeight * 256);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);

        return createScaleBitmap(src, reqWidth, reqHeight);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                             int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
     */
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        // 如果没有缩放，那么不回收
        if (src != dst) {
            // 释放Bitmap的native像素数组
            recycleBitmap(src);
        }
        return dst;
    }

    /**
     * 从指定资源中解析bitmap
     *
     * @param context context
     * @param resId   resource id
     * @return bitmap
     */
    public static Bitmap decodeBitmapFromResource(Context context, int resId) {
        if (null == context || resId == 0) {
            return null;
        }
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    /**
     * Compute the sample size as a function of minSideLength and maxNumOfPixels. minSideLength is
     * used to specify that minimal width or height of a bitmap. maxNumOfPixels is used to specify
     * the maximal size in pixels that is tolerable in terms of memory usage.
     * <p/>
     * The function returns a sample size based on the constraints. Both size and minSideLength can
     * be passed in as IImage.UNCONSTRAINED, which indicates no care of the corresponding
     * constraint. The functions prefers returning a sample size that generates a smaller bitmap,
     * unless minSideLength = IImage.UNCONSTRAINED.
     * <p/>
     * Also, the function rounds up the sample size to a power of 2 or multiple of 8 because
     * BitmapFactory only honors sample size this way. For example, BitmapFactory downsamples an
     * image by 2 even though the request is 3. So we round up the sample size to avoid OOM.
     *
     * @param options        options
     * @param minSideLength  最小的边
     * @param maxNumOfPixels 最大的像素值
     * @return 采样值
     */
    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
                                         int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) { // SUPPRESS CHECKSTYLE
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;    // SUPPRESS CHECKSTYLE
        }

        return roundedSize;
    }

    /**
     * computeInitialSampleSize
     *
     * @param options        options
     * @param minSideLength  最小的边
     * @param maxNumOfPixels 最大的像素值
     * @return 采样值
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
                                                int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        final int UNCONSTRAINED = -1;// SUPPRESS CHECKSTYLE
        final int UPPERBOUND = 128;// SUPPRESS CHECKSTYLE

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1
                : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? UPPERBOUND
                : (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 把Bitmap保存到文件中
     *
     * @param bmp      Bitmap
     * @param destFile 目标文件
     */
    public static void saveBitmapToFile(Bitmap bmp, File destFile) {
        if (null != bmp && null != destFile && !destFile.isDirectory()) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(destFile);
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);// SUPPRESS CHECKSTYLE
                fos.flush();
            } catch (Exception e) {
                printStackTrace(e);
            } finally {
                IOUtils.closeSafely(fos);
            }
        }
    }

    /**
     * 加载Bitmap
     */
    public static Bitmap loadBitmap(String path, float maxWidth, float maxHeight, boolean useMaxScale) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, bmOptions);

        float photoW = bmOptions.outWidth;
        float photoH = bmOptions.outHeight;
        float scaleFactor = useMaxScale ? Math.max(photoW / maxWidth, photoH / maxHeight) :
                Math.min(photoW / maxWidth, photoH / maxHeight);
        if (scaleFactor < 1) {
            scaleFactor = 1;
        }
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = (int) scaleFactor;
        bmOptions.inPurgeable = Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 21;

        Matrix matrix = null;

        ExifInterface exif;
        try {
            exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
            }
        } catch (Throwable e) {
            printStackTrace(e);
        }

        Bitmap b = null;
        try {
            b = BitmapFactory.decodeFile(path, bmOptions);
            if (b != null) {
                Bitmap newBitmap = createBitmap(matrix, b);
                if (newBitmap != b) {
                    ImageUtils.recycleBitmap(b);
                    b = newBitmap;
                }
            }
        } catch (Throwable e) {
            try {
                if (b == null) {
                    b = BitmapFactory.decodeFile(path, bmOptions);
                }
                if (b != null) {
                    Bitmap newBitmap = createBitmap(matrix, b);
                    if (newBitmap != b) {
                        recycleBitmap(b);
                        b = newBitmap;
                    }
                }
            } catch (Throwable e2) {
                printStackTrace(e2);
            }
        }
        return b;
    }

    private static Bitmap createBitmap(Matrix matrix, Bitmap b) {
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
    }

    public static Bitmap getBigBitmapForDisplay(String imagePath, Context context) {
        if (null == imagePath || !new File(imagePath).exists()) {
            return null;
        }
        try {
            int degeree = readPictureDegree(imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap == null) {
                return null;
            }
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            float scale = bitmap.getWidth() / (float) dm.widthPixels;
            Bitmap newBitMap;
            if (scale > 1) {
                newBitMap = zoomBitmap(bitmap, (int) (bitmap.getWidth() / scale),
                        (int) (bitmap.getHeight() / scale));
                recycleBitmap(bitmap);
                return rotaingImageView(degeree, newBitMap);
            }
            return rotaingImageView(degeree, bitmap);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        if (null == bitmap) {
            return null;
        }
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidth = ((float) width / w);
            float scaleHeight = ((float) height / h);
            matrix.postScale(scaleWidth, scaleHeight);
            return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        } catch (OutOfMemoryError e) {
            System.gc();
            return null;
        }
    }

    public static Bitmap getBitmapByPath(Context ctx, String imgPath) {
        if (ctx == null || TextUtils.isEmpty(imgPath)) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 计算图片缩放比例
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, opts);
        int w = opts.outWidth;
        int h = opts.outHeight;
        if (w <= 0 || h <= 0) {
            return null;
        }
        int screenW = ctx.getResources().getDisplayMetrics().widthPixels;
        int screenH = ctx.getResources().getDisplayMetrics().heightPixels;
        int imgH;
        int imgW;
        if (w > h) {
            imgH = h * screenW / w;
            imgW = screenW;
        } else {
            imgH = screenH;
            imgW = w * screenH / h;
        }
        // 计算图片缩放比例
        final int minSideLength = Math.min(imgW, imgH);
        int inSample = computeSampleSize(opts, minSideLength, imgW * imgH);
        int memoryLevel = DeviceUtils.getmMemoryLevel();
        if (memoryLevel == DeviceUtils.MEMORY_MIDDLE) {
            inSample = 2;
        } else if (memoryLevel == DeviceUtils.MEMORY_SMALL) {
            inSample = 3;
        }
        opts.inSampleSize = inSample >= 1 ? inSample : 1;
        opts.inJustDecodeBounds = false;
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeFile(imgPath, opts);
        } catch (OutOfMemoryError e) {
            System.gc();
            opts.inSampleSize = ++inSample;
            bitmap = BitmapFactory.decodeFile(imgPath, opts);
        }
        return bitmap;
    }

    public static int[] justGetBitmapSize(String path) {
        int[] size = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;
        options.inJustDecodeBounds = false;
        return size;
    }

    public static Bitmap getBitmap(Context context, String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 计算图片缩放比例
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int w = opts.outWidth;
        int h = opts.outHeight;
        if (w <= 0 || h <= 0) {
            return null;
        }
        int screenW = context.getResources().getDisplayMetrics().widthPixels;
        int screenH = context.getResources().getDisplayMetrics().heightPixels;
        int imgH;
        int imgW;
        if (w > h) {
            imgH = h * screenW / w;
            imgW = screenW;
        } else {
            imgH = screenH;
            imgW = w * screenH / h;
        }
        // 计算图片缩放比例
        final int minSideLength = Math.min(imgW, imgH);
        int inSample = computeSampleSize(opts, minSideLength, imgW * imgH);
        opts.inSampleSize = inSample >= 1 ? inSample : 1;
        opts.inJustDecodeBounds = false;
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeFile(path, opts);
        } catch (OutOfMemoryError e) {
            System.gc();
            opts.inSampleSize = ++inSample;
            bitmap = BitmapFactory.decodeFile(path, opts);
        }
        return bitmap;
    }

    public static Bitmap scaleImageLimitWidth(Bitmap image, int maxWidth) {
        if (image == null) {
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        //宽大于极限值
        if (width > maxWidth) {
            int newWidth, newHeight;
            newWidth = maxWidth;
            newHeight = maxWidth * height / width;
            image = Bitmap.createScaledBitmap(image, newWidth, newHeight, true);
        }
        return image;
    }

    public static void handlePictureDegree(String fileName) throws Exception {
        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        bitmap = rotaingImageView(readPictureDegree(fileName), bitmap);
        File dirFile = new File(fileName);
        // 检测图片是否存在
        if (dirFile.exists()) {
            FileUtils.delete(dirFile);
        }
        File myCaptureFile = new File(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        // 100表示不进行压缩，70表示压缩率为30%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        recycleBitmap(bitmap);
        bos.flush();
        IOUtils.closeSafely(bos);
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;

        if (TextUtils.isEmpty(path)) {
            return degree;
        }
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            printStackTrace(e);
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return createBitmap(matrix, bitmap);
    }

    public static Bitmap convertView2Bitmap(View view) {
        if (view == null) {
            return null;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    @SuppressLint("NewApi")
    public static Bitmap blurBitmap(Context context, Bitmap bitmap, float radius) {
        if (context != null && bitmap != null) {
            Bitmap outBitmap = Bitmap
                    .createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
            Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
            blurScript.setRadius(radius);
            blurScript.setInput(allIn);
            blurScript.forEach(allOut);

            allOut.copyTo(outBitmap);
            // release
            allIn.destroy();
            allOut.destroy();
            blurScript.destroy();
            rs.destroy();
            return outBitmap;
        }
        return null;
    }

    public static int[] getThumbSize(int width, int height) {
        int maxValue = Math.max(width, height);
        //  适配长图
        if (Math.max(maxValue / width, maxValue / height) > 3) {
            return new int[]{120, 250};
        }
        if (maxValue > 200) {
            width = width * 200 / maxValue;
            height = height * 200 / maxValue;
        }
        return new int[]{width, height};
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
