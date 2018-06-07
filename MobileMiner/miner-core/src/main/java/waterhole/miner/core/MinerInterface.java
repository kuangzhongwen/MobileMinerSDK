package waterhole.miner.core;

import android.content.Context;

/**
 * 手机挖矿接口，各个币挖矿实现接口需继承此类.
 *
 * @author kzw on 2018/03/14.
 */
public interface MinerInterface extends NoProGuard {

    MinerInterface init(Context context);

    MinerInterface setStateObserver(StateObserver callback);

    Context getContext();

    StateObserver getStateObserver();

    MinerInterface setMaxTemperature(int temperature);

    void startMine();

    void stopMine();
}
