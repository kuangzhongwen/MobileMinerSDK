package waterhole.commonlibs.net.apache;

/**
 * 简单的{@link HttpProgressListener}（适配器）
 * 当希望选择回调HttpProgressListener的部分方法时，可以传入该类型。
 *
 * @author kzw on 2017/07/14.
 */
public class SimpleHttpProgressListener implements HttpProgressListener {

    @Override
    public void transferred(long transferedBytes) {
    }

    @Override
    public void transferredPercent(int percent) {
    }
}
