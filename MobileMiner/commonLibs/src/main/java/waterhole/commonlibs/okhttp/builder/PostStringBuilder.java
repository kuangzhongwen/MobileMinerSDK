package waterhole.commonlibs.okhttp.builder;

import okhttp3.MediaType;
import waterhole.commonlibs.okhttp.request.PostStringRequest;
import waterhole.commonlibs.okhttp.request.RequestCall;

/**
 * Post文本建造者
 *
 * @author kzw on 2017/07/31.
 */
public class PostStringBuilder extends OkHttpRequestBuilder<PostStringBuilder> {

    private MediaType mMediaType;

    private String mContent;

    public PostStringBuilder content(String content) {
        mContent = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType) {
        mMediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostStringRequest(mUrl, mTag, mParams, mHeaders, mContent, mMediaType, mID).build();
    }
}
