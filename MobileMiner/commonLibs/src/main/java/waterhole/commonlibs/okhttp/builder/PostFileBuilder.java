package waterhole.commonlibs.okhttp.builder;

import java.io.File;

import okhttp3.MediaType;
import waterhole.commonlibs.okhttp.request.PostFileRequest;
import waterhole.commonlibs.okhttp.request.RequestCall;

/**
 * Post文件建造者
 *
 * @author kzw on 2017/07/31.
 */
public class PostFileBuilder extends OkHttpRequestBuilder<PostFileBuilder> {

    private File mFile;

    private MediaType mMediaType;

    public OkHttpRequestBuilder file(File file) {
        mFile = file;
        return this;
    }

    public OkHttpRequestBuilder mediaType(MediaType mediaType) {
        mMediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostFileRequest(mUrl, mTag, mParams, mHeaders, mFile, mMediaType, mID).build();
    }
}
