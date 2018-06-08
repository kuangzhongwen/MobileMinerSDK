package waterhole.miner.eth;

import java.io.ObjectStreamException;

import waterhole.miner.core.WaterholeMiner;

public final class EthMiner extends WaterholeMiner {

    private EthMiner() {
    }

    public static EthMiner instance() {
        return Holder.instance;
    }

    private static class Holder {
        static EthMiner instance = new EthMiner();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    @Override
    public void startMine() {
        asserts();
        MineService.startService(getContext());
    }

    @Override
    public void stopMine() {
        asserts();
        MineService.stopService(getContext());
    }
}
