package waterhole.miner.zcash;

import java.io.ObjectStreamException;

import waterhole.commonlibs.NoProGuard;
import waterhole.commonlibs.annotation.ExcuteOnAsyn;
import waterhole.commonlibs.annotation.ExcuteOnMain;
import waterhole.miner.core.GPUMinerCallback;
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
public final class ZcashMiner implements NoProGuard {

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

    /**
     * 由外部异步去执行挖矿程序.
     *
     * @param callback gpu挖矿回调
     */
    @ExcuteOnAsyn
    public void startMining(GPUMinerCallback callback) {
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

    /**
     * 异步执行挖矿程序.
     *
     * @param callback gpu挖矿回调
     */
    @ExcuteOnMain
    public void startMiningAsyn(final GPUMinerCallback callback) {
        checkOnMainThread();
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                startMining(callback);
            }
        });
    }

    /**
     * 由外部异步去停止执行挖矿程序.
     *
     * @param callback 停止挖矿回调.
     */
    @ExcuteOnAsyn
    public void stopMining(StopMingCallback callback) {
        checkOnChildThread();
    }

    /**
     * 异步停止执行挖矿程序.
     *
     * @param callback 停止挖矿回调.
     */
    @ExcuteOnMain
    public void stopMiningAsyn(final StopMingCallback callback) {
        checkOnMainThread();
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
