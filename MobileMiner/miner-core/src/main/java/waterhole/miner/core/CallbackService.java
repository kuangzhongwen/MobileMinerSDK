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
            sMineCallback.onConnectPoolSuccess();
        }

        @Override
        public void onConnectPoolFail(String error) {
            sMineCallback.onConnectPoolFail(error);
        }

        @Override
        public void onPoolDisconnect(String error) {
            sMineCallback.onPoolDisconnect(error);
        }

        @Override
        public void onMessageFromPool(String message) {
            sMineCallback.onMessageFromPool(message);
        }

        @Override
        public void onMiningError(String error) {
            sMineCallback.onMiningError(error);
        }

        @Override
        public void onMiningStatus(double speed) {
            sMineCallback.onMiningStatus(speed);
        }
    }
}
