package waterhole.miner.core;

import android.os.RemoteException;

/**
 * @author huwwds on 2018/05/23
 */
public interface StateObserver {
    /**
     * 开始连接矿池.
     */
    void onConnectPoolBegin() throws RemoteException;

    /**
     * 连接矿池成功.
     */
    void onConnectPoolSuccess() throws RemoteException;

    /**
     * 连接矿池失败.
     *
     * @param error 错误信息
     */
    void onConnectPoolFail(String error) throws RemoteException;

    /**
     * 与矿池连接断开.
     *
     * @param error 错误信息
     */
    void onPoolDisconnect(String error) throws RemoteException;

    /**
     * 矿池推送的数据.
     *
     * @param message 下发的消息
     */
    void onMessageFromPool(String message) throws RemoteException;

    /**
     * 挖矿中产生异常.
     *
     * @param error 错误信息
     */
    void onMiningError(String error) throws RemoteException;

    /**
     * 挖矿进度回调.
     *
     * @param speed 挖矿速度
     */
    void onMiningStatus(double speed) throws RemoteException;
}
