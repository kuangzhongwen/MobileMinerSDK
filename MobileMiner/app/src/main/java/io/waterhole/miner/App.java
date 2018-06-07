package io.waterhole.miner;

import android.app.Application;

import waterhole.miner.core.AbstractMiner;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AbstractMiner.initApplication(this);
    }
}
