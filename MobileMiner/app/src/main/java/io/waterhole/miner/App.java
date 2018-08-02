package io.waterhole.miner;

import android.app.Application;

import android.content.Context;
import com.igexin.sdk.PushManager;
import waterhole.miner.core.WaterholeMiner;

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        WaterholeMiner.enableLog(true);
        WaterholeMiner.initApplication(this);

        PushManager.getInstance().initialize(getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance().registerPushIntentService(getApplicationContext(), GetuiIntentService.class);
    }

    public static Context getContext() {
        return context;
    }
}
