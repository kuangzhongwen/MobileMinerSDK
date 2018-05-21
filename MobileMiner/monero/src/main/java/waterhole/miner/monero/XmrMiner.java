package waterhole.miner.monero;

import java.io.ObjectStreamException;

import waterhole.miner.core.AbstractMiner;

public final class XmrMiner extends AbstractMiner {

    static final String LOG_TAG = "Waterhole-XmrMiner";

    private XmrMiner() {
    }

    public static XmrMiner instance() {
        return Holder.instance;
    }

    private static class Holder {
        private static XmrMiner instance = new XmrMiner();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    @Override
    public void startMine() {
        MineService.startService(getContext());
    }

    @Override
    public void stopMine() {
        MineService.stopService(getContext());
    }
}
