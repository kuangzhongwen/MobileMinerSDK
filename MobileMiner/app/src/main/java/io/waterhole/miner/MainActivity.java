package io.waterhole.miner;

import android.app.Activity;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import waterhole.commonlibs.ContextWrapper;
import waterhole.commonlibs.utils.LogUtils;
import waterhole.miner.zcash.ZcashMiner;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ContextWrapper.getInstance().injectContext(getApplicationContext());
        LogUtils.enableDebug(true);

        ZcashMiner.startMine();
    }
}
