package waterhole.commonlibs.net.apache;

/**
 * Http上传监听.
 *
 * @author kzw on 2015/12/21.
 */
public interface HttpUploadListener {

    void onUploadSuccess();

    String onUploadFail();

    String onUploadFail(int errorCode);
}
