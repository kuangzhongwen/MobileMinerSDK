package waterhole.commonlibs.net.apache;

/**
 * 简单的{@link waterhole.commonlibs.net.apache.HttpUploadListener}（适配器）
 * 当希望选择回调HttpUploadListener的部分方法时，可以传入该类型。
 *
 * @author kzw on 2017/07/14.
 */
public class SimpleHttpUploadListener implements HttpUploadListener {

    @Override
    public void onUploadSuccess() {
    }

    @Override
    public String onUploadFail() {
        return null;
    }

    @Override
    public String onUploadFail(int errorCode) {
        return null;
    }
}
