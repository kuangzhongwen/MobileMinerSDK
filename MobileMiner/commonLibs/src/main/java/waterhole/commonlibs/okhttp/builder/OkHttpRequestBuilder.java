package waterhole.commonlibs.okhttp.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import waterhole.commonlibs.okhttp.request.RequestCall;

/**
 * OkHttp抽象建造者
 *
 * @author kzw on 2017/07/31.
 */
public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder> {

    protected String mUrl;
    protected Object mTag;
    protected int mID;

    protected Map<String, String> mHeaders;
    protected Map<String, String> mParams;

    @SuppressWarnings("unchecked")
    public T id(int id) {
        mID = id;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T url(String url) {
        mUrl = url;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T tag(Object tag) {
        mTag = tag;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T headers(Map<String, String> headers) {
        mHeaders = headers;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T addHeader(String key, String val) {
        if (mHeaders == null) {
            mHeaders = new LinkedHashMap<>();
        }
        mHeaders.put(key, val);
        return (T) this;
    }

    public abstract RequestCall build();
}
