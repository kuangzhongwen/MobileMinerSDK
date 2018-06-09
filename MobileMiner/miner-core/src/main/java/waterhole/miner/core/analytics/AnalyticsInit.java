package waterhole.miner.core.analytics;

final class AnalyticsInit {

    // 设备id
    int deviceId;
    // sdk版本
    String sdkVersion;
    // app包名
    String packageName;
    // app名称
    String appName;
    // app版本
    String appVersion;
    // 开始时间
    long startTime;

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
