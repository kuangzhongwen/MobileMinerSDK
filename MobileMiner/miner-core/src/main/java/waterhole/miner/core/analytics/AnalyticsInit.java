package waterhole.miner.core.analytics;

import java.io.Serializable;

public final class AnalyticsInit implements Serializable {

    // 设备id
    public int deviceId;
    // sdk版本
    public String sdkVersion;
    // app包名
    public String packageName;
    // app名称
    public String appName;
    // app版本
    public String appVersion;
    // 开始时间
    public long startTime;

    @Override
    public String toString() {
        return "AnalyticsInit{" +
                "deviceId='" + deviceId + '\'' +
                ", sdkVersion='" + sdkVersion + '\'' +
                ", packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
