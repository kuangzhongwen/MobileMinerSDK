package waterhole.commonlibs.okhttp.request;

import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import waterhole.commonlibs.okhttp.OkHttpUtils;
import waterhole.commonlibs.okhttp.builder.PostFormBuilder.FileInput;
import waterhole.commonlibs.okhttp.callback.Callback;
import waterhole.commonlibs.utils.LogUtils;

/**
 * Post表单请求
 *
 * @author kzw on 2017/07/31.
 */
public class PostFormRequest extends OkHttpRequest {

    private List<FileInput> mFiles;

    public PostFormRequest(String url, Object tag, Map<String, String> params,
                           Map<String, String> headers, List<FileInput> files, int id) {
        super(url, tag, params, headers, id);
        mFiles = files;
    }

    @Override
    protected RequestBody buildRequestBody() {
        if (mFiles == null || mFiles.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            addParams(builder);
            FormBody formBody = builder.build();
            return formBody;
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            addParams(builder);
            for (int i = 0; i < mFiles.size(); i++) {
                FileInput fileInput = mFiles.get(i);
                RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file);
                builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
            }
            return builder.build();
        }
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

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LogUtils.printStackTrace(e);
        }
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void addParams(MultipartBody.Builder builder) {
        if (mParams != null && !mParams.isEmpty()) {
            for (String key : mParams.keySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, mParams.get(key)));
            }
        }
    }

    private void addParams(FormBody.Builder builder) {
        if (mParams != null) {
            for (String key : mParams.keySet()) {
                builder.add(key, mParams.get(key));
            }
        }
    }
}
