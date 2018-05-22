package waterhole.miner.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * @author huwwds on 2018/05/21
 */
public class CallbackService extends Service {
    static MineCallback sMineCallback;

    public static void setCallBack(MineCallback mineCallback) {
        sMineCallback = mineCallback;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new CallbackBinder();
    }

    public class CallbackBinder extends MineCallback.Stub {

        @Override
        public void onConnectPoolBegin() throws RemoteException {
            sMineCallback.onConnectPoolBegin();
        }

        @Override
        public void onConnectPoolSuccess() {
            try {
                sMineCallback.onConnectPoolSuccess();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectPoolFail(String error) {
            try {
                sMineCallback.onConnectPoolFail(error);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPoolDisconnect(String error) {
            try {
                sMineCallback.onPoolDisconnect(error);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMessageFromPool(String message) {
            try {
                sMineCallback.onMessageFromPool(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMiningError(String error) {
            try {
                sMineCallback.onMiningError(error);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMiningStatus(double speed) {
            try {
                sMineCallback.onMiningStatus(speed);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
