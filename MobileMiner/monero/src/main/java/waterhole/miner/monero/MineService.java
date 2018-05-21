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

import waterhole.miner.core.CallbackService;
import waterhole.miner.core.MineCallback;
import waterhole.miner.monero.temperature.ITempTask;
import waterhole.miner.monero.temperature.TemperatureController;

import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.miner.core.utils.APIUtils.hasLollipop;
import static waterhole.miner.core.utils.LogUtils.info;
import static waterhole.miner.monero.XmrMiner.LOG_TAG;

public final class MineService extends Service implements ITempTask {

    public static MineService sMineService;

    Handler mMainHandler = new Handler(Looper.getMainLooper());

    public TemperatureController temperatureController;
    private MineCallback mineCallback;

    @Override
    public void start() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                startMine();
            }
        });
    }

    @Override
    public void stop() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                stopMine();
            }
        });
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mineCallback = MineCallback.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        getApplicationContext().bindService(new Intent(this, CallbackService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        temperatureController = new TemperatureController();
        temperatureController.setTask(this);
        temperatureController.startControl();
        sMineService = this;
        IMiningServiceBinder.MiningServiceBinder miningServiceBinder = new IMiningServiceBinder.MiningServiceBinder();
        miningServiceBinder.controller = temperatureController;
        return miningServiceBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sMineService = null;
        stopMine();

        Intent intent = new Intent("waterhole.miner.monero.destroy");
        sendBroadcast(intent);
    }

    void startMine() {
        if (!hasLollipop()) {
            XmrMiner.instance().getMineCallback().onMiningError("Android version must be >= 21");
            return;
        }
        final String cpuABI = Build.CPU_ABI;
        info(LOG_TAG, cpuABI);
        if (!cpuABI.toLowerCase().equals("arm64-v8a")) {
            XmrMiner.instance().getMineCallback().onMiningError("Sorry, this app currently only supports 64 bit architectures, but yours is " + cpuABI);
            // this flag will keep the start button disabled
            return;
        }
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                NewXmr newXmr = NewXmr.instance();
                newXmr.startMine(Runtime.getRuntime().availableProcessors() - 1,
                        99,
                        mineCallback);
            }
        });
    }

    void stopMine() {
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
