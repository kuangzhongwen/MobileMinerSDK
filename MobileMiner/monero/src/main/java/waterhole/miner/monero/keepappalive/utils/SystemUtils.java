package waterhole.miner.monero.keepappalive.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;
import waterhole.miner.core.utils.LogUtils;

/**
 * 工具类
 */
public class SystemUtils {

    /**
     * 判断本应用是否存活
     * 如果需要判断本应用是否在后台还是前台用getRunningTask
     */
    public static boolean isAPPALive(Context mContext) {
        boolean isAPPRunning = false;
        // 获取activity管理对象
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取所有正在运行的app
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        // 遍历，进程名即包名
        for (ActivityManager.RunningAppProcessInfo appInfo : appProcessInfoList) {
            LogUtils.info("alive process:" + appInfo.processName);
            if (appInfo.processName.endsWith(":waterhole")) {
                isAPPRunning = true;
                break;
            }
        }
        return isAPPRunning;
    }
}
