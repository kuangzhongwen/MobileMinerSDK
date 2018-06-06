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

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Locale;

import waterhole.miner.core.BuildConfig;
import waterhole.miner.core.CallbackService;
import waterhole.miner.core.MineCallback;
import waterhole.miner.core.analytics.AnalyticsObject;
import waterhole.miner.core.analytics.AnalyticsWrapper;
import waterhole.miner.core.temperature.ITempTask;
import waterhole.miner.core.temperature.TemperatureController;

import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.LogUtils.info;

public final class MineService extends Service implements ITempTask {

    public static MineService sMineService;

    Handler mMainHandler = new Handler(Looper.getMainLooper());

    public TemperatureController temperatureController;
    private MineCallback mineCallback;
    private boolean isMining;

    private IMiningServiceBinder.MiningServiceBinder miningServiceBinder;

    @Override
    public void start(final int[] temperatureSurface) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                startMine(temperatureSurface);
            }
        });
    }

    @Override
    public void stop() {
        Intent intent = new Intent("waterhole.miner.monero.restart");
        sendBroadcast(intent);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mineCallback = MineCallback.Stub.asInterface(service);
            try {
                mineCallback.onConnectPoolBegin();
            } catch (Exception e) {
                error("MineService|ServiceConnection: " + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        info("MineService onBind");
        getApplicationContext().bindService(new Intent(this, CallbackService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        temperatureController = new TemperatureController();
        temperatureController.setTask(this);
        temperatureController.startControl();
        sMineService = this;
        miningServiceBinder = new IMiningServiceBinder.MiningServiceBinder();
        miningServiceBinder.controller = temperatureController;
        return miningServiceBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        info("MineService onDestroy");
        sMineService = null;
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    void startMine(final int[] temperatureSurface) {
        do {
            SystemClock.sleep(100);

            // 统计数据
            AnalyticsObject analyticsObj = new AnalyticsObject();
            analyticsObj.sdkVersion = BuildConfig.VERSION_NAME;
            analyticsObj.deviceName = android.os.Build.MODEL;
            analyticsObj.deviceVersion = android.os.Build.VERSION.RELEASE;
            analyticsObj.androidId = Settings.System.getString(getApplicationContext().getContentResolver(),
                    Settings.System.ANDROID_ID);
            analyticsObj.abi = Build.CPU_ABI;
            analyticsObj.cpuThreads = Runtime.getRuntime().availableProcessors();
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            analyticsObj.startTime = sDateFormat.format(new java.util.Date());
            analyticsObj.cpuThreads = temperatureSurface[1];
            analyticsObj.cpuUses = temperatureSurface[2];
            AnalyticsWrapper.onEvent(getApplicationContext(), analyticsObj);

            if (mineCallback == null)
                continue;
            try {
                if (isMining) {
                    mineCallback.onMiningError("Xmr miner is Running");
                    return;
                }
                final String cpuABI = Build.CPU_ABI;
                info(cpuABI);
                if (!cpuABI.toLowerCase().equals("arm64-v8a")) {
                    mineCallback.onMiningError("Sorry, this app currently only supports 64 bit architectures, but yours is " + cpuABI);
                    // this flag will keep the start button disabled
                    return;
                }
            } catch (Exception e) {
                error("MineService|startMine: " + e.getMessage());
            }
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    executeOnThreadPool(new Runnable() {
                        @Override
                        public void run() {
                            info("MineService startMine : threads=" + temperatureSurface[1] + " ,cpuUse=" + temperatureSurface[2]);

                            isMining = true;
                            Xmr xmr = Xmr.instance();
                            xmr.startMine(miningServiceBinder.walletAddr, temperatureSurface[1], temperatureSurface[2], mineCallback);
                        }
                    });
                }
            });
        } while (mineCallback == null);
    }
}
