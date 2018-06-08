package waterhole.miner.core.analytics;

import java.io.Serializable;

public final class AnalyticsInit implements Serializable {

    // 设备id
    public String deviceId;
    // sdk版本
    public String sdkVersion;
    // app包名
    public String appPackageName;
    // app名称
    public String appName;
    // app版本
    public String appVersionName;
    // 开始时间
    public long startTime;

    @Override
    public String toString() {
        return "AnalyticsInit{" +
                "deviceId='" + deviceId + '\'' +
                ", sdkVersion='" + sdkVersion + '\'' +
                ", appPackageName='" + appPackageName + '\'' +
                ", appName='" + appName + '\'' +
                ", appVersionName='" + appVersionName + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
