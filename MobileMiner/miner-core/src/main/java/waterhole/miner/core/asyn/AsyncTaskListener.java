package waterhole.miner.core.asyn;
/**
 * 异步执行回调监听.
 *
 * @author kzw on 2017/10/14.
 */
public interface AsyncTaskListener<T> {

    /**
     * 执行完毕.
     */
    void runComplete(T t);
}
