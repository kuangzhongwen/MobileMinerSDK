package waterhole.commonlibs.structure;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂，定义好线程名，非后台线程
 *
 * @author kzw on 2017/1/17.
 */
public final class DefaultThreadFactory implements ThreadFactory {

    // 线程池数量
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    // 线程数，保持并发下的原子性
    private final AtomicInteger mThreadNumber = new AtomicInteger(1);
    // 线程组
    private final ThreadGroup mThreadGroup;

    // 线程名前缀
    private final String mNamePrefix;
    // 线程优先级
    private final int mThreadPriority;

    public DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
        mThreadPriority = threadPriority;
        SecurityManager s = System.getSecurityManager();
        mThreadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        mNamePrefix = threadNamePrefix + POOL_NUMBER.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(final Runnable r) {
        Thread t = new Thread(mThreadGroup, r, mNamePrefix + mThreadNumber.getAndIncrement(), 0);

        // 设置为非后台线程
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        t.setPriority(mThreadPriority);
        return t;
    }
}
