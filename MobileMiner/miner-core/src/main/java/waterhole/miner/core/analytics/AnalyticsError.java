package waterhole.miner.core.analytics;

/**
 * 统计错误信息实体类.
 *
 * @author kzw on 2018/06/05.
 */
public final class AnalyticsError {

    // 设备id
    public String deviceId;
    // 错误信息
    public String error;

    @Override
    public String toString() {
        return "AnalyticsErrorObject{" +
                "deviceId='" + deviceId + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
