package waterhole.miner.core.analytics;

final class AnalyticsMining {

    // 挖矿id
    long mineId;
    // 币种
    String coin;
    // 使用的线程数
    int cpuUseThreads;
    // cpu使用率
    int cpuUses;
    // 挖矿场景 normal 正常模式, night 夜间挖矿模式
    String scene;
    // 速度
    double speed;
    // 温度
    double temperature;
    // 挖矿时间戳
    long miningTime;

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
