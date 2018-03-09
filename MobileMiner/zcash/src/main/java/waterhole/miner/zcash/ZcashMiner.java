package waterhole.miner.zcash;

/**
 * Zcash挖矿.
 *
 * @author kzw on 2018/03/09.
 */
public final class ZcashMiner {

    private native void execSlientarmy();

    static {
        try {
            System.loadLibrary("silentarmy");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    public void start() {
        execSlientarmy();
    }
}
