package waterhole.miner.core.temperature;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import java.util.List;

import waterhole.miner.core.NoProGuard;

import static waterhole.miner.core.utils.LogUtils.error;

/**
 * 温控任务
 */
public class TemperatureController implements NoProGuard {

    private int stopTemperature = 70 * 1000;
    private int startTemperature = 35 * 1000;
    private long pollingTime = 1000L;
    private long lastStopTime;
    private long stopDelay = 5000L;
    public boolean needRun = false;
    private ITempTask tempTask;
    private boolean isTempTaskRunning;
    private int curUsage;

    // 需要根据不同的cpu，不同的温度设置不同的参数
    private int[][] temperatureSurface = {{startTemperature, 2, 100},
            {stopTemperature, 2, 80}};

    public void setTemperature(int stopTp) {
        if (stopTp > 1000)
            stopTp /= 1000;
        this.stopTemperature = stopTp;
        this.startTemperature = stopTemperature - 20 * 1000;

        temperatureSurface = new int[][]{{startTemperature, 2, 100},
                {stopTemperature, 2, 80}};
    }

    public void setPollingTime(long pollingTime) {
        this.pollingTime = pollingTime;
    }

    public void setTask(ITempTask iTempTask) {
        tempTask = iTempTask;
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
                            List<String> thermalInfo = ThermalInfoUtil.getThermalInfo(context);
                            double maxTemperature = -1;
                            for (String info : thermalInfo) {
                                String temp = info.replaceAll("(\\d+).*", "$1").trim();
                                if (TextUtils.isDigitsOnly(temp.replace(".", ""))) {
                                    double dTemp = Double.parseDouble(temp);
                                    if (maxTemperature < dTemp)
                                        maxTemperature = dTemp;
                                }
                            }
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
                            error(context, "TemperatureController|startControl: " + e.getMessage());
                        }
                    }
                    SystemClock.sleep(pollingTime);
                }
            }
        }.start();
    }
}
