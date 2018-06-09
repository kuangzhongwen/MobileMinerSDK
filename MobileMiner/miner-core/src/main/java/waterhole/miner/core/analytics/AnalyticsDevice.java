package waterhole.miner.core.analytics;

final class AnalyticsDevice {

    // 设备名称
    String deviceName;
    // 设备系统版本
    String deviceVersion;
    // 唯一标识
    String androidId;
    // abi类型
    String abi;
    // cpu核心线程数
    int cpuCoreThreads;

    @Override
    public String toString() {
        return "AnalyticsDevice{" +
                "deviceName='" + deviceName + '\'' +
                ", deviceVersion='" + deviceVersion + '\'' +
                ", androidId='" + androidId + '\'' +
                ", abi='" + abi + '\'' +
                ", cpuCoreThreads=" + cpuCoreThreads +
                '}';
    }
}
