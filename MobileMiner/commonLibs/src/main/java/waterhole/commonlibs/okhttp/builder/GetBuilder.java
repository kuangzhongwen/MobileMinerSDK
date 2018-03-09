package waterhole.commonlibs.okhttp.builder;

import android.net.Uri;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import waterhole.commonlibs.ContextWrapper;
import waterhole.commonlibs.acache.ACache;
import waterhole.commonlibs.acache.CacheCallback;
import waterhole.commonlibs.annotation.ExcuteOnAsyn;
import waterhole.commonlibs.crypto.MD5;
import waterhole.commonlibs.okhttp.request.GetRequest;
import waterhole.commonlibs.okhttp.request.RequestCall;
import waterhole.commonlibs.utils.AppUtils;
import waterhole.commonlibs.utils.ObjectUtils;
import waterhole.commonlibs.utils.Preconditions;

/**
 * get建造者
 *
 * @author kzw on 2017/07/31.
 */
public class GetBuilder extends OkHttpRequestBuilder<GetBuilder> implements HasParamsable {

    @Override
    public RequestCall build() {
        if (mParams != null) {
            mUrl = appendParams(mUrl, mParams);
        }
        return new GetRequest(mUrl, mTag, mParams, mHeaders, mID).build();
    }

    protected String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }

    @Override
    public GetBuilder params(Map<String, String> params) {
        mParams = params;
        return this;
    }

    @Override
    public GetBuilder addParams(String key, String val) {
        if (mParams == null) {
            mParams = new LinkedHashMap<>();
        }
        mParams.put(key, val);
        return this;
    }

    public static String getCacheDataKey(RequestCall requestCall) {
        if (requestCall != null && requestCall.getRequest() != null && requestCall.getRequest().url() != null) {
            return MD5.md5Hex(requestCall.getRequest().url().toString());
        }
        return "";
    }

    public static String getCacheUpdateTimeKey(String cacheDataKey) {
        if (!TextUtils.isEmpty(cacheDataKey)) {
            return cacheDataKey + "_update_time";
        }
        return "";
    }

    @ExcuteOnAsyn
    public static void saveCacheData(RequestCall requestCall, Object o) {
        Preconditions.checkOnChildThread();

        String cacheKey = getCacheDataKey(requestCall);
        if (!TextUtils.isEmpty(cacheKey)) {
            String cacheUpdateTimeKey = getCacheUpdateTimeKey(cacheKey);
            ACache cache = ACache.get(ContextWrapper.getInstance().obtainContext());
            if (o instanceof Serializable) {
                cache.put(cacheKey, (Serializable) o);
                cache.put(cacheUpdateTimeKey, System.currentTimeMillis());
            }
        }
    }

    @ExcuteOnAsyn
    public static void readCacheData(RequestCall requestCall, final CacheCallback<Object> cacheCallback) {
        Preconditions.checkOnChildThread();
        Preconditions.checkNotNull(cacheCallback);

        final ACache cache = ACache.get(ContextWrapper.getInstance().obtainContext());
        String cacheKey = getCacheDataKey(requestCall);
        if (!TextUtils.isEmpty(cacheKey)) {
            final Object data = cache.getAsObject(cacheKey);
            if (data != null) {
                String cacheUpdateTimeKey = getCacheUpdateTimeKey(cacheKey);
                long updateTime = ObjectUtils.getAsLong(cache.getAsString(cacheUpdateTimeKey));
                if (updateTime - System.currentTimeMillis() <= requestCall.getCacheTimeOut()) {
                    scheduleMainThreadCallbackSuccess(cacheCallback, data);
                } else {
                    cache.remove(cacheKey);
                    cache.remove(cacheUpdateTimeKey);
                    scheduleMainThreadCallbackFail(cacheCallback);
                }
            } else {
                cache.remove(cacheKey);
                scheduleMainThreadCallbackFail(cacheCallback);
            }
        } else {
            scheduleMainThreadCallbackFail(cacheCallback);
        }
    }

    private static void scheduleMainThreadCallbackSuccess(final CacheCallback<Object> cacheCallback, final Object data) {
        AppUtils.runAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                cacheCallback.runResultOnMainThread(data);
            }
        });
    }

    private static void scheduleMainThreadCallbackFail(final CacheCallback<Object> cacheCallback) {
        AppUtils.runAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                cacheCallback.runFailOnMainThread();
            }
        });
    }
}
