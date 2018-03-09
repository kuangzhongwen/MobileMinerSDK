package waterhole.commonlibs.utils;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

/**
 * 线程工具
 *
 * @author kzw on 2017/07/04.
 */
public final class ThreadUtils {

    private ThreadUtils() {
    }

    /**
     * 为了防止使用线程时不给线程起名字，写此静态函数.目的是当发生线程泄漏后能够快速定位问题.
     *
     * @param context 上下文
     * @param r       Runnable
     * @param name    线程名
     * @return Thread
     */
    public static Thread newThread(Context context, Runnable r, String name) {
        if (context == null) {
            throw new RuntimeException("context should not be null");
        }
        if (TextUtils.isEmpty(name)) {
            throw new RuntimeException("thread name should not be empty");
        }
        return new Thread(r, context.getPackageName() + name);
    }

    /**
     * 在主线程休眠
     */
    public static void sleepOnMainThread(long sleepTime) {
        sleepOnThread(true, sleepTime);
    }

    /**
     * 在线程休眠
     */
    public static void sleepOnThread(boolean onMainThread, long sleepTime) {
        if (sleepTime <= 0) {
            return;
        }
        if (onMainThread && Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            LogUtils.printStackTrace(e);
        }
    }

    /**
     * 获取可执行的核心线程数
     */
    public static int getCoreThreadSize() {
        int coreThreadSize = Runtime.getRuntime().availableProcessors();
        coreThreadSize = coreThreadSize <= 1 ? 2 : (coreThreadSize <= 2 ? 3 : coreThreadSize);
        return coreThreadSize;
    }
}
