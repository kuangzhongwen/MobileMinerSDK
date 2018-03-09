package waterhole.commonlibs.net.okhttp.request;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import waterhole.commonlibs.net.okhttp.callback.Callback;

/**
 * OkHttp请求的抽象类
 *
 * @author kzw on 2017/07/31.
 */
public abstract class OkHttpRequest {

    protected String mUrl;
    protected Object mTag;
    protected int mID;

    protected Map<String, String> mParams;
    protected Map<String, String> mHeaders;

    protected Request.Builder mBuilder = new Request.Builder();

    protected OkHttpRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id) {
        mUrl = url;
        mTag = tag;
        mParams = params;
        mHeaders = headers;
        mID = id;

        if (url == null) {
            throw new IllegalArgumentException("url can not be null.");
        }

        initBuilder();
    }

    /**
     * 初始化一些基本参数 url , tag , headers
     */
    private void initBuilder() {
        mBuilder.url(mUrl).tag(mTag);
        appendHeaders();
    }

    protected abstract RequestBody buildRequestBody();

    protected RequestBody wrapRequestBody(RequestBody requestBody, final Callback callback) {
        return requestBody;
    }

    protected abstract Request buildRequest(RequestBody requestBody);

    public RequestCall build() {
        return new RequestCall(this);
    }

    public Request generateRequest(Callback callback) {
        RequestBody requestBody = buildRequestBody();
        RequestBody wrappedRequestBody = wrapRequestBody(requestBody, callback);
        return buildRequest(wrappedRequestBody);
    }

    protected void appendHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (mHeaders == null || mHeaders.isEmpty()) return;

        for (String key : mHeaders.keySet()) {
            headerBuilder.add(key, mHeaders.get(key));
        }
        mBuilder.headers(headerBuilder.build());
    }

    public int getId() {
        return mID;
    }
}
