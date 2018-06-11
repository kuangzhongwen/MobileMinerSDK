package io.waterhole.miner;

import android.app.Application;

import waterhole.miner.core.WaterholeMiner;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        WaterholeMiner.enableLog(true);
        WaterholeMiner.initApplication(this);
    }
}
