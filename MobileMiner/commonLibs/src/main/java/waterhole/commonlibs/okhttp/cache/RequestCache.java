package waterhole.commonlibs.okhttp.cache;

import android.text.TextUtils;

import java.io.Serializable;

import waterhole.commonlibs.ContextWrapper;
import waterhole.commonlibs.acache.ACache;
import waterhole.commonlibs.acache.CacheCallback;
import waterhole.commonlibs.annotation.ExcuteOnAsyn;
import waterhole.commonlibs.crypto.MD5;
import waterhole.commonlibs.okhttp.request.RequestCall;
import waterhole.commonlibs.utils.AppUtils;
import waterhole.commonlibs.utils.LogUtils;
import waterhole.commonlibs.utils.ObjectUtils;
import waterhole.commonlibs.utils.Preconditions;

/**
 * 请求缓存，为了减轻服务器的请求压力，当调用OKHttp时，使用useCache，那么在缓存有效期内将返回缓存数据，否则请求网络。
 * 开启缓存适用于只读型接口，比如get，像post类型的写接口则尽量不要开启缓存。
 *
 * @author kzw on 2017/08/02.
 */
public class RequestCache {

    private static final String TAG = "RequestCache";

    public RequestCache() {
        throw new RuntimeException("RequestCacheUtils stub!");
    }

    /**
     * 获取缓存数据的Key，生成规则：MD5.md5Hex(url)
     *
     * @param requestCall {@link RequestCall}
     */
    private static String getCacheDataKey(RequestCall requestCall) {
        if (requestCall != null && requestCall.getRequest() != null && requestCall.getRequest().url() != null) {
            // MD5转成16进制
            return MD5.md5Hex(requestCall.getRequest().url().toString());
        }
        return "";
    }

    /**
     * 获取缓存更新时间Key，生成规则cacheDataKey + "_update_time"
     *
     * @see #getCacheDataKey(RequestCall)
     */
    private static String getCacheUpdateTimeKey(String cacheDataKey) {
        if (!TextUtils.isEmpty(cacheDataKey)) {
            return cacheDataKey + "_update_time";
        }
        return "";
    }

    /**
     * 保存缓存数据，需要异步调用，保存缓存的data以及缓存更新时间
     *
     * @param requestCall {@link RequestCall}
     * @param object      数据对象
     */
    @ExcuteOnAsyn
    public static void saveCacheData(RequestCall requestCall, Object object) {
        // 检查是否在子线程
        Preconditions.checkOnChildThread();
        if (requestCall != null && object != null && object instanceof Serializable) {
            // 获取数据缓存key
            String cacheKey = getCacheDataKey(requestCall);
            if (!TextUtils.isEmpty(cacheKey)) {
                // 获取缓存更新时间key
                String cacheUpdateTimeKey = getCacheUpdateTimeKey(cacheKey);
                ACache cache = ACache.get(ContextWrapper.getInstance().obtainContext());
                cache.put(cacheKey, (Serializable) object);
                cache.put(cacheUpdateTimeKey, Long.toString(System.currentTimeMillis()));
            }
        }
    }

    /**
     * 读取缓存数据，需要异步调用，命中缓存的结果通过{@link CacheCallback}回调到主线程
     *
     * @param requestCall   {@link RequestCall}
     * @param cacheCallback 缓存回调
     */
    @ExcuteOnAsyn
    public static void readCacheData(RequestCall requestCall, CacheCallback<Object> cacheCallback) {
        // 检查是否在子线程
        Preconditions.checkOnChildThread();
        if (requestCall != null && cacheCallback != null) {
            String cacheKey = getCacheDataKey(requestCall);
            if (!TextUtils.isEmpty(cacheKey)) {
                ACache cache = ACache.get(ContextWrapper.getInstance().obtainContext());
                // 读取缓存数据
                Object data = cache.getAsObject(cacheKey);
                if (data != null) {
                    String cacheUpdateTimeKey = getCacheUpdateTimeKey(cacheKey);
                    long updateTime = ObjectUtils.getAsLong(cache.getAsString(cacheUpdateTimeKey));
                    // 如果在缓存有效期内
                    long currentTime = System.currentTimeMillis();
                    LogUtils.info(TAG, "updateTime:" + updateTime + ",currentTime:" + currentTime);
                    if (updateTime != 0 && (currentTime - updateTime <= requestCall.getCacheTimeOut())) {
                        // 切换到主线程回调成功
                        scheduleMainThreadCallbackSuccess(cacheCallback, data);
                        return;
                    } else {
                        cache.remove(cacheKey);
                        cache.remove(cacheUpdateTimeKey);
                    }
                } else {
                    cache.remove(cacheKey);
                }
            }
        }
        // 切换到主线程回调失败
        scheduleMainThreadCallbackFail(cacheCallback);
    }

    /**
     * 切换到主线程回调成功数据
     *
     * @param cacheCallback {@link CacheCallback}
     * @param data          缓存结果数据
     */
    private static void scheduleMainThreadCallbackSuccess(final CacheCallback<Object> cacheCallback, final Object data) {
        AppUtils.runAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                cacheCallback.runResultOnMainThread(data);
            }
        });
    }

    /**
     * 切换到主线程回调失败
     *
     * @param cacheCallback {@link CacheCallback}
     */
    private static void scheduleMainThreadCallbackFail(final CacheCallback<Object> cacheCallback) {
        AppUtils.runAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                cacheCallback.runFailOnMainThread();
            }
        });
    }
}
