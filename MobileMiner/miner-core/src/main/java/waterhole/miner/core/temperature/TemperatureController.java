package waterhole.miner.core.temperature;

import android.os.SystemClock;
import android.text.TextUtils;

import java.util.List;

/**
 * 温控任务
 */
public class TemperatureController {

    int stopTemperature = 60 * 1000;
    int startTemperature = 40 * 1000;
    long pollingTime = 1000l;
    long lastStopTime;
    long stopDelay = 5000l;
    public boolean needRun = false;
    public ITempTask tempTask;
    boolean isTempTaskRunning;
    int curUsage;

    int[][] temperatureSurface = {{startTemperature, Runtime.getRuntime().availableProcessors() > 1 ? Runtime.getRuntime().availableProcessors() - 1 : 1, 100}, {stopTemperature, Runtime.getRuntime().availableProcessors() > 2 ? Runtime.getRuntime().availableProcessors() - 2 : 1, 80}};

    public void setTemperature(int stopTp) {
        this.stopTemperature = stopTp;
        this.startTemperature = stopTemperature - 10 * 1000;

        temperatureSurface = new int[][]{{startTemperature, Runtime.getRuntime().availableProcessors() > 1 ? Runtime.getRuntime().availableProcessors() - 1 : 1, 100}, {stopTemperature, Runtime.getRuntime().availableProcessors() > 2 ? Runtime.getRuntime().availableProcessors() - 2 : 1, 80}};
    }

    public void setPollingTime(long pollingTime) {
        this.pollingTime = pollingTime;
    }

    public void setTask(ITempTask iTempTask) {
        tempTask = iTempTask;
    }

    public void startControl() {
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
                            if (((maxTemperature > temperatureSurface[1][0] && curUsage != temperatureSurface[1][2]) || (maxTemperature < temperatureSurface[0][0] && curUsage != temperatureSurface[0][2])) && isTempTaskRunning) {
                                isTempTaskRunning = false;
                                lastStopTime = System.currentTimeMillis();
                                tempTask.stop();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    SystemClock.sleep(pollingTime);
                }
            }
        }.start();
    }
}
