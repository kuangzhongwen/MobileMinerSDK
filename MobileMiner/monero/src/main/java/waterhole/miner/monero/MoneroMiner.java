package waterhole.miner.monero;

import java.io.ObjectStreamException;

import waterhole.miner.core.MineCallback;
import waterhole.miner.core.CommonMinerIterface;

public final class MoneroMiner implements CommonMinerIterface<MineCallback> {

    static {
        // load library
    }

    private MineCallback mMineCallback;

    private MoneroMiner() {
    }

    public static MoneroMiner instance() {
        return Holder.instance;
    }

    private static class Holder {
        static MoneroMiner instance = new MoneroMiner();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    @Override
    public CommonMinerIterface<MineCallback> setMineCallback(MineCallback callback) {
        mMineCallback = callback;
        return this;
    }

    @Override
    public void startMine() {

    }

    @Override
    public void stopMine() {

    }
}
