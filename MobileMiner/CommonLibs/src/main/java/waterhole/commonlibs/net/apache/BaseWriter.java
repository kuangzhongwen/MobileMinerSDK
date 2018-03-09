package waterhole.commonlibs.net.apache;

/**
 * 抽象写入
 *
 * @author kzw on 2016/07/07.
 */
public abstract class BaseWriter<T> {

    protected final T dest;

    protected final byte[] byteIn;

    protected BaseWriter(T dest, byte... byteIn) {
        this.dest = dest;
        this.byteIn = byteIn;
    }

    public abstract void write();
}
