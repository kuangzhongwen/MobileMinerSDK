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

    /**
     * 如果支持多核对gpu，是否开启多核同时挖.
     *
     * @param use 是否使用，默认false
     */
    CommonMinerIterface useMultGpusIfSupport(boolean use);

    Context getContext();

    MineCallback getMineCallback();

    boolean isUseMultGpusIfSupport();

    void startMine();

    void stopMine();
}
