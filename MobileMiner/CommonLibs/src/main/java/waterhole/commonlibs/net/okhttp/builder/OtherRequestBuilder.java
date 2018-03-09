package waterhole.commonlibs.net.okhttp.builder;

import okhttp3.RequestBody;
import waterhole.commonlibs.net.okhttp.request.OtherRequest;
import waterhole.commonlibs.net.okhttp.request.RequestCall;

/**
 * DELETE、PUT、PATCH等其他方法建造者
 *
 * @author kzw on 2017/07/31.
 */
public class OtherRequestBuilder extends OkHttpRequestBuilder<OtherRequestBuilder> {

    private RequestBody mRequestBody;

    private String mMethod;
    private String mContent;

    public OtherRequestBuilder(String method) {
        mMethod = method;
    }

    @Override
    public RequestCall build() {
        return new OtherRequest(mRequestBody, mContent, mMethod, mUrl, mTag, mParams, mHeaders, mID).build();
    }

    public OtherRequestBuilder requestBody(RequestBody requestBody) {
        mRequestBody = requestBody;
        return this;
    }

    public OtherRequestBuilder requestBody(String content) {
        mContent = content;
        return this;
    }
}
