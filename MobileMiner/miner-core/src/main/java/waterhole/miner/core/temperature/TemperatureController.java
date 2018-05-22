package waterhole.miner.core.temperature;

import android.os.SystemClock;
import android.text.TextUtils;

import java.util.List;

/**
 * 温控任务
 */
public class TemperatureController {

    float stopTemperature = 45 * 1000;
    int startTemperature = 40 * 1000;
    long pollingTime = 1000l;
    long lastStopTime;
    long stopDelay = 5000l;
    public boolean needRun = false;

    public ITempTask tempTask;
    boolean isTempTaskRunning;

    public void setTemperature(float stopTp) {
        this.stopTemperature = stopTp;
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
                            if (maxTemperature > stopTemperature && isTempTaskRunning) {
                                isTempTaskRunning = false;
                                lastStopTime = System.currentTimeMillis();
                                tempTask.stop();
                            }
                            if (maxTemperature < startTemperature && !isTempTaskRunning && (System.currentTimeMillis() - lastStopTime > stopDelay)) {
                                isTempTaskRunning = true;
                                tempTask.start();
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
