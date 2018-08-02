package waterhole.miner.monero.keepappalive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import waterhole.miner.core.utils.LogUtils;
import waterhole.miner.monero.keepappalive.utils.SystemUtils;

/**
 * 监听系统广播，复活进程
 * (1) 网络变化广播
 * (2) 屏幕解锁广播
 * (3) 应用安装卸载广播
 * (4) 开机广播
 */
public class KeepAliveReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.info("AliveBroadcastReceiver---->接收到的系统广播："+action);
        if(SystemUtils.isAPPALive(context)){
            LogUtils.info("AliveBroadcastReceiver---->APP还是活着的");
            return;
        }
        LogUtils.info("AliveBroadcastReceiver---->复活进程(APP)");
    }
}
