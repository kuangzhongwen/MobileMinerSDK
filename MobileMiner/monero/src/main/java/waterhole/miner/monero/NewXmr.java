package waterhole.miner.monero;

import java.io.ObjectStreamException;

final class NewXmr {

    static {
        System.loadLibrary("monero-miner");
    }

    native void startMine();

    native void stopMine();

    private NewXmr() {
    }

    public static NewXmr instance() {
        return Holder.instance;
    }

    private static class Holder {
        private static NewXmr instance = new NewXmr();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }
}
