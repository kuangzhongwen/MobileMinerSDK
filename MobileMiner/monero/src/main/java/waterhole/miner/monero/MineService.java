/*
 *  Monero Miner App (c) 2018 Uwe Post
 *  based on the XMRig Monero Miner https://github.com/xmrig/xmrig
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 * /
 */

package waterhole.miner.monero;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;

import waterhole.miner.core.utils.IOUtils;
import waterhole.miner.core.utils.LogUtils;

public class MineService extends Service {

    private static final String LOG_TAG = "Waterhole-XmrMiner";
    private Process process;
    private String privatePath;
    private OutputReaderThread outputHandler;
    private int accepted;
    private String speed = "./.";

    static {
        System.loadLibrary("monero-miner-new");
    }

    private native void startMineNewXmr();

    @Override
    public void onCreate() {
        super.onCreate();

        // path where we may execute our program
        privatePath = getFilesDir().getAbsolutePath();
    }

    public class MiningServiceBinder extends Binder {
        public MineService getService() {
            return MineService.this;
        }
    }

    @Override
    public void onDestroy() {
        stopMining();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MiningServiceBinder();
    }

    public void stopMining() {
        if (outputHandler != null) {
            outputHandler.interrupt();
            outputHandler = null;
        }
        if (process != null) {
            process.destroy();
            process = null;
            LogUtils.info(LOG_TAG, "stopped");
        }
    }

    public void startMining() {
        LogUtils.info(LOG_TAG, "starting...");
        if (process != null) {
            process.destroy();
        }

        try {
            // write the config
            writeOldConfig(privatePath);

            // run xmrig using the config
            String[] args = {"./xmrig"};
            ProcessBuilder pb = new ProcessBuilder(args);
            // in our directory
            pb.directory(getApplicationContext().getFilesDir());
            // with the directory as ld path so xmrig finds the libs
            pb.environment().put("LD_LIBRARY_PATH", privatePath);
            // in case of errors, read them
            pb.redirectErrorStream();

            accepted = 0;
            // run it!
            process = pb.start();
            // start processing xmrig's output
            outputHandler = new OutputReaderThread(process.getInputStream());
            outputHandler.start();
        } catch (Exception e) {
            LogUtils.error(LOG_TAG, "exception:", e);
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            process = null;
        }

    }

    public String getSpeed() {
        return speed;
    }

    public int getAccepted() {
        return accepted;
    }

    public String getOutput() {
        if (outputHandler != null && outputHandler.getOutput() != null)
            return outputHandler.getOutput().toString();
        else return "";
    }

    public int getAvailableCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * thread to collect the binary's output
     */
    @TargetApi(19)
    private class OutputReaderThread extends Thread {

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
                        accepted++;
                    } else if (line.contains("speed")) {
                        String[] split = TextUtils.split(line, " ");
                        speed = split[split.length - 2];
                    }
                    LogUtils.info(LOG_TAG, "accepted = " + accepted + " ,speed = " + speed);
                }
            } catch (IOException e) {
                LogUtils.error(LOG_TAG, "exception", e);
            }
        }

        public StringBuilder getOutput() {
            return output;
        }
    }

    private void writeOldConfig(String privatePath) {
        String config = "* {\n" +
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
                "         \"threads\": 4,\n" +
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
        FileOutputStream outStream = null;
        try {
            File file = new File(privatePath + "/config.json");
            outStream = new FileOutputStream(file);
            outStream.write(config.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeSafely(outStream);
        }
    }
}
