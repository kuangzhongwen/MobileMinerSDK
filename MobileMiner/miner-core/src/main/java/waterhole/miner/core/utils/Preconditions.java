package waterhole.miner.core.utils;

import android.os.Looper;

/**
 * 先决条件校验，一般配合注解使用，如@Nullable, @NonNull.
 *
 * @author kzw on 2017/07/17.
 */
public final class Preconditions {

    private Preconditions() {
        throw new AssertionError();
    }

    /**
     * 检测一个对象是否为null，如果为null，则抛出异常
     *
     * @param instance 对象
     * @param <T>      对象泛型
     */
    public static <T> T checkNotNull(T instance) {
        return checkNotNull(instance, "Object");
    }

    /**
     * 检测一个对象是否为null，如果为null，则抛出异常
     *
     * @param instance 对象
     * @param name     异常描述文字
     * @param <T>      对象泛型
     */
    public static <T> T checkNotNull(T instance, String name) {
        if (instance == null) {
            throw new NullPointerException(name + " must not be null");
        } else {
            return instance;
        }
    }

    /**
     * 检测是否在主线程
     */
    public static void checkOnMainThread() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new RuntimeException("please invoke the method in main thread");
        }
    }

    /**
     * 检测是否在子线程
     */
    public static void checkOnChildThread() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            throw new RuntimeException("please invoke the method in child thread");
        }
    }
}
