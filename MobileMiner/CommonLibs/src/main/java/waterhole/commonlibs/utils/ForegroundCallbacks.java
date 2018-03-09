package waterhole.commonlibs.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static waterhole.commonlibs.utils.LogUtils.error;
import static waterhole.commonlibs.utils.LogUtils.info;

/**
 * Activity生命周期监听回调类，android 14以上使用.
 *
 * @author kzw on 2018/01/05.
 */
@TargetApi(14)
public final class ForegroundCallbacks implements Application.ActivityLifecycleCallbacks {

    public static final String TAG = "ForegroundCallbacks";

    private static final long CHECK_DELAY = 500L;

    public interface Listener {
        // 回到前台
        void onBecameForeground();

        // 回到后台
        void onBecameBackground();
    }

    private static ForegroundCallbacks mInstance;

    private final List<Listener> listeners = new CopyOnWriteArrayList<>();
    private final Handler mHandler = new Handler();
    private Runnable mCheckTask;

    private boolean isForeground;
    private boolean isPaused = true;

    // 是否忽略后台回到前台监听，比如在一些特殊情况下，如打开第三方应用
    private boolean isIngoreCallback;

    public static ForegroundCallbacks init(Application application) {
        if (mInstance == null) {
            mInstance = new ForegroundCallbacks();
            application.registerActivityLifecycleCallbacks(mInstance);
        }
        return mInstance;
    }

    public static ForegroundCallbacks get(Application application) {
        if (mInstance == null) {
            init(application);
        }
        return mInstance;
    }

    public static ForegroundCallbacks get(Context ctx) {
        if (mInstance == null) {
            Context appCtx = ctx.getApplicationContext();
            if (appCtx instanceof Application) {
                init((Application) appCtx);
            }
            info(TAG, "Foreground is not initialised and cannot obtain " +
                    "the Application object");
        }
        return mInstance;
    }

    public static ForegroundCallbacks get() {
        if (mInstance == null) {
            info(TAG, "Foreground is not initialised - invoke at least once with " +
                    "parameterised init/get");
        }
        return mInstance;
    }

    public boolean isForeground() {
        return isForeground;
    }

    public boolean isBackground() {
        return !isForeground;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void setIngoreCallback(boolean ingoreCallback) {
        isIngoreCallback = ingoreCallback;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        isPaused = false;
        boolean wasBackground = !isForeground;
        isForeground = true;
        if (mCheckTask != null) {
            mHandler.removeCallbacks(mCheckTask);
        }
        if (wasBackground) {
            info(TAG, "went foreground");
            for (Listener l : listeners) {
                try {
                    if (!isIngoreCallback) {
                        l.onBecameForeground();
                    }
                } catch (Exception exc) {
                    error(TAG, "Listener throw exception!:" + exc.toString());
                }
            }
        } else {
            info(TAG, "still foreground");
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        isPaused = true;
        if (mCheckTask != null) {
            mHandler.removeCallbacks(mCheckTask);
        }
        mHandler.postDelayed(mCheckTask = new Runnable() {
            @Override
            public void run() {
                if (isForeground && isPaused) {
                    isForeground = false;
                    info(TAG, "went background");
                    for (Listener l : listeners) {
                        try {
                            if (!isIngoreCallback) {
                                l.onBecameBackground();
                            }
                        } catch (Exception exc) {
                            error(TAG, "Listener throw exception!:" + exc.toString());
                        }
                    }
                } else {
                    info(TAG, "still foreground");
                }
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}