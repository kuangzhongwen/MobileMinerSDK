package waterhole.commonlibs.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import waterhole.commonlibs.ContextWrapper;

import static android.content.ContentValues.TAG;
import static android.os.Looper.getMainLooper;

/**
 * app相关工具，如安装，运行堆栈，跳转，前后台信息等
 *
 * @author kzw on 2017/07/04.
 */
public final class AppUtils {

    // 打开定位设置页面
    public static final int ACTIVITY_CODE_OPEN_LOCATION_SETTINGS = 116;
    // 打开设置中的开发者选项
    public static final int ACTIVITY_CODE_OPEN_DEVELOPER_OPTIONS_SETTING = 138;
    // 打开VPN设置界面
    public static final int ACTIVITY_CODE_OPEN_VPN_SETTINGS = 148;
    // app设置界面
    public static final int ACTIVITY_CODE_OPEN_APP_SETTINGS = 157;

    // 打开app设置页跳转码
    public static final int ACTIVITY_CODE_OPEN_APP_PERMISSION_SETTINGS = 0x0114;

    private static final String SCHEME = "package";

    private static Handler mHandler = new Handler(getMainLooper());

    private AppUtils() {
    }

    /**
     * 获取Activity
     */
    public static Activity getActivity(Object object) {
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof Activity) {
            return (Activity) object;
        }
        return null;
    }

    /**
     * 判断用于UI上下文的activity对象是否安全
     *
     * @param activity 需要判断的activity
     * @return true: UI上下文安全的activity对象
     */
    public static boolean isSecureContextForUI(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return false;
        }
        if (APIUtils.hasJellyBeanMR1()) {
            try {
                return !activity.isDestroyed();
            } catch (NoSuchMethodError e) {
                LogUtils.printStackTrace(e);
                LogUtils.error(TAG, "bugfix umeng 三星SM-w2014 4.3 系统 NoSuchMethodError 崩溃");
            }
        }
        return true;
    }

    /**
     * 判断某个Activity是否处于最前端
     *
     * @param className Class.getName  判断该ClassName对应的类是否是TopActivity     * @return
     */
    public static boolean isTopActivity(Context ctx, String className) {
        ActivityManager activityManager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> taskInfos = activityManager.getRunningTasks(100);
        if (taskInfos.size() == 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo taskInfo : taskInfos) {
            // 判断是否为我们的应用跑起来的task
            if (taskInfo.baseActivity.toString().contains(ctx.getPackageName())) {
                if (taskInfo.topActivity.getClassName().equals(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否是同一个Activity类
     */
    public static boolean isTopActivity(Activity activity, Class<?> destClas) {
        if (activity == null || destClas == null) {
            return false;
        }
        return getClassSimpleName(activity.getClass()).equals(getClassSimpleName(destClas));
    }

    /**
     * 获取class的Simple name
     */
    public static String getClassSimpleName(Class<?> cla) {
        return cla.getSimpleName();
    }

    /**
     * 是否运行在前台
     */
    public static boolean isRunningForeground(Context context) {
        if (context != null) {
            String packageName = context.getPackageName();
            String topActivityClassName = getTopActivityName(context);
            return packageName != null && topActivityClassName != null && topActivityClassName.startsWith(packageName);
        }
        return false;
    }

    /**
     * 是否在后台
     */
    public static boolean isAppOnFreground(Context context) {
        if (context != null) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            String curPackageName = context.getApplicationContext().getPackageName();
            List<RunningAppProcessInfo> app = am.getRunningAppProcesses();
            if (app != null) {
                for (RunningAppProcessInfo a : app) {
                    if (a.processName.equals(curPackageName) &&
                            a.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取栈顶Activity名称
     */
    public static String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager = (ActivityManager) (context
                .getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName;
    }

    /**
     * Get info for given package.
     *
     * @param packageName package name
     * @return {@link PackageInfo}, null if package cannot be found in the system.
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageInfo pkgInfo = null;
        if (context != null && packageName != null) {
            PackageManager pm = context.getPackageManager();
            try {
                pkgInfo = pm.getPackageInfo(packageName, 0);
            } catch (NameNotFoundException e) {
                LogUtils.printStackTrace(e);
            }
        }
        return pkgInfo;
    }

    /**
     * If given package is installed.
     *
     * @param packageName package name
     * @return is installed or not.
     */
    public static boolean isPackageInstalled(Context context, String packageName) {
        return getPackageInfo(context, packageName) != null;
    }

    /**
     * 获取:管理应用程序，应用程序详情Intent
     * <pre>
     * 支持android 2.3（ApiLevel 9）以上
     * </pre>
     */
    public static Intent getInstalledAppDetailsIntent(String pkgName) {
        Intent intent = new Intent();
        if (APIUtils.hasGingerbread()) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, pkgName, null);
            intent.setData(uri);
        }
        return intent;
    }

    /**
     * 启动程序安装界面
     */
    public static void startInstall(Context context, String fileName,
                                    boolean deleteFileWhenNotExist) {
        File installFile = new File(fileName);
        if (installFile.exists()) {
            Intent intent;
            if (APIUtils.hasM()) {
                intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction("android.intent.action.VIEW");
                String type = FileUtils.getMIMEByFilePath(fileName);
                intent.setDataAndType(Uri.fromFile(installFile), type);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromFile(installFile);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }

            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Related programs were not found!",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            if (deleteFileWhenNotExist) {
                FileUtils.delete(installFile);
            }
        }
    }

    /**
     * 获取正在运行的进程
     */
    @SuppressWarnings("unchecked")
    public static List<String> getRunningProcess(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcess = activityManager
                .getRunningAppProcesses();
        if (runningProcess == null || runningProcess.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<String> processNames = new ArrayList<>();
        for (ActivityManager.RunningAppProcessInfo ra : runningProcess) {
            processNames.add(ra.processName);
        }
        return processNames;
    }

    /**
     * 获得当前进程的进程名
     *
     * @return 当前进程的进程名
     */
    public static String getCurrentProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcess = activityManager
                .getRunningAppProcesses();
        if (runningProcess == null || runningProcess.isEmpty()) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo info : runningProcess) {
            if (info.pid == pid) {
                return info.processName;
            }
        }
        return null;
    }

    public static void openAppPermissionSettings(Activity ac) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + ac.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResultSafely(ac, intent, ACTIVITY_CODE_OPEN_APP_PERMISSION_SETTINGS);
    }

    /**
     * 获取渠道名.
     *
     * @param context 上下文对象.
     */
    public static String getChannelName(Context context, String metaKey) {
        if (context != null && !TextUtils.isEmpty(metaKey)) {
            String channelName = null;
            try {
                PackageManager packageManager = context.getPackageManager();
                if (packageManager != null) {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                            context.getPackageName(),
                            PackageManager.GET_META_DATA);
                    if (applicationInfo != null) {
                        if (applicationInfo.metaData != null) {
                            channelName = applicationInfo.metaData.getString(metaKey);
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return channelName;
        }
        return "";
    }

    /**
     * 安全启动应用程序，截获Exception。
     *
     * @param activity Activity
     * @param intent   Intent
     * @return 是否成功启动Activity。
     */
    private static void startActivitySafely(Activity activity, Intent intent) {
        startActivitySafely(activity, intent, true);
    }

    /**
     * 安全启动应用程序，截获Exception。
     *
     * @param activity activity
     * @param intent   Intent
     * @param newTask  是否添加Intent.FLAG_ACTIVITY_NEW_TASK
     * @return 是否成功启动Activity。
     */
    private static void startActivitySafely(Activity activity, Intent intent, boolean newTask) {
        if (activity == null || intent == null) {
            return;
        }
        try {
            if (newTask) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "Related programs were not found!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 安全启动应用程序，截获Exception。 必须在主线程被调用
     *
     * @param context context
     * @param intent  Intent
     * @return 是否成功启动Activity。
     */
    public static void startActivitySafely(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            LogUtils.info(TAG, "startActivitySafely");
        } catch (Exception e) {
            Toast.makeText(context, "Related programs were not found!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Safe version to start an activity for result
     *
     * @return 是否成功启动
     */
    public static void startActivityForResultSafely(Activity activity, Intent intent,
                                                    int requestCode) {
        if (activity == null || intent == null) {
            return;
        }
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Toast.makeText(activity, "Related programs were not found!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Safe version to start an activity for result
     *
     * @return 是否成功启动
     */
    public static void startActivityForResultSafely(android.support.v4.app.Fragment fragment,
                                                    Intent intent, int requestCode) {
        if (fragment == null || intent == null) {
            return;
        }
        try {
            fragment.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            if (fragment.getActivity() != null) {
                Toast.makeText(fragment.getActivity(), "Related programs were not found!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 获取fragment依附的Activity
     */
    public static Activity getAttachActivity(android.support.v4.app.Fragment fragment) {
        Activity ac = fragment.getActivity();
        if (ac != null && !ac.isFinishing()) {
            return ac;
        }

        return null;
    }

    /**
     * 回到系统桌面
     */
    public static void backtoSystemDesktop(Context context) {
        if (context != null) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivitySafely(context, i);
        }
    }

    public static void killProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static void runOnUiThreadSafely(Activity activity, Runnable runnable) {
        if (activity == null || activity.isFinishing() || runnable == null) {
            return;
        }
        try {
            activity.runOnUiThread(runnable);
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
    }

    public static void runOnMainThread(Runnable runnable) {
        runOnMainThread(runnable, 0L);
    }

    public static void runOnMainThread(Runnable runnable, long delay) {
        if (runnable == null) {
            return;
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }

        if (delay > 0) {
            mHandler.postDelayed(runnable, delay);
        } else {
            mHandler.post(runnable);
        }
    }

    public static void runAtFrontOfQueue(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postAtFrontOfQueue(runnable);
    }

    public static Handler getHandler() {
        return mHandler;
    }

    /**
     * 终止ActivityThread的looper循环
     */
    public static void mainLooperQuit() {
        try {
            Looper looper = getMainLooper();
            if (looper != null) {
                if (APIUtils.hasJellyBeanMR2()) {
                    looper.quitSafely();
                } else {
                    looper.quit();
                }
            }
        } catch (Throwable e) {
            LogUtils.printStackTrace(e);
        }
    }

    public static void openSystemBrowserWithUrl(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri contentUrl = Uri.parse(url);
        intent.setData(contentUrl);
        startActivitySafely(ContextWrapper.getInstance().obtainContext(), intent);
    }

    public static void openGooglePlayServiceActivity(Context context) {
        Uri uri = Uri.parse("market://details?id=com.google.android.gms&pcampaignid=gcore_8487000");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.android.vending");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivitySafely(context, intent);
    }

    public static void openGPSSettings(Activity ac) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResultSafely(ac, intent, ACTIVITY_CODE_OPEN_LOCATION_SETTINGS);
    }

    public static void openSystemSendSMSInterface(Context context, Uri smsToUri, String smsBody) {
        if (smsToUri == null || smsBody == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", smsBody);
        startActivitySafely(context, intent);
    }

    public static void openSystemSettingsToDeveloperOptionsForResult(Activity ac) {
        startActivityForResultSafely(ac, new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS),
                ACTIVITY_CODE_OPEN_DEVELOPER_OPTIONS_SETTING);
    }

    public static void viewFile(Context context, String filePath, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType(mimeType);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, mimeType);
        startActivitySafely(context, intent);
    }

    public static void openVPNSettings(Activity ac) {
        Intent intent = new Intent();
        intent.setAction("android.net.vpn.SETTINGS");
        startActivityForResultSafely(ac, intent, ACTIVITY_CODE_OPEN_VPN_SETTINGS);
    }
}
