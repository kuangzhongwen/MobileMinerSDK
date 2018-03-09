/*
 * Copyright (C) 2014 Zlianjie Inc. All rights reserved.
 */
package waterhole.commonlibs.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * UI工具类
 *
 * @author kzw on 2014/07/25.
 */
public final class UIUtils {

    private UIUtils() {
    }

    public static void runOnLayoutDone(final View view, final Runnable runnable) {
        OnGlobalLayoutListener l = new OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if (APIUtils.hasJellyBean()) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                runnable.run();
            }
        };
        view.getViewTreeObserver().addOnGlobalLayoutListener(l);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void setBackgroundDrawable(View view, Drawable d) {
        if (view != null) {
            if (APIUtils.hasJellyBean()) {
                view.setBackground(d);
            } else {
                view.setBackgroundDrawable(d);
            }
        }
    }

    public static int getColor(Context context, int resId) {
        try {
            return context.getResources().getColor(resId);
        } catch (NotFoundException e) {
            return 0;
        }
    }

    public static CharSequence getText(Context context, int resId) {
        try {
            return context.getText(resId);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getString(Context context, int resId) {
        try {
            return context.getString(resId);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getString(Context context, int resId, Object... formatArgs) {
        try {
            return context.getString(resId, formatArgs);
        } catch (Exception e) {
            return "";
        }
    }

    public static String[] getStringArray(Context context, int resId) {
        try {
            return context.getResources().getStringArray(resId);
        } catch (Exception e) {
            return null;
        }
    }

    public static float getDimen(Context context, int resId) {
        try {
            return context.getResources().getDimension(resId);
        } catch (NotFoundException e) {
            return 0;
        }
    }

    public static int getDimenPixelSize(Context context, int resId) {
        try {
            return context.getResources().getDimensionPixelSize(resId);
        } catch (NotFoundException e) {
            return 0;
        }
    }

    public static int getDimenPixelOffset(Context context, int resId) {
        try {
            return context.getResources().getDimensionPixelOffset(resId);
        } catch (NotFoundException e) {
            return 0;
        }
    }

    public static Drawable getDrawable(Context context, int resId) {
        try {
            return context.getResources().getDrawable(resId);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public static Bitmap getBitmap(Context context, int resId) {
        try {
            return BitmapFactory.decodeResource(context.getResources(), resId);
        } catch (Exception e) {
            return null;
        }
    }

    public static int getInteger(Context context, int resId) {
        try {
            return context.getResources().getInteger(resId);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int[] getIntArray(Context context, int resId) {
        try {
            return context.getResources().getIntArray(resId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Set color to given {@link CharSequence}.
     *
     * @param text  Original text
     * @param color Color
     * @return Colored text.
     */
    public static CharSequence getColoredText(CharSequence text, int color) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spanned.SPAN_COMPOSING);
        return ss;
    }

    /**
     * 通过资源名找到资源
     */
    public static int getResId(Context context, String name, String defType) {
        try {
            String packageName = context.getApplicationInfo().packageName;
            return context.getResources().getIdentifier(name, defType, packageName);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 填充stub
     */
    public static View inflateStub(View root, int stubRes, int layoutRes) {
        View securityFields = null;
        View fields = root.findViewById(stubRes);
        if (fields instanceof ViewStub) {
            if (layoutRes != 0) {
                ViewStub stub = (ViewStub) fields;
                stub.setLayoutResource(layoutRes);
                securityFields = stub.inflate();
            }
        } else {
            securityFields = fields;
        }
        return securityFields;
    }

    /**
     * DecorView是一个FrameLayout(底层容器)，内部是一个LinearLayout（包含titleBar和contentView)
     */
    public static View getDecorView(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return null;
        }
        return activity.getWindow().getDecorView();
    }

    public static View getContentView(Activity activity) {
        View decorView = getDecorView(activity);
        if (decorView != null) {
            return ((ViewGroup) decorView.findViewById(android.R.id.content)).getChildAt(0);
        } else {
            return null;
        }
    }

    /**
     * 对话框是否已弹出
     */
    public static boolean dialogIsShowing(Dialog dialog) {
        return dialog != null && dialog.isShowing();
    }
}
