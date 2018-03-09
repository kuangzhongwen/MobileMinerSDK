package waterhole.commonlibs.okhttp.request;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Post文本请求
 *
 * @author kzw on 2017/07/31.
 */
public class PostStringRequest extends OkHttpRequest {

    // MediaTypePlain
    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");

    private MediaType mMediaType;

    private String mContent;

    public PostStringRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers,
                             String content, MediaType mediaType, int id) {
        super(url, tag, params, headers, id);
        mContent = content;
        mMediaType = mediaType;

        if (mContent == null) {
            throw new IllegalArgumentException("the content can not be null !");
        }
        // 赋默认值
        if (mMediaType == null) {
            mMediaType = MEDIA_TYPE_PLAIN;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mMediaType, mContent);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return mBuilder.post(requestBody).build();
    }
}
