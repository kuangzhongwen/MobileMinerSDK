package waterhole.miner.core;

import android.content.Context;

/**
 * 手机挖矿接口，各个币挖矿实现接口需继承此类.
 *
 * @author kzw on 2018/03/14.
 */
public interface CommonMinerIterface extends NoProGuard {

    CommonMinerIterface setContext(Context context);

    CommonMinerIterface setMineCallback(MineCallback callback);

    Context getContext();

    MineCallback getMineCallback();

    void startMine();

    void stopMine();
}
