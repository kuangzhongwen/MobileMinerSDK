package waterhole.commonlibs.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import waterhole.commonlibs.ContextWrapper;
import waterhole.commonlibs.preferences.SharedPreferencesUtils;
import waterhole.commonlibs.utils.APIUtils;
import waterhole.commonlibs.utils.ForegroundCallbacks;
import waterhole.commonlibs.utils.LogUtils;

import static waterhole.commonlibs.utils.AppUtils.getActivity;

/**
 * Android 6.0 权限管理工具，调用的module自行处理具体的权限逻辑，这边只提供最基本的逻辑方法。
 * <p>
 * <br> 相关文档：Requesting Permissions at Run Time:
 *
 * @author kzw on 2016/09/30.
 * @OnMPermissionGranted(requestCode) public void onReadExternalStoragePermissionSuccess() {
 * //...
 * }
 * @OnMPermissionDenied(requestCode) public void onReadExternalStoragePermissionDenied() {
 * //...
 * }
 * @OnMPermissionNeverAskAgain(requestCode) public void onReadExternalStoragePermissionNeverAskAgain() {
 * //...
 * }
 * </code>
 * <p>
 * Annotation:
 * @see # https://developer.android.com/training/permissions/requesting.html </br>
 * <p>
 * <code>
 * // request
 * MPermissionUtils.with(activity)
 * .addRequestCode(requestCode)
 * .permissions(READ_EXTERNAL_STORAGE)
 * .request();
 * <p>
 * // response by reflect
 * @see OnMPermissionGranted
 * @see OnMPermissionDenied
 * @see OnMPermissionNeverAskAgain
 */
public final class MPermissionUtils {

    private final Object mObject;

    private String[] mPermissions;

    private int mRequestCode;

    private MPermissionUtils(Object object) {
        mObject = object;
    }

    /**
     * 在Activiy上做权限申请
     *
     * @param activity 权限申请的Activity
     * @return MPermissionUtils对象
     */
    public static MPermissionUtils with(Activity activity) {
        return new MPermissionUtils(activity);
    }


    /**
     * 在Fragment上做权限申请
     *
     * @param fragment 权限申请的Fragment
     * @return MPermissionUtils对象
     */
    public static MPermissionUtils with(Fragment fragment) {
        return new MPermissionUtils(fragment);
    }

    /**
     * 本次需要申请的权限
     *
     * @param permissions 权限数组 example:[Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION]
     * @return MPermissionUtils对象
     */
    public MPermissionUtils permissions(String... permissions) {
        mPermissions = permissions;
        return this;
    }

