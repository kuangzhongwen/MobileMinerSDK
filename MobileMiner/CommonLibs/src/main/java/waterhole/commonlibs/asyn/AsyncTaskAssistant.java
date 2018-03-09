package waterhole.commonlibs.asyn;

import android.os.Build;
import android.os.Handler;
import android.os.Process;

import java.util.LinkedList;
import java.util.concurrent.Executor;

import waterhole.commonlibs.utils.APIUtils;

/**
 * {@link AsyncTask} 类的辅助工具类，方便直接执行 {@link Runnable} 任务。<br>
 * 并且支持延时  {@link Runnable} 任务，内部通过 {@link Handler#postDelayed(Runnable, long)} 实现，
 * 参考 {@link #execute(Runnable, long)} 函数，执行前由于用到  {@link Handler}对象，所以需要提前在UI线程
 * 调用 {@link #init()}函数，进行初始化操作。<br><br>
 * <p>
 * 从{@link Build.VERSION_CODES#HONEYCOMB} 开始，{@link AsyncTask#execute(Object...)} 函数
 * 执行的任务按顺序执行，也就是一个任务执行完了才会执行下一个任务。如果想同时在线程池执行，需要调用
 * {@link AsyncTask#executeOnExecutor(Executor, Object...)} 函数。<br><br>
 * <p>
 * 为了隐藏这些细节，这个类的 {@link #execute(Runnable)} 函数也是按顺序执行，只不过和android版本没有关系。<br>
 * 如果想在线程池执行，请调用 {@link #executeOnThreadPool(Runnable)} 相关函数。
 *
 * @author kzw on 2017/07/01.
 */
public final class AsyncTaskAssistant {

    /**
     * 需要在UI线程初始化，调用{@link #init()}函数。
     */
    private static Handler sHandler;

    /**
     * 一个 {@link Executor} ，按照顺序执行任务，一个任务执行完以后才执行下一个。
     * 一个进程中共用一个。
     */
    private static final Executor SERIAL_EXECUTOR = new SerialExecutor();

    /**
     * 默认的 {@link Executor}，默认为 {@link #SERIAL_EXECUTOR}
     */
    private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;

    /**
     * 内部使用参数类。用作执行任务的参数。
     */
    private static class Task {

        /**
         * 所具体执行的任务。
         */
        Runnable runnable;
    }

    /**
     * @see {@link AsyncTaskAssistant#SERIAL_EXECUTOR}
     */
    private static class SerialExecutor implements Executor {

        /**
         * 任务队列。
         */
        final LinkedList<Runnable> mTasks = new LinkedList<>();

        /**
         * 当前正在执行的任务。
         */
        Runnable mActive;

        @Override
        public synchronized void execute(final Runnable r) {
            mTasks.add(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        /**
         * 执行下一个任务。
         */
        synchronized void scheduleNext() {
            mActive = mTasks.poll();

            if (mActive != null) {
                executeOnThreadPool(mActive);
            }
        }
    }

    /**
     * 用来实际执行任务的 {@link AsyncTask}.
     */
    private static class WorkerAsyncTask extends AsyncTask<Task, Object, Object> {

        @Override
        protected Object doInBackground(Task... params) {
            if (params[0] != null && params[0].runnable != null) {

                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Runnable task = params[0].runnable;
                task.run();
            }

            return null;
        }

    }

    /**
     * Utility class use a private constructor.
     */
    private AsyncTaskAssistant() {
    }

    /**
     * 调用延时任务{@link #execute(Runnable, long)}之前，需要在UI线程中调用此函数，进行初始化操作。
     */
    public static void init() {
        if (sHandler == null) {
            sHandler = new Handler();
        }
    }

    /**
     * 一个在 {@link AsyncTask} 中执行 {@link Runnable} 任务的快捷方式。
     * 任务按顺序执行，一个任务执行完后，才执行下一个任务。
     *
     * @param runnable 要执行的任务
     * @see #executeOnThreadPool(Runnable)
     */
    public static void execute(Runnable runnable) {
        sDefaultExecutor.execute(runnable);
    }

    /**
     * 延时执行{@link Runnable} 任务. 调用之前需要在UI线程调用 {@link #init()}
     *
     * @param runnable    要执行的任务。
     * @param delayTimeMS 需要延时的时间，单位毫秒。
     * @see #execute(Runnable)
     */
    public static void execute(final Runnable runnable, long delayTimeMS) {
        if (sHandler == null) {
            init();
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                execute(runnable);
            }
        };

        sHandler.postDelayed(r, delayTimeMS);
    }

    private static void executeOnThreadPoolInternal(Runnable runnable) {
        Task task = new Task();
        task.runnable = runnable;
        
        /*
         * 从 apilevel 11 开始，默认为顺序执行，之前是在线程池执行。当然对刚开始只有一个线程执行。从1.6开始
         * 使用线程池执行。
         */
        WorkerAsyncTask asyncTask = new WorkerAsyncTask();
        if (APIUtils.hasHoneycomb()) {
            asyncTask.executeOnExecutor(WorkerAsyncTask.THREAD_POOL_EXECUTOR, task);
        } else {
            asyncTask.execute(task);
        }
    }

    /**
     * 一个在 {@link AsyncTask} 中执行 {@link Runnable} 任务的快捷方式。
     * 执行顺序无法保证，在线程池执行。
     *
     * @param runnable 要执行的任务
     * @see AsyncTask#executeOnExecutor(Executor, Object...)
     */
    public static void executeOnThreadPool(Runnable runnable) {
        executeOnThreadPool(runnable, 0);
    }

    /**
     * 一个延时在 {@link AsyncTask} 中执行 {@link Runnable} 任务的快捷方式。
     * 执行顺序无法保证，在线程池执行。
     * 调用之前需要在UI线程调用 {@link #init()}
     *
     * @param runnable    要执行的任务
     * @param delayTimeMS 需要延时的时间，单位毫秒。
     * @see AsyncTask#executeOnExecutor(Executor, Object...)
     */
    public static void executeOnThreadPool(final Runnable runnable, long delayTimeMS) {
        if (sHandler == null) {
            init();
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                executeOnThreadPoolInternal(runnable);
            }
        };
        sHandler.postDelayed(r, delayTimeMS);
    }
}
