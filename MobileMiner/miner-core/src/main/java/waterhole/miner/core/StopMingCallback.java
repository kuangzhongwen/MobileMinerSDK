package waterhole.miner.core;

import waterhole.commonlibs.NoProGuard;

/**
 * 停止挖矿回调，供接入方使用.
 *
 * @author kzw on 2018/03/14.
 */
public interface StopMingCallback extends NoProGuard {

    /**
     * 停止挖矿成功.
     */
    void onStopSuccess();

    /**
     * 停止挖矿失败
     *
     * @param reason 失败原因
     */
    void onStopFail(String reason);
}
