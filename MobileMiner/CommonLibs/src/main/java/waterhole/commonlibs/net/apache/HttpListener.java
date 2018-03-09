package waterhole.commonlibs.net.apache;

/**
 * @author liuyan on 2016/03/08.
 */
public interface HttpListener {

    void onSuccess(String response);

    void onFail();

    void onTimeOut();
}
