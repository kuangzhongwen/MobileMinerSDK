package waterhole.miner.core.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import waterhole.miner.core.analytics.AnalyticsWrapper;

/**
 * 日志打印工具
 *
 * @author kzw on 2017/07/10.
 */
public final class LogUtils {

    private static final String TAG = "WaterholeMinerSDK";

    public static boolean enableLog = true;

    public LogUtils() {
        throw new RuntimeException("LogUtils stub!");
    }

    public static void info(String msg) {
        if (enableLog && !TextUtils.isEmpty(msg)) {
            Log.i(TAG, msg);
        }
    }

    public static void debug(String msg) {
        if (enableLog && !TextUtils.isEmpty(msg)) {
            Log.d(TAG, msg);
        }
    }

    public static void error(String msg) {
        if (enableLog && !TextUtils.isEmpty(msg)) {
            Log.e(TAG, msg);
        }
    }

    public static void errorWithReport(Context context, String msg) {
        if (context != null && !TextUtils.isEmpty(msg)) {
            if (enableLog) {
                Log.e(TAG, msg);
            }
            AnalyticsWrapper.onErrorEvent(context, msg);
        }
    }

    public static void error(String msg, Throwable tr) {
        if (enableLog && !TextUtils.isEmpty(msg) && tr != null) {
            Log.e(TAG, msg, tr);
        }
    }

    public static void printStackTrace(Throwable t) {
        if (enableLog && t != null) {
            t.printStackTrace();
            error(t.getMessage(), t);
        }
    }
}
