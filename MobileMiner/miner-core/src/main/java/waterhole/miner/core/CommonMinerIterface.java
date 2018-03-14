package waterhole.miner.core;

import waterhole.miner.core.annotation.ExcuteOnAsyn;

/**
 * 手机挖矿接口，各个币挖矿实现接口需继承此类.
 *
 * @author kzw on 2018/03/14.
 */
public interface CommonMinerIterface<Callback extends MineCallback> extends NoProGuard {

    CommonMinerIterface<Callback> setMineCallback(Callback callback);

    @ExcuteOnAsyn
    void startMine();

    @ExcuteOnAsyn
    void stopMine();
}
