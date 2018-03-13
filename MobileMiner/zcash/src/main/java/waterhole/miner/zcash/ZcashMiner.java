package waterhole.miner.zcash;

import waterhole.commonlibs.NoProGuard;
import waterhole.commonlibs.asyn.AsyncTaskAssistant;

import static waterhole.commonlibs.utils.LogUtils.printStackTrace;

/**
 * Zcash挖矿接口类.
 *
 * @author kzw on 2018/03/12.
 */
public final class ZcashMiner implements NoProGuard {

    static {
        try {
            System.loadLibrary("silentarmy");
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    private native static void execSilentarmy();

    public static void startMine() {
        AsyncTaskAssistant.executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                KernelTools.copyKernel();
//                execSilentarmy();

                // test pool socket
                SocketManager socketManager = SocketManager.instance();
                socketManager.connect();
                socketManager.sendMessage("{\"id\": 2, \"params\": [\"silentarmy\", null, " +
                        "\"us1-zcash.flypool.org\", \"3333\"]," +
                        " \"method\": \"mining.subscribe\"}");

                execSilentarmy();
            }
        });
    }
}
