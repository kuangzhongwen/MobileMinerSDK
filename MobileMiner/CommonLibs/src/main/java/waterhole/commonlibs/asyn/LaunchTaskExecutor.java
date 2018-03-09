package waterhole.commonlibs.asyn;

import java.util.LinkedList;

/**
 * 随app启动一起执行的异步任务调度器。<br>
 * <p>
 * app启动可能会有很多任务需要异步线程执行。为了减少对app启动速度的影响（减小启动过程中cpu消耗），
 * 一种方案是采用延时方式。但延时方式有个弊端，不同性能的设备延时多少也不合适。
 * 另外第一次安装，有引导界面，第一次启动速度也慢，导致延时时间相对较短，在app还没有初始化完毕，这些异步线程
 * 在预期之前执行，过多占用cpu。
 * <p>
 * 为此我们设计该类，app初始化完毕后才开始执行这些异步任务，而不用考虑到底延时多少的问题。<br>
 * <p>
 * app 需要在合适的地方调用 {@link #appReady(boolean)}，此时会安排队列中的任务开始顺序执行。
 * 如果app不调用 {@link #appReady(boolean)}, 则默认 {@link #GUARANTEE_DELAY_MS} 毫秒以后执行。
 *
 * @author kzw on 2017/07/01
 */
public final class LaunchTaskExecutor {

    /**
     * 排队等候需要执行的消息队列。
     */
    private static LinkedList<Task> sQueue = new LinkedList<>();

    /**
     * app 是否已经初始化完毕，完毕后才安排执行 {@link #sQueue} 中的任务。
     */
    private static boolean sAppReady = false;

    /**
     * 为了防止app忘记调用 {@link #appReady(boolean)}, 而导致任务执行。我们建立一个保证任务。
     * 30秒以后如果app没有{@link #appReady(boolean)}, 我们自动执行。
     */
    private static final long GUARANTEE_DELAY_MS = 30 * 1000;

    /**
     * {@link #sGuaranteeRunnable} 只需要执行一次，用来标记是否已经安排执行。
     */
    private static boolean sGuaranteeRunnableScheduled = false;

    /**
     * @see #GUARANTEE_DELAY_MS
     */
    private static Runnable sGuaranteeRunnable = new Runnable() {
        public void run() {
            appReady(true);
        }
    };


    /**
     * 工具类，私有化构造函数。
     */
    private LaunchTaskExecutor() {
    }

    /**
     * 内部使用参数类。用作执行任务的参数。
     */
    private static class Task {

        /**
         * 所具体执行的任务。
         */
        Runnable runnable;
        /**
         * 任务名字表示
         */
        String name;
        /**
         * 任务延时
         */
        long delay = 0L;
    }


    /**
     * 标记app初始化完毕，能够在不影响自身启动（抢占cpu资源）的前提下执行异步任务。
     * 但是还有一个情况。当app只是推出Activity，但是进程没有杀死。
     * 等一下次Activity进入的时候静态变量标记为已经ready，任务就会立刻执行。所以还是有问题的。
     * <p>
     * 这时候需要一个 notready，把状态给位 notready。 随后等初始化完毕后再次标记为 ready。
     * <p>
     * 建议在 Activity onCreate最开始 标记为 notready。
     *
     * @param readyOrNot 参考函数说明。
     */
    public static synchronized void appReady(boolean readyOrNot) {
        if (!readyOrNot) {
            sAppReady = false;
            sGuaranteeRunnableScheduled = false;
            return;
        }

        if (sAppReady) {
            return;
        }

        sAppReady = true;

        // 执行等待队列中的任务。
        while (true) {
            Task task = sQueue.poll();

            if (task != null && task.runnable != null) {
                // 将 执行任务放到 AsyncTaskAssistant 的执行队列中，按顺序执行。
                if (task.delay > 0L) {
                    AsyncTaskAssistant.execute(task.runnable, task.delay);
                } else {
                    AsyncTaskAssistant.execute(task.runnable);
                }
            } else {
                // 队列为空，中断循环。
                break;
            }
        }
    }

    /**
     * 获取初始化完毕标记值
     *
     * @see #appReady(boolean)
     */
    public static synchronized boolean isAppReady() {
        return sAppReady;
    }

    /**
     * 执行随 app 启动的异步任务，如果app还没有初始化完毕{@link #appReady(boolean)}，任务暂时直接放到等待队列。
     * 如果app已经初始化完毕，则直接安排通过 {@link AsyncTaskAssistant#execute(Runnable)} 执行，顺序执行。
     *
     * @param runnable 需要执行的 runnable 任务
     * @param taskName 任务名字，目前只用来调试，log输出作用。
     */
    public static synchronized void execute(Runnable runnable, String taskName) {
        execute(runnable, taskName, 0L);
    }

    /**
     * 执行随 app 启动的异步任务，如果app还没有初始化完毕{@link #appReady(boolean)}，任务暂时直接放到等待队列。
     * 如果app已经初始化完毕，则直接安排通过 {@link AsyncTaskAssistant#execute(Runnable)} 执行，顺序执行。
     *
     * @param runnable 需要执行的 runnable 任务
     * @param taskName 任务名字，目前只用来调试，log输出作用。
     * @param delay    任务延迟时间（毫秒）
     */
    public static synchronized void execute(Runnable runnable, String taskName, long delay) {
        if (sAppReady) {
            if (delay > 0L) {
                AsyncTaskAssistant.execute(runnable, delay);
            } else {
                AsyncTaskAssistant.execute(runnable);
            }
        } else {
            Task task = new Task();
            task.runnable = runnable;
            task.name = taskName;
            task.delay = delay;
            sQueue.add(task);
            
            /*
             * 为了防止app忘记调用 appReady() 函数，我们设计一个最后保证执行的任务。
             */
            if (!sGuaranteeRunnableScheduled) {
                sGuaranteeRunnableScheduled = true;

                AsyncTaskAssistant.execute(sGuaranteeRunnable, GUARANTEE_DELAY_MS);
            }
        }
    }
}
