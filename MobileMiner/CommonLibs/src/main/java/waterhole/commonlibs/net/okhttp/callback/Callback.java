package waterhole.commonlibs.net.okhttp.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 回调抽象类
 *
 * @author kzw on 2017/07/31.
 */
public abstract class Callback<T> {

    /**
     * UI Thread
     */
    public void onBefore(Request request, int id) {
    }

    /**
     * UI Thread
     */
    public void onAfter(int id) {
    }

    /**
     * UI Thread
     */
    public void inProgress(float progress, long total, int id) {

    }

    /**
     * if you parse reponse code in parseNetworkResponse, you should make this method return true.
     */
    public boolean validateReponse(Response response, int id) {
        return response.isSuccessful();
    }

    /**
     * Thread Pool Thread
     */
    public abstract T parseNetworkResponse(Response response, int id) throws Exception;

    public abstract void onError(Call call, Exception e, int id);

    public abstract void onResponse(T response, int id);

    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public Object parseNetworkResponse(Response response, int id) throws Exception {
            return null;
        }

        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(Object response, int id) {

        }
    };
}