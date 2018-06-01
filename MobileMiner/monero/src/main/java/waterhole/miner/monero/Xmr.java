package waterhole.miner.monero;

import java.io.ObjectStreamException;

import waterhole.miner.core.MineCallback;

final class Xmr {

    static {
        System.loadLibrary("monero-miner");
    }

    native void startMine(int thread, int cpuUses, MineCallback callback);

    private Xmr() {
    }

    public static Xmr instance() {
        return Holder.instance;
    }

    private static class Holder {
        private static Xmr instance = new Xmr();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }
}
