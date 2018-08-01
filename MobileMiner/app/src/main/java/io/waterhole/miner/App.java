package io.waterhole.miner;

import android.app.Application;

import com.igexin.sdk.PushManager;
import waterhole.miner.core.WaterholeMiner;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        WaterholeMiner.enableLog(true);
        WaterholeMiner.initApplication(this);

        PushManager.getInstance().initialize(getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance().registerPushIntentService(getApplicationContext(), GetuiIntentService.class);
    }
}