    /**
     * 权限申请请求码，需自定义，权限返回时，反射识别用
     *
     * @param requestCode 请求码
     * @return MPermissionUtils对象
     */
    public MPermissionUtils addRequestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }

    /**
     * 发起权限请求
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public void request() {
        ForegroundCallbacks.get(ContextWrapper.getInstance().obtainContext()).setIngoreCallback(true);
        requestPermissions(mObject, mRequestCode, mPermissions);
    }

    /**
     * 权限是否被允许了
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static boolean isPermissionGranted(Context context, String permission) {
        return APIUtils.hasM() && !TextUtils.isEmpty(permission) && context != null &&
                context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 权限是否是允许了,只要一个允许就返回true
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static boolean isPermissionsGrantedOr(Context context, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        boolean isGranted = false;
        for (String permission : permissions) {
            if (isPermissionGranted(context, permission)) {
                isGranted = true;
                break;
            }
        }

        return isGranted;
    }

    /**
     * 权限是否是允许了,所有权限都允许才返回true
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static boolean isPermissionsGrantedAnd(Context context, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }

        int deniedNum = 0;
        for (String permission : permissions) {
            if (isPermissionDenied(context, permission)) {
                deniedNum++;
            }
        }

        return deniedNum == 0;
    }

    /**
     * 权限是否被拒绝了
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static boolean isPermissionDenied(Context context, String permission) {
        return !TextUtils.isEmpty(permission) && !isPermissionGranted(context, permission);
    }

    /**
     * 权限是否是被拒绝了,只要一个允许就返回false
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static boolean isPermissionsDeniedOr(Context context, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        boolean isGranted = false;
        for (String permission : permissions) {
            if (isPermissionGranted(context, permission)) {
                isGranted = true;
                break;
            }
        }

        return !isGranted;
    }

    /**
     * 权限是否是不再询问
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static boolean isPermissionNeverAskAgain(Activity activity, String permission) {
        return APIUtils.hasM() && !TextUtils.isEmpty(permission)
                && activity.checkSelfPermission(permission)
                != PackageManager.PERMISSION_GRANTED && !activity
                .shouldShowRequestPermissionRationale(permission);
    }

    /**
     * 权限是否是不再询问,只要有一个就返回true
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static boolean isPermissionNeverAskAgainOr(Activity activity, String... permissions) {
        boolean isNeverAskAgain = false;
        for (String permission : permissions) {
            if (isPermissionNeverAskAgain(activity, permission)) {
                isNeverAskAgain = true;
                break;
            }
        }
        return isNeverAskAgain;
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    private static void requestPermissions(Object object, int requestCode, String[] permissions) {
        if (!APIUtils.hasM()) {
            doPermissionExecuteSuccess(object, requestCode);
            return;
        }
        List<String> deniedPermissions = findDeniedPermissions(getActivity(object), permissions);

        if (deniedPermissions.size() > 0) {
            if (object instanceof Activity) {
                ((Activity) object).requestPermissions(
                        deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else if (object instanceof Fragment) {
                ((Fragment) object).requestPermissions(
                        deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else {
                throw new IllegalArgumentException(
                        object.getClass().getName() + " is not supported");
            }
        } else {
            doPermissionExecuteSuccess(object, requestCode);
        }
    }

    /**
     * 权限是否请求过
     */
    public static boolean isPermissionRequested(Context context, String key) {
        return !TextUtils.isEmpty(key) && !SharedPreferencesUtils.getBoolean(context, key, true);
    }

    /**
     * 权限是否都请求过
     */
    public static boolean isPermissionRequestedAnd(Context context, String... keys) {
        boolean isRequested = true;
        for (String key : keys) {
            if (!isPermissionRequested(context, key)) {
                isRequested = false;
                break;
            }
        }

        return isRequested;
    }

    /**
     * 处理请求结果
     */
    public static void onRequestPermissionsResult(Activity activity, int requestCode,
                                                  String[] permissions, int[] grantResults) {
        requestResult(activity, requestCode, permissions, grantResults);
    }

    private static void requestResult(Object obj, int requestCode, String[] permissions,
                                      int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }

        if (deniedPermissions.size() > 0) {
            if (hasNeverAskAgainPermission(getActivity(obj), deniedPermissions)) {
                doPermissionExecuteFailAsNeverAskAgain(obj, requestCode);
            } else {
                doPermissionExecuteFail(obj, requestCode);
            }
        } else {
            doPermissionExecuteSuccess(obj, requestCode);
        }
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    private static List<String> findDeniedPermissions(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    private static boolean hasNeverAskAgainPermission(Activity activity, List<String> permission) {
        for (String value : permission) {
            if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED &&
                    !activity.shouldShowRequestPermissionRationale(value)) {
                return true;
            }
        }

        return false;
    }

    public static String toString(List<String> permission) {
        if (permission == null || permission.isEmpty()) {
            return "";
        }

        return toString(permission.toArray(new String[permission.size()]));
    }

    public static String toString(String[] permission) {
        if (permission == null || permission.length <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String p : permission) {
            sb.append(p.replaceFirst("android.permission.", ""));
            sb.append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static <A extends Annotation> Method findPermissionMethodWithRequestCode(Class clazz,
                                                                                     Class<A> annotation, int requestCode) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                if (isEqualPermissionRequestCodeFromAnnotation(method, annotation, requestCode)) {
                    return method;
                }
            }
        }
        return null;
    }

    private static boolean isEqualPermissionRequestCodeFromAnnotation(Method m, Class clazz,
                                                                      int requestCode) {
        if (clazz.equals(OnMPermissionDenied.class)) {
            return requestCode == m.getAnnotation(OnMPermissionDenied.class).value();
        } else if (clazz.equals(OnMPermissionGranted.class)) {
            return requestCode == m.getAnnotation(OnMPermissionGranted.class).value();
        } else if (clazz.equals(OnMPermissionNeverAskAgain.class)) {
            return requestCode == m.getAnnotation(OnMPermissionNeverAskAgain.class).value();
        } else {
            return false;
        }
    }

    /**
     * 避免使用org.androidannotations.annotations，导致class追加_，而回调不了注解函数.
     *
     * @param clazz class对象
     */
    private static Class getEnsureClass(Object clazz) {
        Class clz = clazz.getClass();
        String className = clz.getName();
        if (className.endsWith("_")) {
            try {
                clz = Class.forName(className.substring(0, className.length() - 1));
            } catch (ClassNotFoundException e) {
                LogUtils.printStackTrace(e);
            }
        }
        return clz;
    }

    private static void doPermissionExecuteSuccess(Object activity, int requestCode) {
        executeMethod(activity, findPermissionMethodWithRequestCode(getEnsureClass(activity),
                OnMPermissionGranted.class, requestCode));
    }

    private static void doPermissionExecuteFail(Object activity, int requestCode) {
        executeMethod(activity, findPermissionMethodWithRequestCode(getEnsureClass(activity),
                OnMPermissionDenied.class, requestCode));
    }

    private static void doPermissionExecuteFailAsNeverAskAgain(Object activity, int requestCode) {
        executeMethod(activity, findPermissionMethodWithRequestCode(getEnsureClass(activity),
                OnMPermissionNeverAskAgain.class, requestCode));
    }

    private static void executeMethod(Object activity, Method executeMethod) {
        executeMethodWithParam(activity, executeMethod, new Object[]{});
    }

    private static void executeMethodWithParam(Object activity, Method executeMethod,
                                               Object... args) {
        if (executeMethod != null) {
            try {
                if (!executeMethod.isAccessible()) {
                    executeMethod.setAccessible(true);
                }
                executeMethod.invoke(activity, args);
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    /**
     * 判断权限是否打开，Android 6.0以前的判断方式
     *
     * @return true 允许  false禁止
     */
    @Deprecated
    public static boolean checkPermissionByAppOps(Context context, int permissionId) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(permissionId);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            return false;
        }
    }
}
