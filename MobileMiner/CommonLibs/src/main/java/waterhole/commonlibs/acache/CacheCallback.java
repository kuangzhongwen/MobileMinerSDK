package waterhole.commonlibs.acache;

import android.support.annotation.UiThread;

/**
 * 缓存回调，缓存读取或失败都切换到主线程进行回调.
 *
 * @author kzw on 2017/08/02.
 */
public interface CacheCallback<T> {

    /**
     * 主线程回调缓存对象
     *
     * @param t 缓存对象
     */
    @UiThread
    void runResultOnMainThread(T t);

    /**
     * 主线程回调缓存失败
     */
    @UiThread
    void runFailOnMainThread();
}
