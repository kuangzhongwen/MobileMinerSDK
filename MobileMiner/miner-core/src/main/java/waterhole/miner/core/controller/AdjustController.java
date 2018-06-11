package waterhole.miner.core.controller;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import waterhole.miner.core.utils.SpUtil;

/**
 * 自适控制器
 */

public final class AdjustController extends BaseController {

    public static final String ADJUST_CONFIG = "adjust_config";
    private static final String DEFAULT_CONFIG = "default_config";
    public static final int STEP = 10;
    private static final int DEFAULT_REDUCE_FREQ_TEMP = 40;
    private static final int MIN_ADJUST_SPEED = 3;
    public static boolean hasBestConfig;

    @Override
    public void startControl(Context context) {
        SpUtil.config(context);
        new Thread() {
            @Override
            public void run() {
                for (; ; ) {
                    SystemClock.sleep(pollingTime);
                    try {
                        if (fullPower || hasBestConfig) return;
                        if (ThermalInfoUtil.getCurrentTemperature() > TemperatureController.sStopTemperature)
                            continue;
                        if (ThermalInfoUtil.getCurrentTemperature() > DEFAULT_REDUCE_FREQ_TEMP)
                            continue;
                        String default_config = SpUtil.getShareData(DEFAULT_CONFIG, "");
                        if (TextUtils.isEmpty(default_config)) {
                            if (TemperatureController.sCurUsageArr == null) continue;
                            if (TemperatureController.sSpeed < MIN_ADJUST_SPEED) continue;
                            SpUtil.putShareData(DEFAULT_CONFIG, TemperatureController.sCurUsageArr[1]
                                    + "&" + TemperatureController.sCurUsageArr[2] + "&" + TemperatureController.sSpeed);
                        } else {
                            String adjust_config = SpUtil.getShareData(ADJUST_CONFIG, "");
                            if (TextUtils.isEmpty(adjust_config)) {
                                if (TemperatureController.sCurUsageArr[2] > STEP) {
                                    SpUtil.putShareData(ADJUST_CONFIG, TemperatureController.sCurUsageArr[1]
                                            + "&" + (TemperatureController.sCurUsageArr[2] - STEP)
                                            + "&" + TemperatureController.sSpeed);
                                    tempTask.stop();
                                } else {
                                    SpUtil.putShareData(ADJUST_CONFIG, TemperatureController.sCurUsageArr[1]
                                            + "&" + (TemperatureController.sCurUsageArr[2])
                                            + "&" + TemperatureController.sSpeed + "&best");
                                }
                                return;
                            }
                            if (!default_config.contains("&")) continue;
                            if (!adjust_config.contains("&")) continue;
                            String[] default_data = default_config.split("&");
                            String[] adjust_data = adjust_config.split("&");
                            if (adjust_data.length > 3) {
                                hasBestConfig = true;
                                return;
                            }
                            if (TemperatureController.sSpeed < MIN_ADJUST_SPEED)
                                continue;
                            if (TemperatureController.sCurUsageArr[2] > STEP
                                    && Math.abs(Double.parseDouble(default_data[2]) - TemperatureController.sSpeed) < 2) {
                                SpUtil.putShareData(ADJUST_CONFIG, TemperatureController.sCurUsageArr[1]
                                        + "&" + (TemperatureController.sCurUsageArr[2] - STEP)
                                        + "&" + TemperatureController.sSpeed);
                                tempTask.stop();
                                return;
                            } else {
                                SpUtil.putShareData(ADJUST_CONFIG, TemperatureController.sCurUsageArr[1]
                                        + "&" + (TemperatureController.sCurUsageArr[2] + STEP)
                                        + "&" + TemperatureController.sSpeed + "&best");
                                tempTask.stop();
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }
}
