package waterhole.commonlibs.okhttp;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import waterhole.commonlibs.acache.CacheCallback;
import waterhole.commonlibs.asyn.AsyncTaskAssistant;
import waterhole.commonlibs.okhttp.builder.GetBuilder;
import waterhole.commonlibs.okhttp.builder.HeadBuilder;
import waterhole.commonlibs.okhttp.builder.OtherRequestBuilder;
import waterhole.commonlibs.okhttp.builder.PostFileBuilder;
import waterhole.commonlibs.okhttp.builder.PostFormBuilder;
import waterhole.commonlibs.okhttp.builder.PostStringBuilder;
import waterhole.commonlibs.okhttp.cache.RequestCache;
import waterhole.commonlibs.okhttp.callback.Callback;
import waterhole.commonlibs.okhttp.request.RequestCall;
import waterhole.commonlibs.utils.LogUtils;
import waterhole.commonlibs.utils.PlatformUtils;

/**
 * OkHttp请求工具，提供最外层的网络请求接口：初始化，提供各种请求的builder函数，发起请求，请求回调，取消请求.
 *
 * @author kzw on 2017/07/31.
 */
public class OkHttpUtils {

    public static final long DEFAULT_MILLISECONDS = 10_000L;

    // OkHttp客户端实例
    private OkHttpClient mOkHttpClient;
    // 平台相关
    private PlatformUtils mPlatform;

    // 单例
    private volatile static OkHttpUtils mInstance;

    public OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = okHttpClient;
        }
        mPlatform = PlatformUtils.get();
    }

    public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static OkHttpUtils getInstance() {
        return initClient(null);
    }

    public Executor getDelivery() {
        return mPlatform.defaultCallbackExecutor();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public static OtherRequestBuilder put() {
        return new OtherRequestBuilder(METHOD.PUT);
    }

    public static HeadBuilder head() {
        return new HeadBuilder();
    }

    public static OtherRequestBuilder delete() {
        return new OtherRequestBuilder(METHOD.DELETE);
    }

    public static OtherRequestBuilder patch() {
        return new OtherRequestBuilder(METHOD.PATCH);
    }

    public void execute(final RequestCall requestCall, Callback callback) {
        if (callback == null) {
            callback = Callback.CALLBACK_DEFAULT;
        }
        final Callback finalCallback = callback;
        final int id = requestCall.getOkHttpRequest().getId();
        if (requestCall.isUseCache()) {
            AsyncTaskAssistant.executeOnThreadPool(new Runnable() {
                @Override
                @SuppressWarnings("unchecked")
                public void run() {
                    RequestCache.readCacheData(requestCall, new CacheCallback<Object>() {
                        @Override
                        public void runResultOnMainThread(Object o) {
                            finalCallback.onResponse(o, id);
                        }

                        @Override
                        public void runFailOnMainThread() {
                            execute(requestCall, finalCallback, id);
                        }
                    });
                }
            });
        } else {
            execute(requestCall, finalCallback, id);
        }
    }

    private void execute(final RequestCall requestCall, final Callback callback, final int id) {
        try {
            requestCall.getCall().enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    if (requestCall.isUseRetry() && !requestCall.rearchMaxRetryCounts()) {
                        requestCall.increaseRetryCounts();
                        // 递归
                        execute(requestCall, callback, id);
                    } else {
                        // 请求失败
                        sendFailResultCallback(call, e, callback, id);
                    }
                }

                @Override
                public void onResponse(final Call call, final Response response) {
                    try {
                        if (call.isCanceled()) {
                            sendFailResultCallback(call, new IOException("Canceled!"), callback, id);
                            return;
                        }
                        // 请求成功
                        if (!callback.validateReponse(response, id)) {
                            sendFailResultCallback(call, new IOException("request failed , reponse's code is : "
                                    + response.code()), callback, id);
                            return;
                        }
                        final Object o = callback.parseNetworkResponse(response, id);
                        sendSuccessResultCallback(o, callback, id);

                        if (requestCall.isUseCache()) {
                            AsyncTaskAssistant.executeOnThreadPool(new Runnable() {
                                @Override
                                public void run() {
                                    RequestCache.saveCacheData(requestCall, o);
                                }
                            });
                        }
                    } catch (Exception e) {
                        sendFailResultCallback(call, e, callback, id);
                    } finally {
                        // 关闭请求
                        if (response.body() != null) {
                            response.body().close();
                        }
                    }
                }
            });
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
    }

    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback, final int id) {
        if (callback == null) return;
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, e, id);
                callback.onAfter(id);
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback, final int id) {
        if (callback == null) return;
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object, id);
                callback.onAfter(id);
            }
        });
    }

    public void cancelTag(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public static class METHOD {
        public static final String HEAD = "HEAD";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";
    }
}

