package waterhole.commonlibs.acache;

import android.support.annotation.UiThread;

/**
 * 简单的{@link CacheCallback}（适配器）
 * 当希望选择回调CacheCallback的部分方法时，可以传入该类型.
 *
 * @author kzw on 2017/08/02.
 */
public class SimpleCacheCallback<T> implements CacheCallback<T> {

    @Override
    @UiThread
    public void runResultOnMainThread(T t) {
    }

    @Override
    @UiThread
    public void runFailOnMainThread() {
    }
}
