package waterhole.miner.zcash;

import java.io.ObjectStreamException;

import waterhole.miner.core.CommonMinerIterface;
import waterhole.miner.core.MineCallback;
import waterhole.miner.core.SocketManager;

import static waterhole.commonlibs.utils.LogUtils.printStackTrace;
import static waterhole.commonlibs.utils.Preconditions.checkNotNull;
import static waterhole.commonlibs.utils.Preconditions.checkOnChildThread;

/**
 * Zcash挖矿类.
 *
 * @author kzw on 2018/03/12.
 */
public final class ZcashMiner implements CommonMinerIterface {

    static {
        try {
            System.loadLibrary("zcash-miner");
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    private native void startJNIMine(MineCallback callback);

    private native void stopJNIMine(MineCallback callback);

    private MineCallback mMineCallback;

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
    public ZcashMiner setMineCallback(MineCallback callback) {
        mMineCallback = callback;
        return this;
    }

    @Override
    public void startMine() {
        checkOnChildThread();
        checkNotNull(mMineCallback, "MineCallback must be not Null");

        SocketManager socketManager = SocketManager.instance();
        socketManager.connect();
        socketManager.sendMessage("{\"id\": 2, \"params\": [\"silentarmy\", null, " +
                "\"zec-cn.waterhole.xyz\", \"3443\"]," +
                " \"method\": \"mining.subscribe\"}");

        startJNIMine(mMineCallback);
    }

    @Override
    public void stopMine() {
        checkOnChildThread();
        checkNotNull(mMineCallback, "MineCallback must be not Null");
        stopJNIMine(mMineCallback);
    }
}
