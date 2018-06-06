package waterhole.miner.core.analytics;

import android.content.Context;

/**
 * 统计包装类.
 *
 * @author kzw on 2018/06/05.
 */
public final class AnalyticsWrapper {

    public AnalyticsWrapper() {
        throw new RuntimeException("AnalyticsWrapper stub!");
    }

    public static void onEvent(Context context, AnalyticsObject object) {
        if (context != null && object != null) {
            // todo kzw 优先考虑国外统计平台，再考虑找阿东写接口
        }
    }

    public static void onError(Context context, AnalyticsErrorObject object) {
        if (context != null && object != null) {
            // todo kzw 优先考虑国外统计平台，再考虑找阿东写接口
        }
    }
}
