package waterhole.miner.core;

/**
 * 通用挖矿回调，供接入方使用，如果有特殊需求，可继承此接口.
 *
 * @author kzw on 2018/03/14.
 */
public interface MineCallback<T> extends NoProGuard {

    /**
     * 开始连接矿池.
     */
    void onConnectPoolBegin();

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
     * 挖矿开始.
     */
    void onMiningStart();

    /**
     * 挖矿停止.
     */
    void onMiningStop();

    /**
     * 挖矿中产生异常.
     *
     * @param error 错误信息
     */
    void onMiningError(String  error);

    /**
     * 挖矿进度回调.
     *
     * @param value 挖矿进度，如 1 sols/s
     */
    void onMiningSpeed(T value);

    /**
     * 提交share到矿池.
     *
     * @param total   总数量
     * @param average 平均进度
     */
    void onSubmitShare(T total, T average);
}
