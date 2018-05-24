package waterhole.miner.core;

import android.content.Context;
import android.content.Intent;

import waterhole.miner.core.temperature.ThermalInfoUtil;

import static waterhole.miner.core.utils.Preconditions.checkNotNull;

/**
 * 抽象Miner类. 如没有特殊处理，继承此类即可，否则可选择实现{@link CommonMinerIterface}
 *
 * @author kzw on 2018/03/15.
 */
public abstract class AbstractMiner implements CommonMinerIterface {

    // 上下文对象
    private Context mContext;

    // 挖矿回调
    private StateObserver mMineCallback;

    protected int topTemperature = -1;

    public void setMaxTemperature(int temperature) {
        this.topTemperature = temperature;
    }

    public String getCurrentTemperature() {
        return ThermalInfoUtil.getThermalInfo().get(0);
    }

    @Override
    public CommonMinerIterface setContext(Context context) {
        mContext = context;
        startCallbackServer();
        return this;
    }

    private void startCallbackServer() {
        final Intent callbackIntent = new Intent(mContext, CallbackService.class);
        mContext.startService(callbackIntent);
    }

    @Override
    public Context getContext() {
        if (mContext == null) throw new RuntimeException("Please call setContext(Context context) first");
        return mContext;
    }

    @Override
    public AbstractMiner setStateObserver(StateObserver stateObserver) {
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
