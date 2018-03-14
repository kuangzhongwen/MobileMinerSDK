package waterhole.miner.core;

import waterhole.commonlibs.NoProGuard;
import waterhole.commonlibs.annotation.ExcuteOnAsyn;
import waterhole.commonlibs.annotation.ExcuteOnMain;

/**
 * 手机挖矿接口，各个币挖矿实现接口需继承此类.
 *
 * @author kzw on 2018/03/14.
 */
public interface IMinerAPI<Callback extends CommonMinerCallback> extends NoProGuard {

    @ExcuteOnAsyn
    void startMine(Callback callback);

    @ExcuteOnMain
    void startMineAsyn(Callback callback);

    @ExcuteOnAsyn
    void stopMine(StopMingCallback callback);

    @ExcuteOnMain
    void stopMineAsyn(StopMingCallback callback);
}
