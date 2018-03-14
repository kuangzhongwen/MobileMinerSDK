package io.waterhole.miner;

import android.app.Activity;
import android.os.Bundle;

import waterhole.miner.core.ContextWrapper;
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

        // 必须注入context
        ContextWrapper.getInstance().injectContext(getApplicationContext());
        LogUtils.enableDebug(true);

        ZcashMiner.instance().setMineCallback(new MineCallback<Double>() {

            @Override
            public void onConnectPoolBegin() {
                info(TAG, "onConnectPoolBegin");
            }

            @Override
            public void onConnectPoolSuccess() {
                info(TAG, "onConnectPoolSuccess");
            }

            @Override
            public void onConnectPoolFail(int errorCode) {
                /**
                 * errorCode see {@link waterhole.miner.core.ErrorCode}
                 */
                error(TAG, "onConnectPoolFail: " + errorCode);
            }

            @Override
            public void onPoolDisconnect(int errorCode) {
                /**
                 * errorCode see {@link waterhole.miner.core.ErrorCode}
                 */
                error(TAG, "onPoolDisconnect: " + errorCode);
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
            public void onMiningError(int errorCode) {
                /**
                 * errorCode see {@link waterhole.miner.core.ErrorCode}
                 */
                error(TAG, "onMiningError = " + errorCode);
            }

            @Override
            public void onMiningSpeed(Double value) {
                info(TAG, "onMiningSpeed: " + value);
            }

            @Override
            public void onSubmitShare(Double total, Double average) {
                info(TAG, "onSubmitShare: total = " + total + ", average = " + average);
            }
        }).startMine();
    }
}
