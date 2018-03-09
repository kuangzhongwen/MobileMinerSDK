package waterhole.commonlibs.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.hardware.Camera;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * 包含设备处理方法的工具类，包括设备内存监控
 *
 * @author kzw on 2015/11/21.
 */
public final class DeviceUtils {

    public static final int MEMORY_LARGE = 1;
    public static final int MEMORY_MIDDLE = 2;
    public static final int MEMORY_SMALL = 3;

    private static int mMemoryLevel;

    private DeviceUtils() {
    }

    /**
     * 获取手机SIM卡序列号
     */
    public static String getSIMSerialNumber(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    public static long getAvailableMemory(Context context) {
        if (context == null) {
            return 0;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        // 获得系统可用内存，保存在MemoryInfo对象上
        am.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;

        // 字符类型转换
        // String leftMemSize = Formatter.formatFileSize(context, memSize);
        long leaveMemSize = memSize / 1024 / 1024;
        if (leaveMemSize >= 500) {
            mMemoryLevel = MEMORY_LARGE;
        } else if (leaveMemSize < 500 && leaveMemSize >= 100) {
            mMemoryLevel = MEMORY_MIDDLE;
        } else {
            mMemoryLevel = MEMORY_SMALL;
        }
        return leaveMemSize;
    }

    public static int getmMemoryLevel() {
        return mMemoryLevel;
    }

    public static String getAndroidId(Context context) {
        return android.provider.Settings.Secure.getString(context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    public static List<Camera.Size> getResolutionList(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        return parameters.getSupportedPreviewSizes();
    }
}
