package waterhole.commonlibs;

import android.app.Application;
import android.content.Context;

import java.io.ObjectStreamException;

/**
 * 子模块的Context，由于子Library不能拥有context，所以由外部注入进来。
 * <a>不要尝试给子module自己注册application.</a>
 *
 * @author kzw on 2017/07/08.
 */
public final class ContextWrapper {

    // 上下文对象，由于Application是跟着app生命周期走的，所以不会因为被单例持有造成内存泄漏
    private Context context;

    private ContextWrapper() {
    }

    private static final class ContextHolder {
        static ContextWrapper instance = new ContextWrapper();
    }

    private Object readResolve() throws ObjectStreamException {
        return getInstance();
    }

    public static ContextWrapper getInstance() {
        return ContextHolder.instance;
    }

    /**
     * 注入上下文对象，类型必须为Application，否则抛出异常
     *
     * @param context 上下文对象
     */
    public void injectContext(Context context) {
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("Inject context must be Application");
        }
        this.context = context;
    }

    /**
     * @return 上下文对象
     */
    public Context obtainContext() {
        if (context == null) {
            throw new RuntimeException("Please invoke IMContextWrapper.getInstance()"
                    + ".injectContext(Context context) first");
        }
        return context;
    }
}
