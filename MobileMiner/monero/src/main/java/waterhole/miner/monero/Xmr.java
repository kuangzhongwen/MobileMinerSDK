package waterhole.miner.monero;

import java.io.ObjectStreamException;

import waterhole.miner.core.MineCallback;

import static waterhole.miner.core.utils.LogUtils.error;

final class Xmr {

    static {
        try {
            System.loadLibrary("monero-miner");
        } catch (Exception e) {
            error(e.getMessage());
        }
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
