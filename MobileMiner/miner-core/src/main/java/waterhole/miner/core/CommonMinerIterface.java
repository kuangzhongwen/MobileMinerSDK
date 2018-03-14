package waterhole.miner.core;

/**
 * 手机挖矿接口，各个币挖矿实现接口需继承此类.
 *
 * @author kzw on 2018/03/14.
 */
public interface CommonMinerIterface extends NoProGuard {

    CommonMinerIterface setMineCallback(MineCallback callback);

    void startMine();

    void stopMine();
}
