package waterhole.miner.core;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import waterhole.miner.core.utils.LogUtils;

/**
 * 通用挖矿回调，供接入方使用，如果有特殊需求，可继承此接口.
 *
 * @author kzw on 2018/03/14.
 */
public interface MineCallback extends IInterface {

    abstract class Stub extends Binder implements MineCallback {
        private static final String DESCRIPTOR = "waterhole.miner.core.MineCallback";

        static final int TRANSACTION_onConnectPoolBegin = (IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_onConnectPoolSuccess = (IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_onConnectPoolFail = (IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_onPoolDisconnect = (IBinder.FIRST_CALL_TRANSACTION + 3);
        static final int TRANSACTION_onMessageFromPool = (IBinder.FIRST_CALL_TRANSACTION + 4);
        static final int TRANSACTION_onMiningError = (IBinder.FIRST_CALL_TRANSACTION + 5);
        static final int TRANSACTION_onMiningStatus = (IBinder.FIRST_CALL_TRANSACTION + 6);

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static MineCallback asInterface(IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof Stub))) {
                return (Stub) iin;
            }
            return new Stub.Proxy(obj);
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_onConnectPoolBegin: {
                    data.enforceInterface(DESCRIPTOR);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onConnectPoolSuccess: {
                    data.enforceInterface(DESCRIPTOR);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onConnectPoolFail: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = data.readString();
                    } else {
                        _arg0 = null;
                    }
                    this.onConnectPoolFail(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onPoolDisconnect: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = data.readString();
                    } else {
                        _arg0 = null;
                    }
                    this.onPoolDisconnect(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onMessageFromPool: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = data.readString();
                    } else {
                        _arg0 = null;
                    }
                    this.onMessageFromPool(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onMiningError: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = data.readString();
                    } else {
                        _arg0 = null;
                    }
                    this.onMiningError(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onMiningStatus: {
                    data.enforceInterface(DESCRIPTOR);
                    double _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = data.readDouble();
                    } else {
                        _arg0 = -1;
                    }
                    this.onMiningStatus(_arg0);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        public IBinder asBinder() {
            return this;
        }

        private static class Proxy extends MineCallback.Stub {
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
            public void onConnectPoolBegin() {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_onConnectPoolBegin, _data, _reply, 0);
                    _reply.readException();
                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void onConnectPoolSuccess() {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(TRANSACTION_onConnectPoolSuccess, _data, _reply, 0);
                    _reply.readException();
                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void onConnectPoolFail(String error) {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((error != null)) {
                        _data.writeInt(1);
                        _data.writeString(error);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(TRANSACTION_onConnectPoolFail, _data, _reply, 0);
                    _reply.readException();
                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void onPoolDisconnect(String error) {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((error != null)) {
                        _data.writeInt(1);
                        _data.writeString(error);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(TRANSACTION_onPoolDisconnect, _data, _reply, 0);
                    _reply.readException();
                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void onMessageFromPool(String message) {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((message != null)) {
                        _data.writeInt(1);
                        _data.writeString(message);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(TRANSACTION_onMessageFromPool, _data, _reply, 0);
                    _reply.readException();
                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void onMiningError(String error) {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((error != null)) {
                        _data.writeInt(1);
                        _data.writeString(error);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(TRANSACTION_onMiningError, _data, _reply, 0);
                    _reply.readException();
                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void onMiningStatus(double speed) {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((speed != -1)) {
                        _data.writeInt(1);
                        _data.writeDouble(speed);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(TRANSACTION_onMiningStatus, _data, _reply, 0);
                    _reply.readException();
                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }

    /**
     * 开始连接矿池.
     */
    void onConnectPoolBegin() throws RemoteException;

    /**
     * 连接矿池成功.
     */
    void onConnectPoolSuccess();

    /**
     * 连接矿池失败.
     *
     * @param error 错误信息
     */
    void onConnectPoolFail(String error);

    /**
     * 与矿池连接断开.
     *
     * @param error 错误信息
     */
    void onPoolDisconnect(String error);

    /**
     * 矿池推送的数据.
     *
     * @param message 下发的消息
     */
    void onMessageFromPool(String message);

    /**
     * 挖矿中产生异常.
     *
     * @param error 错误信息
     */
    void onMiningError(String error);

    /**
     * 挖矿进度回调.
     *
     * @param speed 挖矿速度
     */
    void onMiningStatus(double speed);

    /**
     * @author huwwds on 2018/05/21
     */
    class CallbackService {
        static void setCallBack() {

        }

        public class CallbackBinder extends Stub {

            @Override
            public void onConnectPoolBegin() throws RemoteException {

            }

            @Override
            public void onConnectPoolSuccess() {

            }

            @Override
            public void onConnectPoolFail(String error) {

            }

            @Override
            public void onPoolDisconnect(String error) {

            }

            @Override
            public void onMessageFromPool(String message) {

            }

            @Override
            public void onMiningError(String error) {

            }

            @Override
            public void onMiningStatus(double speed) {

            }
        }
    }
}
