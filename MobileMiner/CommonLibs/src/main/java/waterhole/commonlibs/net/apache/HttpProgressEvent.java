package waterhole.commonlibs.net.apache;

/**
 * Http传输进度事件，配合EventBus使用.
 *
 * @author kzw on 2017/11/08.
 */
public final class HttpProgressEvent {

    // 传输进度
    public int progress;

    // 请求url
    public String url;

    // 本地路径
    public String path;
}
