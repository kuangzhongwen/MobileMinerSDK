package waterhole.commonlibs.okhttp.request;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import waterhole.commonlibs.okhttp.OkHttpUtils;
import waterhole.commonlibs.okhttp.callback.Callback;

import static waterhole.commonlibs.utils.LogUtils.printStackTrace;

/**
 * 对OkHttpRequest的封装，对外提供更多的接口：cancel(),readTimeOut()...
 *
 * @author kzw on 2017/07/31.
 */
public class RequestCall {

    // OkHttp请求包装
    private OkHttpRequest mOkHttpRequest;
    private Request mRequest;
    private Call mCall;

    // 超时时间
    private long mReadTimeOut;
    private long mWriteTimeOut;
    private long mConnTimeOut;

    // 是否使用缓存
    private boolean isUseCache;
    // 缓存超时时间，默认为10分钟，只有isUseCache=true时这个属性才有意义
    private long mCacheTimeOut = 10 * 60 * 1000L;

    // 是否开启重试
    private boolean isUseRetry;
    // 重试次数，只有isUseRetry=true时这个属性才有意义
    private int mRetryCounts;
    // 最大重试次数，默认3次，只有isUseRetry=true时这个属性才有意义
    private int mMaxRetryCounts = 3;

    // 证书相关
    private SSLSocketFactory mSSLSocketFactory;

    public RequestCall(OkHttpRequest request) {
        mOkHttpRequest = request;
    }

    public RequestCall readTimeOut(long readTimeOut) {
        mReadTimeOut = readTimeOut;
        return this;
    }

    public RequestCall writeTimeOut(long writeTimeOut) {
        mWriteTimeOut = writeTimeOut;
        return this;
    }

    public RequestCall connTimeOut(long connTimeOut) {
        mConnTimeOut = connTimeOut;
        return this;
    }

    /**
     * 是否使用缓存，一般get接口可以使用缓存，而post等具有写性质的接口不推荐使用
     * 配合缓存时间，如果在缓存有效期内，则返回有效缓存，否则请求服务器
     *
     * @param useCache 是否使用缓存
     */
    public RequestCall useCache(boolean useCache) {
        isUseCache = useCache;
        return this;
    }

    public RequestCall cacheTimeOut(long cacheTimeOut) {
        mCacheTimeOut = cacheTimeOut;
        return this;
    }

    /**
     * 当请求失败时，开始重试，重试最大次数{@link #mMaxRetryCounts}
     *
     * @param useRetry 是否重试，默认为false
     */
    public RequestCall useRetry(boolean useRetry) {
        isUseRetry = useRetry;
        return this;
    }

    public RequestCall maxRetryCounts(int maxRetryCounts) {
        mMaxRetryCounts = maxRetryCounts;
        return this;
    }

    public RequestCall sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        mSSLSocketFactory = sslSocketFactory;
        return this;
    }

    public Call buildCall(Callback callback) {
        return buildCall(callback, true);
    }

    public Call buildCall(Callback callback, boolean safe) {
        mRequest = generateRequest(callback);

        if (mReadTimeOut > 0 || mWriteTimeOut > 0 || mConnTimeOut > 0 || mSSLSocketFactory != null) {
            mReadTimeOut = mReadTimeOut > 0 ? mReadTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            mWriteTimeOut = mWriteTimeOut > 0 ? mWriteTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            mConnTimeOut = mConnTimeOut > 0 ? mConnTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;

            OkHttpClient.Builder builder = OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
                    .readTimeout(mReadTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(mWriteTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(mConnTimeOut, TimeUnit.MILLISECONDS);

            if (mSSLSocketFactory != null && safe) {
                builder.sslSocketFactory(mSSLSocketFactory);
            } else {
                trustAllCert(builder);
            }
            OkHttpClient clone = builder.build();
            mCall = clone.newCall(mRequest);
        } else {
            if (!safe) {
                OkHttpClient.Builder builder = OkHttpUtils.getInstance().getOkHttpClient().newBuilder();
                trustAllCert(builder);
                OkHttpClient clone = builder.build();
                mCall = clone.newCall(mRequest);
            } else {
                mCall = OkHttpUtils.getInstance().getOkHttpClient().newCall(mRequest);
            }
        }
        return mCall;
    }

    private void trustAllCert(OkHttpClient.Builder builder) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] chain,
                                String authType) {
                        }

                        @Override
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] chain,
                                String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    private Request generateRequest(Callback callback) {
        return mOkHttpRequest.generateRequest(callback);
    }

    public void execute(Callback callback) {
        execute(callback, true);
    }

    public void execute(Callback callback, boolean safe) {
        buildCall(callback, safe);

        if (callback != null) {
            callback.onBefore(mRequest, getOkHttpRequest().getId());
        }

        OkHttpUtils.getInstance().execute(this, callback);
    }

    public boolean isUseCache() {
        return isUseCache;
    }

    public long getCacheTimeOut() {
        return mCacheTimeOut;
    }

    public boolean isUseRetry() {
        return isUseRetry;
    }

    public void increaseRetryCounts() {
        ++mRetryCounts;
    }

    public boolean rearchMaxRetryCounts() {
        return mRetryCounts >= mMaxRetryCounts;
    }

    public Call getCall() {
        return mCall;
    }

    public Request getRequest() {
        return mRequest;
    }

    public OkHttpRequest getOkHttpRequest() {
        return mOkHttpRequest;
    }

    public Response execute() throws IOException {
        buildCall(null);
        return mCall.execute();
    }

    public void cancel() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
