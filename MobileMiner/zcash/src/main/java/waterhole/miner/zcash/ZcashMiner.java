package waterhole.miner.zcash;

import waterhole.commonlibs.NoProGuard;

public final class ZcashMiner implements NoProGuard {

    static {
        try {
            System.loadLibrary("silentarmy");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private native void execSilentarmy();

    public void startMine() {
        execSilentarmy();
    }
}
