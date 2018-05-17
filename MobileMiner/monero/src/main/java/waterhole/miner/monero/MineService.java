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
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import waterhole.miner.monero.temperature.ITempTask;
import waterhole.miner.monero.temperature.TemperatureController;

import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;

public class MineService extends Service implements ITempTask {

    TemperatureController temperatureController;

    @Override
    public void start() {
        startMining();
        Log.e("huwwds", ">>>>>>>>>>>start mine");
    }

    @Override
    public void stop() {
        stopMining();
        Log.e("huwwds", ">>>>>>>>>>>stop mine");
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

    public void startMining() {
//        OldXmr.instance().setContext(getApplicationContext());
//        OldXmr.instance().startMine();
        if (temperatureController == null) {
            temperatureController = new TemperatureController();
            temperatureController.setTask(this);
            temperatureController.startControl();
        } else {
            executeOnThreadPool(new Runnable() {
                @Override
                public void run() {
                    NewXmr newXmr = NewXmr.instance();
                    newXmr.startMine(XmrMiner.instance().getMineCallback());
                }
            });
        }
    }

    public void stopMining() {
//        OldXmr.instance().stopMine();

        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                NewXmr.instance().stopMine();
            }
        });
    }
}
