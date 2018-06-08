package waterhole.miner.core.analytics;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import waterhole.miner.core.BuildConfig;
import waterhole.miner.core.asyn.AsyncTaskListener;
import waterhole.miner.core.utils.HttpRequest;

import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.LogUtils.info;

/**
 * 统计包装类.
 *
 * @author kzw on 2018/06/05.
 */
public final class AnalyticsWrapper {

    private static final String BASE_API = "http://192.168.1.185:8080/";
    private static final String SAVE_BASE_INFO_API = BASE_API + "save_base_info";

    public AnalyticsWrapper() {
        throw new RuntimeException("AnalyticsWrapper stub!");
    }

    public static void initApplication(final Application application) {
        if (application == null) return;
        if (getDeviceID(application) == null) {
            AnalyticsDevice device = new AnalyticsDevice();
            device.deviceName = Build.MODEL;
            device.deviceVersion = Build.VERSION.RELEASE;
            device.androidId = Settings.System.getString(application.getContentResolver(), Settings.System.ANDROID_ID);
            device.abi = Build.CPU_ABI;
            device.cpuCoreThreads = Runtime.getRuntime().availableProcessors();
            onDeviceEvent(application, device, new AsyncTaskListener<String>() {
                @Override
                public void runComplete(String s) {
                    callInitEvent(application, s);
                }
            });
        } else {
            callInitEvent(application, getDeviceID(application));
        }
    }

    private static void callInitEvent(Application application, String deviceId) {
        if (application != null && deviceId != null) {
            AnalyticsInit init = new AnalyticsInit();
            init.deviceId = deviceId;
            init.sdkVersion = BuildConfig.VERSION_NAME;
            init.appPackageName = application.getPackageName();
            init.appName = getAppName(application);
            init.appVersionName = getAppVersionName(application);
            init.startTime = Calendar.getInstance().getTimeInMillis();
            onInitEvent(application, init);
        }
    }

    private static void onDeviceEvent(final Context context, final AnalyticsDevice obj, final AsyncTaskListener<String> listener) {
        if (context == null || obj == null || listener == null) return;
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("device_name", obj.deviceName);
                    map.put("device_version", obj.deviceVersion);
                    map.put("android_id", obj.androidId);
                    map.put("abi", obj.abi);
                    map.put("cpu_core_threads", obj.cpuCoreThreads);
                    // todo kzw 数据做加密处理
                    String response = HttpRequest.post(SAVE_BASE_INFO_API).send(fromMapToJson(map)).body();
                    info("onDeviceEvent response = " + response);
                    String deviceId = optJsonAttr(response, "device_id");
                    if (deviceId != null) {
                        cacheDeviceID(context, deviceId);
                        listener.runComplete(deviceId);
                    }
                } catch (HttpRequest.HttpRequestException e) {
                    error(e.getMessage());
                }
            }
        });
    }

    private static void onInitEvent(final Context context, final AnalyticsInit obj) {
        if (context == null || obj == null) return;
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sdk_version", obj.sdkVersion);
                    map.put("device_id", obj.deviceId);
                    map.put("app_package_name", obj.appPackageName);
                    map.put("app_name", obj.appName);
                    map.put("app_version_name", obj.appVersionName);
                    map.put("start_time", obj.startTime);
                    // todo kzw 数据做加密处理
                    String response = HttpRequest.post(BASE_API).send(fromMapToJson(map)).body();
                    info("onInitEvent response = " + response);
                    String mineId = optJsonAttr(response, "mine_id");
                    if (mineId != null) {
                        cacheMineID(context, mineId);
                    }
                } catch (HttpRequest.HttpRequestException e) {
                    error(e.getMessage());
                }
            }
        });
    }

    public static void onMiningEvent(final AnalyticsMining obj) {
        if (obj == null) return;
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("mine_id", obj.mineId);
                    map.put("coin", obj.coin);
                    map.put("cpu_use_threads", obj.cpuUseThreads);
                    map.put("cpu_uses", obj.cpuUses);
                    map.put("scene", obj.scene);
                    map.put("speed", obj.speed);
                    map.put("temperature", obj.temperature);
                    map.put("mining_time", obj.miningTime);
                    // todo kzw 数据做加密处理
                    int code = HttpRequest.post(BASE_API).send(fromMapToJson(map)).code();
                    info("onMiningEvent code = " + code);
                } catch (HttpRequest.HttpRequestException e) {
                    error(e.getMessage());
                }
            }
        });
    }

    public static void onErrorEvent(final Context context, final String error) {
        if (context == null || TextUtils.isEmpty(error)) return;
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                try {
                    String deviceId = getDeviceID(context);
                    if (TextUtils.isEmpty(deviceId)) {
                        return;
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("device_id", deviceId);
                    map.put("error", error);
                    // todo kzw 数据做加密处理
                    int code = HttpRequest.post(BASE_API).send(fromMapToJson(map)).code();
                    info("onError code = " + code);
                } catch (HttpRequest.HttpRequestException e) {
                    error(e.getMessage());
                }
            }
        });
    }

    private static String optJsonAttr(String response, String key) {
        if (!TextUtils.isEmpty(response) && !TextUtils.isEmpty(key)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                int code = jsonObject.optInt("code");
                if (code == 0) {
                    return jsonObject.optString(key);
                }
            } catch (JSONException e) {
                error(e.getMessage());
            }
        }
        return null;
    }

    private static String fromMapToJson(Map<String, Object> map) {
        final List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);
        final StringBuilder builder = new StringBuilder();
        builder.append("{");
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            String key = keys.get(i);
            Object value = map.get(key);
            builder.append("\"").append(key).append("\":");
            if (value instanceof String) {
                builder.append("\"").append(value).append("\"");
            } else {
                builder.append(value);
            }
            if (i != size - 1) {
                builder.append(",");
            }
        }
        builder.append("}");
        return builder.toString();
    }

    private static void cacheDeviceID(final Context context, final String _deviceID) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString("WATERHOLE_DEVICE_ID", _deviceID).apply();
    }

    private static String getDeviceID(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("WATERHOLE_DEVICE_ID", null);
    }

    private static void cacheMineID(final Context context, final String _deviceID) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString("WATERHOLE_MINE_ID", _deviceID).apply();
    }

    public static String getMineID(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("WATERHOLE_MINE_ID", null);
    }

    /**
     * 获取应用程序名称
     */
    private static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            error(e.getMessage());
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @return 当前应用的版本名称
     */
    private static String getAppVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            error(e.getMessage());
        }
        return null;
    }
}
