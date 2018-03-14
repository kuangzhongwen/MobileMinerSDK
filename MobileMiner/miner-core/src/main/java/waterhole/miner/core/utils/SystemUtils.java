package waterhole.miner.core.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Locale;

/**
 * 系统工具方法
 *
 * @author kzw on 2016/05/22.
 */
@SuppressLint("InlinedApi")
@SuppressWarnings("deprecation")
public final class SystemUtils {

    public static final String LANGUAGE_ZH = "zh";
    public static final String LANGUAGE_ZH_TW = "zh-TW";
    public static final String LANGUAGE_EN = "en";

    private SystemUtils() {
    }

    public static boolean isZh(Context context) {
        if (context != null) {
            Locale locale = context.getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            return language.endsWith("zh");
        }
        return false;
    }

    public static void updateLanguageResources(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale = locale;
        resources.updateConfiguration(config, dm);
    }

    /**
     * Show the input method.
     *
     * @param context context
     * @param view    The currently focused view, which would like to receive soft
     *                keyboard input
     * @return success or not.
     */
    public static boolean showInputMethod(Context context, View view) {
        return showInputMethod(context, view, null);
    }

    /**
     * Show the input method.
     *
     * @param context  context
     * @param view     The currently focused view, which would like to receive soft
     *                 keyboard input
     * @param receiver If non-null, this will be called by the IME when
     *                 it has processed your request to tell you what it has done.  The result
     *                 code you receive may be either {@link InputMethodManager#RESULT_UNCHANGED_SHOWN},
     *                 {@link InputMethodManager#RESULT_UNCHANGED_HIDDEN}, {@link InputMethodManager#RESULT_SHOWN}, or
     *                 {@link InputMethodManager#RESULT_HIDDEN}.
     * @return success or not.
     */
    private static boolean showInputMethod(Context context, View view, ResultReceiver receiver) {
        if (context == null || view == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (receiver != null) {
                return imm.showSoftInput(view, 0, receiver);
            } else {
                return imm.showSoftInput(view, 0);
            }
        }
        return false;
    }

    /**
     * Hides the input method.
     *
     * @param context context
     * @param view    The currently focused view
     * @return success or not.
     */
    public static boolean hideInputMethod(Context context, View view) {
        if (context == null || view == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return false;
    }

    /**
     * 复制到剪切版
     */
    public static boolean clipToBoard(Context context, String str) {
        if (context != null && !TextUtils.isEmpty(str)) {
            ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (APIUtils.hasHoneycomb()) {
                ClipData data = ClipData.newPlainText("data", str);
                manager.setPrimaryClip(data);
            } else {
                manager.setText(str);
            }
            return true;
        }

        return false;
    }
}
