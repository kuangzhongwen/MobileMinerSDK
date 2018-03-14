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
            System.loadLibrary("zcash-miner");
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    private native static void execGpuMining();

    public static void startMining() {
        AsyncTaskAssistant.executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                // test pool socket
                SocketManager socketManager = SocketManager.instance();
                socketManager.connect();
                socketManager.sendMessage("{\"id\": 2, \"params\": [\"silentarmy\", null, " +
                        "\"zec-cn.waterhole.xyz\", \"3443\"]," +
                        " \"method\": \"mining.subscribe\"}");

                execGpuMining();
            }
        });
    }
}
