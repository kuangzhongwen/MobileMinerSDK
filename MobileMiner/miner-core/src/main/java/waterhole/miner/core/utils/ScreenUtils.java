package waterhole.miner.core.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * 获取屏幕,分辨率相关
 *
 * @author kzw on 2017/07/03.
 */
public final class ScreenUtils {

    private ScreenUtils() {
    }

    public static int dip2px(Context context, int dip) {
        float density = getDensity(context);
        return (int) (dip * density + 0.5);
    }

    public static int px2dip(Context context, int px) {
        float density = getDensity(context);
        return (int) ((px - 0.5) / density);
    }

    private static float getDensity(Context context) {
        return context != null ? context.getResources().getDisplayMetrics().density : 0;
    }

    public static float getPixelsInCM(float cm, boolean isX) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        return (cm / 2.54f) * (isX ? displayMetrics.xdpi : displayMetrics.ydpi);
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        if (context != null) {
            Class<?> c;
            Object obj;
            Field field;
            int x;
            int sbar = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                sbar = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                LogUtils.printStackTrace(e1);
            }
            return sbar;
        }
        return 0;
    }

    public static int getScreenHeight(Context context) {
        return context != null ? context.getResources().getDisplayMetrics().heightPixels : 0;
    }

    public static int getScreenWidth(Context context) {
        return context != null ? context.getResources().getDisplayMetrics().widthPixels : 0;
    }

    public static int getPhotoSize() {
        int photoSize;
        if (Build.VERSION.SDK_INT >= 16) {
            photoSize = 1280;
        } else {
            photoSize = 800;
        }
        return photoSize;
    }

    public static void attachWindowNoTitle(Activity activity) {
        if (activity != null) {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    public static void attachWindowFullScreen(Activity activity) {
        if (activity != null) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void attachWindowScreenKeepOn(Activity activity) {
        if (activity != null) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public static void setSoftInputHiddenState(Activity activity) {
        if (activity != null) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }
}