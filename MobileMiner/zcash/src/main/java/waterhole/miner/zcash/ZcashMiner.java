package waterhole.miner.zcash;

import java.io.ObjectStreamException;

import waterhole.miner.core.GPUMinerCallback;
import waterhole.miner.core.IMinerAPI;
import waterhole.miner.core.SocketManager;
import waterhole.miner.core.StopMingCallback;

import static waterhole.commonlibs.utils.LogUtils.printStackTrace;
import static waterhole.commonlibs.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.commonlibs.utils.Preconditions.checkNotNull;
import static waterhole.commonlibs.utils.Preconditions.checkOnChildThread;
import static waterhole.commonlibs.utils.Preconditions.checkOnMainThread;

/**
 * Zcash挖矿类.
 *
 * @author kzw on 2018/03/12.
 */
public final class ZcashMiner implements IMinerAPI<GPUMinerCallback> {

    static {
        try {
            System.loadLibrary("zcash-miner");
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    private native static void execGpuMining();

    private ZcashMiner() {
    }

    public static ZcashMiner instance() {
        return Holder.instance;
    }

    private static class Holder {
        static ZcashMiner instance = new ZcashMiner();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    @Override
    public void startMine(GPUMinerCallback callback) {
        checkOnChildThread();
        checkNotNull(callback);

        // test pool socket
        SocketManager socketManager = SocketManager.instance();
        socketManager.connect();
        socketManager.sendMessage("{\"id\": 2, \"params\": [\"silentarmy\", null, " +
                "\"zec-cn.waterhole.xyz\", \"3443\"]," +
                " \"method\": \"mining.subscribe\"}");

        execGpuMining();
    }

    @Override
    public void startMineAsyn(final GPUMinerCallback callback) {
        checkOnMainThread();
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                startMine(callback);
            }
        });
    }

    @Override
    public void stopMine(StopMingCallback callback) {
        checkOnChildThread();
    }

    @Override
    public void stopMineAsyn(final StopMingCallback callback) {
        checkOnMainThread();
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                stopMine(callback);
            }
        });
    }
}
