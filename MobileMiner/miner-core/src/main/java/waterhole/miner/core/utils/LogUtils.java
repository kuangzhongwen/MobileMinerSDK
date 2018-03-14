package waterhole.miner.core.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * 日志打印工具
 *
 * @author kzw on 2017/07/10.
 */
public final class LogUtils {

    private static String mTag = "LogUtils";

    private static boolean mLogDebug;

    public LogUtils() {
        throw new RuntimeException("LogUtils stub!");
    }

    /**
     * 注入上下文对象
     *
     * @param context 上下文对象
     */
    public static void injectContext(Context context) {
        mTag = context.getPackageName();
    }

    /**
     * 是否能够打印日志
     *
     * @param debug true | false
     */
    public static void enableDebug(boolean debug) {
        mLogDebug = debug;
    }

    public static void info(String tag, String msg) {
        if (!mLogDebug || TextUtils.isEmpty(msg)) {
            return;
        }
        if (!TextUtils.isEmpty(tag)) {
            Log.i(tag, msg);
        } else {
            Log.i(mTag, msg);
        }
    }

    public static void debug(String tag, String msg) {
        if (!mLogDebug || TextUtils.isEmpty(msg)) {
            return;
        }
        if (!TextUtils.isEmpty(tag)) {
            Log.d(tag, msg);
        } else {
            Log.d(mTag, msg);
        }
    }

    public static void error(String tag, String msg) {
        if (!mLogDebug || TextUtils.isEmpty(msg)) {
            return;
        }
        if (!TextUtils.isEmpty(tag)) {
            Log.e(tag, msg);
        } else {
            Log.e(mTag, msg);
        }
    }

    public static void error(String tag, String msg, Throwable tr) {
        if (!mLogDebug || TextUtils.isEmpty(msg) || tr == null) {
            return;
        }
        if (!TextUtils.isEmpty(tag)) {
            Log.e(tag, msg, tr);
        } else {
            Log.e(mTag, msg, tr);
        }
    }

    public static void printStackTrace(Throwable t) {
        if (!mLogDebug || t == null) {
            return;
        }
        t.printStackTrace();
        error(mTag, t.getMessage());
    }
}
