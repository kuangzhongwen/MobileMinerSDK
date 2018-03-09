package waterhole.commonlibs.okhttp.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Get请求
 *
 * @author kzw on 2017/07/31.
 */
public class GetRequest extends OkHttpRequest {

    public GetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id) {
        super(url, tag, params, headers, id);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return mBuilder.get().build();
    }
}
