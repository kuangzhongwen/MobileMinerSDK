package waterhole.miner.monero;

import java.io.ObjectStreamException;

import waterhole.miner.core.AbstractMiner;

public final class MoneroMiner extends AbstractMiner {

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
    public void startMine() {

    }

    @Override
    public void stopMine() {

    }
}
