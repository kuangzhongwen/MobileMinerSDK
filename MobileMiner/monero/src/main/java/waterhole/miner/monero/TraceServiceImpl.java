package waterhole.miner.monero;

import android.content.*;
import android.os.*;
import android.preference.PreferenceManager;

import java.util.concurrent.*;

import io.reactivex.*;
import io.reactivex.disposables.*;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import waterhole.miner.core.StateObserver;
import waterhole.miner.core.asyn.AsyncTaskListener;
import waterhole.miner.core.config.NightConfig;
import waterhole.miner.core.config.NightConfiguration;
import waterhole.miner.core.keepAlive.AbsWorkService;

import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.LogUtils.info;

public final class TraceServiceImpl extends AbsWorkService {

    // 是否任务完成, 不再需要服务运行
    public static boolean sShouldStopService;
    public static Disposable sDisposable;

    private NightConfig nightConfig;

    private int startBatteryLevel;

    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                info("KeepAliveWatchDog: battery level = " + level + " ,battery status = " + status);

                if (nightConfig != null) {
                    startBatteryLevel = startBatteryLevel == 0 ? level : 0;
                    info("KeepAliveWatchDog: startBatteryLevel = " + startBatteryLevel);
                    int consumer = startBatteryLevel - level;
                    switch (status) {
                        case BatteryManager.BATTERY_STATUS_CHARGING: {
                            info("KeepAliveWatchDog: isCharging consumer = " + consumer);
                            if (consumer >= nightConfig.consumerChargingPower) {
                                resetMiner(context);
                            }
                        }
                            break;
                        default: {
                            info("KeepAliveWatchDog: notCharging consumer = " + consumer);
                            if (startBatteryLevel <= nightConfig.minPower || consumer >= nightConfig.consumerPower) {
                                resetMiner(context);
                            }
                        }
                            break;
                    }
                } else {
                    startBatteryLevel = 0;
                }
            }
        }

        private void resetMiner(Context context) {
            nightConfig = null;
            startBatteryLevel = 0;
            stopMine();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        registerReceiver(mBatteryReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mBatteryReceiver);
    }

    public static void stopService() {
        sShouldStopService = true;
        // 取消对任务的订阅
        if (sDisposable != null) sDisposable.dispose();
        // 取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     *
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        info("KeepAlive: check whether the data stored in the disk is saved during the last destruction.");
        sDisposable = Observable
                .interval(30, TimeUnit.SECONDS)
                // 取消任务时取消定时唤醒
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        info("KeepAliveWatchDog: cancel.");
                        cancelJobAlarmSub();
                    }
                }).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long count) {
                        info("KeepAliveWatchDog: collecting data per 30 seconds... count = " + count);
                        NightConfiguration.instance().getConfigObject(getApplicationContext(),
                                new AsyncTaskListener<NightConfig>() {
                                    @Override
                                    public void runComplete(NightConfig nightConfig) {
                                        if (nightConfig == null) {
                                            info("KeepAliveWatchDog: nightConfig is null");
                                            TraceServiceImpl.this.nightConfig = null;
                                            return;
                                        }
                                        if (!nightConfig.enableNightDaemon) {
                                            info("KeepAliveWatchDog: night mine disable");
                                            TraceServiceImpl.this.nightConfig = null;
                                            return;
                                        }
                                        long current = System.currentTimeMillis() / 1000;
                                        long interval = current - nightConfig.nightStartupTime;
                                        info("KeepAliveWatchDog: current = " + current + " ,interval = " + interval);
                                        if (interval < 0) {
                                            info("KeepAliveWatchDog: not reaching the time of night mine");
                                            TraceServiceImpl.this.nightConfig = null;
                                            return;
                                        }
                                        if (interval > 60 * 60 * 1000) {
                                            info("KeepAliveWatchDog: take an hour out of the night mine");
                                            if (TraceServiceImpl.this.nightConfig != null) {
                                                TraceServiceImpl.this.nightConfig = null;
                                                if (isMining()) {
                                                    stopMine();
                                                }
                                            }
                                            return;
                                        }
                                        if (!isMining()) {
                                            info("KeepAliveWatchDog: start night mine");
                                            TraceServiceImpl.this.nightConfig = nightConfig;
                                            startMine();
                                        } else {
                                            info("KeepAliveWatchDog: is mining");
                                        }
                                    }
                                });
                    }
                });
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();
    }

    /**
     * 任务是否正在运行?
     *
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        // 若还没有取消订阅, 就说明任务仍在运行.
        return sDisposable != null && !sDisposable.isDisposed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        info("keep alive: save data to disk");
    }

    private void startMine() {
        XmrMiner.instance().init(getApplicationContext()).setStateObserver(new StateObserver() {

            @Override
            public void onConnectPoolBegin() {
                info("onConnectPoolBegin");
            }

            @Override
            public void onConnectPoolSuccess() {
                info("onConnectPoolSuccess");
            }

            @Override
            public void onConnectPoolFail(String error) {
                error("onConnectPoolFail: " + error);
            }

            @Override
            public void onPoolDisconnect(String error) {
                error("onPoolDisconnect: " + error);
            }

            @Override
            public void onMessageFromPool(String message) {
                info("onMessageFromPool: " + message);
            }

            @Override
            public void onMiningError(String error) {
                error("onMiningError = " + error);
            }

            @Override
            public void onMiningStatus(double speed) {
                info("onMiningStatus speed = " + speed);
            }
        }).startMine();
    }

    private void stopMine() {
        XmrMiner.instance().stopMine();
    }

    private boolean isMining() {
        return XmrMiner.instance().isMining();
    }
}
