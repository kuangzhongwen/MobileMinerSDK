package waterhole.miner.core.analytics;

import java.io.Serializable;

public final class AnalyticsMining implements Serializable {

    // 挖矿id
    public long mineId;
    // 币种
    public String coin;
    // 使用的线程数
    public int cpuUseThreads;
    // cpu使用率
    public int cpuUses;
    // 挖矿场景 0 normal, 1 夜间挖矿模式
    public int scene;
    // 速度
    public double speed;
    // 温度
    public double temperature;
    // 挖矿时间戳
    public long miningTime;

    @Override
    public String toString() {
        return "AnalyticsMining{" +
                "mineId=" + mineId +
                ", coin='" + coin + '\'' +
                ", cpuUseThreads=" + cpuUseThreads +
                ", cpuUses=" + cpuUses +
                ", scene=" + scene +
                ", speed=" + speed +
                ", temperature=" + temperature +
                ", miningTime=" + miningTime +
                '}';
    }
}
