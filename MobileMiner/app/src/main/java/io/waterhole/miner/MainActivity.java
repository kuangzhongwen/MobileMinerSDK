package io.waterhole.miner;

import android.app.Activity;
import android.os.Bundle;

import waterhole.commonlibs.ContextWrapper;
import waterhole.commonlibs.utils.LogUtils;
import waterhole.miner.core.GPUMinerCallback;
import waterhole.miner.zcash.ZcashMiner;

import static waterhole.commonlibs.utils.LogUtils.info;
import static waterhole.commonlibs.utils.LogUtils.error;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ContextWrapper.getInstance().injectContext(getApplicationContext());
        LogUtils.enableDebug(true);

        ZcashMiner.instance().startMiningAsyn(new GPUMinerCallback<Double>() {
            @Override
            public void onLoadOpenCLSuccess() {
                info(TAG, "onLoadOpenCLSuccess");
            }

            @Override
            public void onLoadOpenCLFail(String reason) {
                error(TAG, "onLoadOpenCLFail : " + reason);
            }

            @Override
            public void onConnectPoolBegin() {
                info(TAG, "onConnectPoolBegin");
            }

            @Override
            public void onConnectPoolSuccess() {
                info(TAG, "onConnectPoolSuccess");
            }

            @Override
            public void onConnectPoolFail(String reason) {
                info(TAG, "onConnectPoolFail : " + reason);
            }

            @Override
            public void onPoolDisconnect() {
                error(TAG, "onPoolDisconnect");
            }

            @Override
            public void onMessageFromPool(String message) {
                info(TAG, "onMessageFromPool : " + message);
            }

            @Override
            public void onMiningSpeed(Double value) {
                info(TAG, "onMiningSpeed : " + value);
            }

            @Override
            public void onSubmitShare(Double total, Double average) {
                info(TAG, "onSubmitShare total = " + total + ", average = " + average);
            }
        });
    }
}
