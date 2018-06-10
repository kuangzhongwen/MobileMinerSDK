package waterhole.miner.core;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import waterhole.miner.core.analytics.AnalyticsWrapper;
import waterhole.miner.core.config.NightConfiguration;
import waterhole.miner.core.controller.ThermalInfoUtil;

import static waterhole.miner.core.utils.Preconditions.checkNotNull;

/**
 * 抽象Miner类. 如没有特殊处理，继承此类即可，否则可选择实现{@link MinerInterface}
 *
 * @author kzw on 2018/03/15.
 */
public abstract class WaterholeMiner implements MinerInterface {

    // 上下文对象
    private Context mContext;
    // 挖矿回调
    private StateObserver mMineCallback;

    protected int topTemperature = -1;

    public static void initApplication(final Application application) {
        if (application == null) {
            throw new IllegalArgumentException("application is null");
        }
        // 获取夜挖配置
        NightConfiguration.instance().fetchConfig(application);
        // 统计设备信息，初始化挖矿数据
        AnalyticsWrapper.initApplication(application);
    }

    public MinerInterface setMaxTemperature(int temperature) {
        topTemperature = temperature;
        return this;
    }

    public double getCurrentTemperature() {
        return ThermalInfoUtil.getCurrentTemperature();
    }

    @Override
    public MinerInterface init(Context context) {
        mContext = context;
        startCallbackServer();
        registerReceiver();
        return this;
    }

    private void registerReceiver() {
        mContext.registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                // 电池温度
                ThermalInfoUtil.batteryTemperature = String.valueOf(intent.getIntExtra("temperature", 0));
            }
        }
    };

    private void startCallbackServer() {
        final Intent callbackIntent = new Intent(mContext, CallbackService.class);
        mContext.startService(callbackIntent);
    }

    @Override
    public Context getContext() {
        if (mContext == null) throw new RuntimeException("Please call init(Context context) first");
        return mContext;
    }

    @Override
    public WaterholeMiner setStateObserver(StateObserver stateObserver) {
        mMineCallback = stateObserver;
        CallbackService.setCallBack(stateObserver);
        return this;
    }

    @Override
    public StateObserver getStateObserver() {
        return mMineCallback;
    }

    protected void asserts() {
        checkNotNull(mMineCallback, "Context must be not Null");
        checkNotNull(mMineCallback, "MineCallback must be not Null");
    }
}
