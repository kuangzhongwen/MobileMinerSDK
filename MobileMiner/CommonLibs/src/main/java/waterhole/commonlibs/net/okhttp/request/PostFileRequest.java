package waterhole.commonlibs.net.okhttp.request;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import waterhole.commonlibs.net.okhttp.OkHttpUtils;
import waterhole.commonlibs.net.okhttp.callback.Callback;

/**
 * Post文件请求
 *
 * @author kzw on 2017/07/31.
 */
public class PostFileRequest extends OkHttpRequest {

    private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    private File mFile;
    private MediaType mMediaType;

    public PostFileRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers,
                           File file, MediaType mediaType, int id) {
        super(url, tag, params, headers, id);
        mFile = file;
        mMediaType = mediaType;

        if (mFile == null) {
            throw new IllegalArgumentException("the file can not be null !");
        }
        if (mMediaType == null) {
            mMediaType = MEDIA_TYPE_STREAM;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mMediaType, mFile);
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody, final Callback callback) {
        if (callback == null) return requestBody;
        CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody,
                new CountingRequestBody.Listener() {
                    @Override
                    public void onRequestProgress(final long bytesWritten, final long contentLength) {
                        OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.inProgress(bytesWritten * 1.0f / contentLength, contentLength, mID);
                            }
                        });

                    }
                });
        return countingRequestBody;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return mBuilder.post(requestBody).build();
    }
}
