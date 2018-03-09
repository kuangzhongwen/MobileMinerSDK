package waterhole.commonlibs.net.okhttp.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * 文本回调
 *
 * @author kzw on 2017/07/31.
 */
public abstract class StringCallback extends Callback<String> {

    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException {
        return response.body().string();
    }
}
