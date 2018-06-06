package waterhole.miner.core.analytics;

/**
 * 统计实体类.
 *
 * @author kzw on 2018/06/05.
 */
public final class AnalyticsObject {

    // sdk版本
    public String sdkVersion;
    // 设备名称
    public String deviceName;
    // 设备系统版本
    public String deviceVersion;
    // 唯一标识
    public String androidId;
    // abi类型
    public String abi;
    // cpu核心线程数
    public int cpuThreads;
    // 币种
    public String coin;
    // 开始时间
    public String startTime;
    // 结束时间
    public String endTime;
    // 使用的线程数
    public int useThreads;
    // cpu使用率
    public int cpuUses;
    // 速度
    public double speed;
    // 挖矿场景 normal, charging
    public String scene;

    @Override
    public String toString() {
        return "AnalyticsObject{" +
                "sdkVersion='" + sdkVersion + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceVersion='" + deviceVersion + '\'' +
                ", androidId='" + androidId + '\'' +
                ", abi='" + abi + '\'' +
                ", cpuThreads=" + cpuThreads +
                ", coin='" + coin + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", useThreads=" + useThreads +
                ", cpuUses=" + cpuUses +
                ", speed=" + speed +
                ", scene='" + scene + '\'' +
                '}';
    }
}
