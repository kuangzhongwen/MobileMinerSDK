package waterhole.miner.monero;

import android.app.Activity;
import android.os.Bundle;

import waterhole.miner.core.asyn.AsyncTaskAssistant;

public class NewXmrMinerActivity extends Activity {

    static {
        System.loadLibrary("monero-miner");
    }

    private native void startMine();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_xmr_miner);

        AsyncTaskAssistant.executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                startMine();
            }
        });
    }
}
