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
    // 上报时间
    public String reportTime;
    // 速度
    public double speed;

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
                ", reportTime='" + reportTime + '\'' +
                ", speed=" + speed +
                '}';
    }
}
