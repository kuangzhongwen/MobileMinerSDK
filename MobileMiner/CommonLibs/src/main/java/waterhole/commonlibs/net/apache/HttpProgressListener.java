package waterhole.commonlibs.net.apache;

/**
 * Http传输进度监听
 *
 * @author kzw on 2015/12/16.
 */
public interface HttpProgressListener {

    // 传输的字节
    void transferred(long bytes);

    // 当前的进度
    void transferredPercent(int percent);
}
