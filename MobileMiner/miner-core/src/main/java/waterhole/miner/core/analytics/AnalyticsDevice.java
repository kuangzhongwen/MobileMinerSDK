package waterhole.miner.core.analytics;

import java.io.Serializable;

public final class AnalyticsDevice implements Serializable {

    // 设备名称
    public String deviceName;
    // 设备系统版本
    public String deviceVersion;
    // 唯一标识
    public String androidId;
    // abi类型
    public String abi;
    // cpu核心线程数
    public int cpuCoreThreads;

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
