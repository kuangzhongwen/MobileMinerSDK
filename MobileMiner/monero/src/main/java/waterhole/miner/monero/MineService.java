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
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;

import waterhole.miner.core.CallbackService;
import waterhole.miner.core.MineCallback;
import waterhole.miner.core.utils.LogUtils;
import waterhole.miner.core.utils.temperature.ITempTask;
import waterhole.miner.core.utils.temperature.TemperatureController;

import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.miner.core.utils.APIUtils.hasLollipop;
import static waterhole.miner.core.utils.LogUtils.info;
import static waterhole.miner.monero.XmrMiner.LOG_TAG;

public final class MineService extends Service implements ITempTask {
    static MineService sMineService;

    Handler mMainHandler = new Handler(Looper.getMainLooper());

    public TemperatureController temperatureController;
    private MineCallback mineCallback;

    private boolean isMining;

    public interface IMiningServiceBinder extends IInterface {
        String DESCRIPTOR = "waterhole.miner.monero.IMiningServiceBinder";
        int TRANSACTION_startMine = (IBinder.FIRST_CALL_TRANSACTION + 0);
        int TRANSACTION_stopMine = (IBinder.FIRST_CALL_TRANSACTION + 1);
        int TRANSACTION_setControllerNeedRun = (IBinder.FIRST_CALL_TRANSACTION + 2);
        int TRANSACTION_setTemperature = (IBinder.FIRST_CALL_TRANSACTION + 3);

        void startMine();

        void stopMine();

        void setControllerNeedRun(boolean needRun);

        void setTemperature(float stopTp);
    }

    public static class MiningServiceBinder extends Binder implements IMiningServiceBinder {
        TemperatureController controller;

        public MiningServiceBinder() {
            this.attachInterface(this, DESCRIPTOR);
        }

        MiningServiceBinder(TemperatureController temperatureController) {
            this.controller = temperatureController;
        }

        public void startMine() {
            sMineService.startMine();
        }

        public void stopMine() {
            sMineService.stopMine();
        }

        public void setControllerNeedRun(boolean needRun) {
            this.controller.needRun = needRun;
        }

        @Override
        public void setTemperature(float stopTp) {
            controller.setTemperature(stopTp);
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_startMine: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_stopMine: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_setControllerNeedRun: {
                    reply.writeString(DESCRIPTOR);
                    setControllerNeedRun(data.readFloat() == 1);
                    return true;
                }
                case TRANSACTION_setTemperature: {
                    reply.writeString(DESCRIPTOR);
                    setTemperature(data.readFloat());
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        public static IMiningServiceBinder asInterface(IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof MiningServiceBinder))) {
                return (MiningServiceBinder) iin;
            }
            return new MiningServiceBinder.Proxy(obj);
        }

        private static class Proxy implements IMiningServiceBinder {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            public Proxy(IBinder obj) {
                this.mRemote = obj;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            @Override
            public void startMine() {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_startMine, _data, _reply, 0);
                    _reply.readException();
                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void stopMine() {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_stopMine, _data, _reply, 0);
                    _reply.readException();
                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setControllerNeedRun(boolean needRun) {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(1);
                    _data.writeFloat(needRun ? 1 : 0);
                    mRemote.transact(TRANSACTION_setControllerNeedRun, _data, _reply, 0);
                    _reply.readException();
                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setTemperature(float stopTp) {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(1);
                    _data.writeFloat(stopTp);
                    mRemote.transact(TRANSACTION_setTemperature, _data, _reply, 0);
                    _reply.readException();
                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override
        public IBinder asBinder() {
            return this;
        }
    }

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
        return new MiningServiceBinder(temperatureController);
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
        if (isMining) {
            XmrMiner.instance().getMineCallback().onMiningError("Xmr miner is Running");
            return;
        }
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
                isMining = true;
                NewXmr newXmr = NewXmr.instance();
                newXmr.startMine(Runtime.getRuntime().availableProcessors() - 1,
                        99,
                        mineCallback);
            }
        });
    }

    void stopMine() {
        isMining = false;
        //      OldXmr.instance().stopMine();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
