package waterhole.miner.core.config;

import java.io.Serializable;

/**
 * example:
 *
 * 然后如果在充电，就唤醒cpu去挖，消耗15%的电，如果未充电，则判断电量是否低于30%，如果低于则不挖，高于则挖10%的电
 */
public final class NightConfig implements Serializable {

    // 是否开启夜间挖矿模式 - 夜间挖矿需要考虑是否锁屏，需要唤醒cpu，是否联网了
    public boolean enableNightDaemon;

    // 夜挖开始时间戳，只判断小时
    public long nightStartupTime;

    // 未充电的耗电量
    public int consumerPower;

    // 充电时的耗电量
    public int consumerChargingPower;

    // 未充电挖矿的最低电量
    public int minPower;

    @Override
    public String toString() {
        return "NightConfigObject{" +
                "enableNightDaemon=" + enableNightDaemon +
                ", nightStartupTime=" + nightStartupTime +
                ", consumerPower=" + consumerPower +
                ", consumerChargingPower=" + consumerChargingPower +
                ", minPower=" + minPower +
                '}';
    }
}
