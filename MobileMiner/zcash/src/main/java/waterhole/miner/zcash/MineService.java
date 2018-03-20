package waterhole.miner.zcash;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.concurrent.atomic.AtomicBoolean;

import waterhole.miner.core.KernelCopy;
import waterhole.miner.core.MineCallback;
import waterhole.miner.core.NoProGuard;
import waterhole.miner.core.minePool.SocketManager;

import static waterhole.miner.core.utils.LogUtils.printStackTrace;
import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;

/**
 * 挖矿后台服务.
 *
 * @author kzw on 2018/03/14.
 */
public final class MineService extends Service implements NoProGuard {

    // kernel文件名
    private static final String KERNEL_FILENAME = "zcash.kernel";

    // ZcashMiner实例对象
    public final ZcashMiner mZcashMiner = ZcashMiner.instance();

    // 避免重复启动
    protected final AtomicBoolean isRunningMine = new AtomicBoolean(false);

    //矿池通讯实例
    public static MinerPoolCommunicator mMinerPoolCommunicator;

    static {
        try {
            System.loadLibrary("zcash-miner");
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    public native void startJNIMine(String packName, MineCallback callback);

    private native void stopJNIMine();

    public native void writeJob(String job);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mMinerPoolCommunicator == null)
            mMinerPoolCommunicator = new MinerPoolCommunicator(this);
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                try {
                    Context context = mZcashMiner.getContext();
                    KernelCopy.copy(context, KERNEL_FILENAME);

                    mMinerPoolCommunicator.startCommunicate();

//                    if (!isRunningMine.get()) {
//                        startJNIMine(context.getPackageName(), mZcashMiner.getMineCallback());
//                        isRunningMine.set(true);
//                    }
                } catch (Exception e) {
                    MineCallback callback = mZcashMiner.getMineCallback();
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
        mMinerPoolCommunicator.disconnect();
        isRunningMine.set(false);
        MineCallback callback = mZcashMiner.getMineCallback();
        if (callback != null) {
            callback.onMiningStop();
        }
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
