package waterhole.miner.core.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * 日志打印工具
 *
 * @author kzw on 2017/07/10.
 */
public final class LogUtils {

    private static final String TAG = "WaterholeMinerSDK";

    public LogUtils() {
        throw new RuntimeException("LogUtils stub!");
    }

    public static void info(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Log.i(TAG, msg);
        }
    }

    public static void debug(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Log.d(TAG, msg);
        }
    }

    public static void error(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Log.e(TAG, msg);
        }
    }

    public static void error(String msg, Throwable tr) {
        if (!TextUtils.isEmpty(msg) && tr != null) {
            Log.e(TAG, msg, tr);
        }
    }

    public static void printStackTrace(Throwable t) {
        if (t != null) {
            t.printStackTrace();
            error(t.getMessage(), t);
        }
    }
}
