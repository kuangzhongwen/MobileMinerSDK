package waterhole.commonlibs.okhttp.request;

import android.text.TextUtils;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;
import waterhole.commonlibs.okhttp.OkHttpUtils;

/**
 * 其他类型请求，如PUT, DELETE, HEAD, PATCH
 *
 * @author kzw on 2017/07/31.
 */
public class OtherRequest extends OkHttpRequest {

    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");

    private RequestBody mRequestBody;
    private String mMethod;
    private String mContent;

    public OtherRequest(RequestBody requestBody, String content, String method, String url,
                        Object tag, Map<String, String> params, Map<String, String> headers, int id) {
        super(url, tag, params, headers, id);
        mRequestBody = requestBody;
        mMethod = method;
        mContent = content;
    }

    @Override
    protected RequestBody buildRequestBody() {
        if (mRequestBody == null && TextUtils.isEmpty(mContent) && HttpMethod.requiresRequestBody(mMethod)) {
            throw new IllegalArgumentException("requestBody and content can not be null in method:" + mMethod);
        }
        if (mRequestBody == null && !TextUtils.isEmpty(mContent)) {
            mRequestBody = RequestBody.create(MEDIA_TYPE_PLAIN, mContent);
        }
        return mRequestBody;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        if (mMethod.equals(OkHttpUtils.METHOD.PUT)) {
            mBuilder.put(requestBody);
        } else if (mMethod.equals(OkHttpUtils.METHOD.DELETE)) {
            if (requestBody == null) {
                mBuilder.delete();
            } else {
                mBuilder.delete(requestBody);
            }
        } else if (mMethod.equals(OkHttpUtils.METHOD.HEAD)) {
            mBuilder.head();
        } else if (mMethod.equals(OkHttpUtils.METHOD.PATCH)) {
            mBuilder.patch(requestBody);
        }
        return mBuilder.build();
    }
}
