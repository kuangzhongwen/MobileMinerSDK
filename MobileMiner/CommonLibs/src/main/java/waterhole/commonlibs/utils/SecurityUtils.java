package waterhole.commonlibs.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static waterhole.commonlibs.permission.MPermissionUtils.isPermissionGranted;
import static waterhole.commonlibs.preferences.SharedPreferencesUtils.getBoolean;
import static waterhole.commonlibs.preferences.SharedPreferencesUtils.setBoolean;
import static waterhole.commonlibs.preferences.SharedPreferencesUtils.getString;
import static waterhole.commonlibs.preferences.SharedPreferencesUtils.setString;
import static waterhole.commonlibs.utils.DeviceUtils.getSIMSerialNumber;

/**
 * 对手机的某些信息进行安全性检测
 *
 * @author SwainLi on 2016/03/30.
 */
public final class SecurityUtils {

    private static final String SP_SIM_SERIAL_NUMBER = "sim_serial_number";
    private static final String SP_SIM_SERIAL_NUMBER_FIRST_STORE = "sim_serial_number_first_store";

    private SecurityUtils() {
    }

    public static boolean checkSimStatus(Context context) {
        return !checkSimExist(context);
    }

    public static boolean checkAirMode(Context context) {
        return isAirModeOn(context);
    }

    public static boolean checkSimSerialNumberIsChanged(Context context) {
        return isSIMSerialNumberChanged(context);
    }

    /**
     * 检测sim卡是否存在
     */
    public static boolean checkSimExist(Context context) {
        return context != null && !TextUtils.isEmpty(getSIMSerialNumber(context));
    }

    /**
     * 判断是否为飞行模式
     */
    public static boolean isAirModeOn(Context context) {
        return context != null && (Settings.System
                .getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1);
    }

    /**
     * @return sim卡序列号是否变更了
     */
    private static boolean isSIMSerialNumberChanged(Context context) {
        String simSerialNumber = "";
        if (!APIUtils.hasM() || isPermissionGranted(context, Manifest.permission.READ_PHONE_STATE)) {
            simSerialNumber = getSIMSerialNumber(context);
        }
        simSerialNumber = simSerialNumber == null ? "" : simSerialNumber;
        // 如果是第一次存储，则直接返回
        if (getBoolean(context, SP_SIM_SERIAL_NUMBER_FIRST_STORE, true)) {
            setBoolean(context, SP_SIM_SERIAL_NUMBER_FIRST_STORE, false);
            setString(context, SP_SIM_SERIAL_NUMBER, simSerialNumber);
            return false;
        }
        String oldSimSerialNumber = getString(context, SP_SIM_SERIAL_NUMBER, "");
        // 如果都为空
        if (simSerialNumber.equals("") && oldSimSerialNumber.equals("")) {
            return false;
        }
        return !simSerialNumber.equals(oldSimSerialNumber);
    }

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    public static boolean isDevelopmentSettingsEnable(Context context) {
        return APIUtils.hasJellyBeanMR1() && context != null && Settings.Secure.getInt(context.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1;
    }

    public static boolean isMockLocationActivated(Context context) {
        return context != null && isMockLocationSettingOn(context);
    }

    public static boolean isAdbEnable(Context context) {
        return APIUtils.hasJellyBeanMR1() && Settings.Secure.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0) == 1;
    }

    /**
     * 检测AndroidManifest.xml的调试标志位是否被篡改
     * <pre>
     *     使用此方法时必须预先在AndroidManifest.xml设置android:debuggable=”false”，
     *     攻击者要尝试调试应用时很有可能去修改该参数，因而此手法可用于做动态反调试检测
     * </pre>
     */
    public static boolean isTamperDebuggable(Context context) {
        return context != null && (context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    /**
     * @return 检测应用程序是否连接调试器
     */
    public static boolean isDebuggerConnected() {
        return android.os.Debug.isDebuggerConnected();
    }

    /**
     * @return 判断应用程序是否运行在模拟器上
     */
    public static boolean isEmulator() {
        // model:Android SDK built for x86
        //只要是在模拟器中，不管是什么版本的模拟器，在它的MODEL信息里就会带有关键字参数sdk
        return Build.MODEL.contains("sdk") || Build.MODEL.contains("SDK");
    }


    /**
     * 获取签名信息
     *
     * @param context : 上下文
     * @param pkgName package name
     * @return 签名String
     */
    public static String getSignature(Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        String sign = "";
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            if (packageInfo != null && packageInfo.signatures.length > 0) {
                sign = packageInfo.signatures[0].toCharsString();
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
        return sign;
    }

    /**
     * 对比当前和预埋签名的hashcode是否一致
     *
     * @param context      上下文对象
     * @param appSignature app签名字符串
     */
    public static boolean isNotEqualsSignature(Context context, String appSignature) {
        return context != null && !getSignature(context, context.getPackageName()).equals(appSignature);
    }

    /**
     * 是否允许模拟地理位置
     */
    public static boolean isMockLocationSettingOn(Context context) {
        if (context != null) {
            boolean isOpen = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
            /**
             * 该判断API是androidM以下的API,由于Android M中已经没有了关闭允许模拟位置的入口,
             * 所以这里一旦检测到开启了模拟位置,并且是android M以上,则默认设置为未有开启模拟位置
             */
            if (isOpen && Build.VERSION.SDK_INT >= 23) {
                isOpen = false;
            }
            return isOpen;
        }
        return false;
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"
        };
        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        BufferedReader in = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
            IOUtils.closeSafely(in);
        }
    }
}
