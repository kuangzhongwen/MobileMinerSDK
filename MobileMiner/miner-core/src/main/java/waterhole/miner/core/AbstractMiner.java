package waterhole.miner.core;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.util.List;

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

    public double getCurrentTemperature() {
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
        return maxTemperature;
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
