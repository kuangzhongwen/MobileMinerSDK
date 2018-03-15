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
    private final ZcashMiner mZcashMiner = ZcashMiner.instance();

    // 避免重复启动
    protected final AtomicBoolean isRunningMine = new AtomicBoolean(false);

    static {
        try {
            System.loadLibrary("zcash-miner");
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    private native void startJNIMine(String packName, MineCallback callback);

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
                Context context = mZcashMiner.getContext();
                KernelCopy.copy(context, KERNEL_FILENAME);

                SocketManager socketManager = SocketManager.instance();
                socketManager.connect();
                socketManager.sendMessage("{\"id\": 2, \"params\": [\"silentarmy\", null, " +
                        "\"zec-cn.waterhole.xyz\", \"3443\"]," +
                        " \"method\": \"mining.subscribe\"}");

                if (!isRunningMine.get()) {
                    startJNIMine(context.getPackageName(), mZcashMiner.getMineCallback());
                    isRunningMine.set(true);
                }
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopJNIMine();
        isRunningMine.set(false);
        mZcashMiner.getMineCallback().onMiningStop();
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
