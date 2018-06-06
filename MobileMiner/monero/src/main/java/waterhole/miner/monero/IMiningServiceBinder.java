package waterhole.miner.monero;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import waterhole.miner.core.temperature.TemperatureController;

/**
 * @author huwwds on 2018/05/21
 */
public interface IMiningServiceBinder extends IInterface {
    String DESCRIPTOR = "waterhole.miner.monero.IMiningServiceBinder";
    int TRANSACTION_startMine = (IBinder.FIRST_CALL_TRANSACTION + 0);
    int TRANSACTION_stopMine = (IBinder.FIRST_CALL_TRANSACTION + 1);
    int TRANSACTION_setControllerNeedRun = (IBinder.FIRST_CALL_TRANSACTION + 2);
    int TRANSACTION_setTemperature = (IBinder.FIRST_CALL_TRANSACTION + 3);

    void startMine() throws RemoteException;

    void stopMine() throws RemoteException;

    void setControllerNeedRun(boolean needRun) throws RemoteException;

    void setTemperature(int stopTp) throws RemoteException;

    class MiningServiceBinder extends Binder implements IMiningServiceBinder {
        TemperatureController controller;

        MiningServiceBinder() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public void startMine() {
        }

        public void stopMine() {
        }

        public void setControllerNeedRun(boolean needRun) {
            this.controller.needRun = needRun;
        }

        @Override
        public void setTemperature(int stopTp) {
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
                    data.enforceInterface(DESCRIPTOR);
                    startMine();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_stopMine: {
                    data.enforceInterface(DESCRIPTOR);
                    stopMine();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setControllerNeedRun: {
                    data.enforceInterface(DESCRIPTOR);
                    this.setControllerNeedRun(data.readInt() == 1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_setTemperature: {
                    data.enforceInterface(DESCRIPTOR);
                    setTemperature(data.readInt());
                    reply.writeNoException();
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
            if (((iin != null) && (iin instanceof IMiningServiceBinder))) {
                return (IMiningServiceBinder) iin;
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
            public void startMine() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_startMine, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void stopMine() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_stopMine, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setControllerNeedRun(boolean needRun) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(needRun ? 1 : 0);
                    mRemote.transact(TRANSACTION_setControllerNeedRun, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void setTemperature(int stopTp) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(stopTp * 1000);
                    mRemote.transact(TRANSACTION_setTemperature, _data, _reply, 0);
                    _reply.readException();
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
}
