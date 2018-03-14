package waterhole.miner.zcash;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import waterhole.miner.core.ContextWrapper;
import waterhole.miner.core.KernelCopy;
import waterhole.miner.core.MineCallback;
import waterhole.miner.core.NoProGuard;
import waterhole.miner.core.minePool.SocketManager;

import static java.lang.System.exit;
import static waterhole.miner.core.utils.LogUtils.printStackTrace;
import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;

/**
 * 挖矿后台服务，单开进程运行，在异常或停止挖矿时杀死进程能彻底清理挖矿内存.
 *
 * @author kzw on 2018/03/14.
 */
public final class MineService extends Service implements NoProGuard {

    // 上下文对象
    private final Context mContext = ContextWrapper.getInstance().obtainContext();

    // kernel文件名
    private static final String KERNEL_FILENAME = "zcash.kernel";

    // 传递callback对象的Extra Name
    private static final String EXTRAS_CALLBACK = "waterhole.miner.zcash.callback";

    static {
        try {
            System.loadLibrary("zcash-miner");
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    private native void startJNIMine(String packName, MineCallback callback);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final MineCallback callback = (MineCallback) intent.getSerializableExtra(EXTRAS_CALLBACK);
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                KernelCopy.copy(KERNEL_FILENAME);

                SocketManager socketManager = SocketManager.instance();
                socketManager.connect();
                socketManager.sendMessage("{\"id\": 2, \"params\": [\"silentarmy\", null, " +
                        "\"zec-cn.waterhole.xyz\", \"3443\"]," +
                        " \"method\": \"mining.subscribe\"}");

                startJNIMine(mContext.getPackageName(), callback);
            }
        });
        return START_STICKY;
    }

    public static void startService(Context context, MineCallback callback) {
        if (context != null) {
            Intent intent = new Intent(context, MineService.class);
            intent.putExtra(EXTRAS_CALLBACK, callback);
            context.startService(intent);
        }
    }

    public static void stopService(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, MineService.class);
            context.stopService(intent);
            // 退出进程
            exit(0);
        }
    }
}
