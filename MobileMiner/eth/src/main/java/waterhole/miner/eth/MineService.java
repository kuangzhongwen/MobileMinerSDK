package waterhole.miner.eth;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import waterhole.miner.core.KernelCopy;
import waterhole.miner.core.MineCallback;
import waterhole.miner.core.NoProGuard;

import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.miner.core.utils.LogUtils.printStackTrace;

/**
 * 挖矿后台服务.
 *
 * @author kzw on 2018/03/23.
 */
public final class MineService extends Service implements NoProGuard {

    // EthMiner实例对象
    public final EthMiner mEthMiner = EthMiner.instance();

    static {
        try {
            System.loadLibrary("eth-miner");
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    public native void startJNIMine(MineCallback callback);

    private native void stopJNIMine();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                try {
                    startJNIMine(mEthMiner.getMineCallback());
                } catch (Exception e) {
                    MineCallback callback = mEthMiner.getMineCallback();
                    if (callback != null) {
                        callback.onMiningError(e.getMessage());
                    }
                }
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopJNIMine();
    }

    public static void startService(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, MineService.class);
            context.startService(intent);
        }
    }

    public static void stopService(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, MineService.class);
            context.stopService(intent);
        }
    }
}
