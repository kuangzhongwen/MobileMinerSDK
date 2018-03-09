package waterhole.commonlibs.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

/**
 * InputMethodManager内存泄漏修复
 *
 * @author kzw on 2017/08/22.
 */
public final class InputMethodManagerLeakBug {

    public InputMethodManagerLeakBug() {
        throw new RuntimeException("InputMethodManagerLeakBug stub!");
    }

    public static void fixInputMethodManagerLeak(Context context) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
                Field field;
                Object obj;
                for (int i = 0, len = arr.length; i < len; i++) {
                    String param = arr[i];
                    try {
                        field = imm.getClass().getDeclaredField(param);
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        obj = field.get(imm);
                        if (obj != null && obj instanceof View) {
                            View view = (View) obj;
                            // 被InputMethodManager持有引用的context是想要目标销毁的
                            if (view.getContext() == context) {
                                // 置空，破坏掉path to gc节点
                                field.set(imm, null);
                            } else {
                                break;
                            }
                        }
                    } catch (Throwable t) {
                        LogUtils.printStackTrace(t);
                    }
                }
            }
        }
    }
}
