package waterhole.commonlibs.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * 为了修复在全屏幕模式下软键盘挡住输入框问题的解决方案类
 *
 * @author kzw on 2017/07/31.
 */
public final class FullScreenBugWorkaroundUtils {

    private FullScreenBugWorkaroundUtils() {
    }

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static void assistActivity(final Activity activity) {
        if (activity == null) {
            return;
        }
        new FullScreenBugWorkaroundUtils(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private FullScreenBugWorkaroundUtils(final Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        if (content != null && content.getChildCount() > 0) {
            mChildOfContent = content.getChildAt(0);
            if (mChildOfContent != null) {
                mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        possiblyResizeChildOfContent();
                    }
                });
                frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
            }
        }
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        // 全屏模式下： return r.bottom
        return (r.bottom - r.top);
    }
}
