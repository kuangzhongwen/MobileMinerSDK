package waterhole.miner.monero;

import java.io.ObjectStreamException;

import waterhole.miner.core.CommonMinerCallback;
import waterhole.miner.core.IMinerAPI;
import waterhole.miner.core.StopMingCallback;

public final class MoneroMiner implements IMinerAPI<CommonMinerCallback> {

    static {
        // load library
    }

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
    public void startMine(CommonMinerCallback callback) {

    }

    @Override
    public void startMineAsyn(CommonMinerCallback callback) {

    }

    @Override
    public void stopMine(StopMingCallback callback) {

    }

    @Override
    public void stopMineAsyn(StopMingCallback callback) {

    }
}
