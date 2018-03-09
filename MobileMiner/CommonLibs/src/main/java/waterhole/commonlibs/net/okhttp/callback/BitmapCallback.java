package waterhole.commonlibs.net.okhttp.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.Response;

/**
 * 图片回调
 *
 * @author kzw on 2017/07/31.
 */
public abstract class BitmapCallback extends Callback<Bitmap> {

    @Override
    public Bitmap parseNetworkResponse(Response response, int id) throws Exception {
        return BitmapFactory.decodeStream(response.body().byteStream());
    }
}
