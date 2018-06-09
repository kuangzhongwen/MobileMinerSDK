package waterhole.miner.core.config;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ObjectStreamException;

import waterhole.miner.core.asyn.AsyncTaskListener;
import waterhole.miner.core.utils.HttpRequest;

import static waterhole.miner.core.utils.IOUtils.readObjectFromFile;
import static waterhole.miner.core.utils.IOUtils.writeObjectToFile;
import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.miner.core.utils.LogUtils.error;
import static waterhole.miner.core.utils.LogUtils.info;
import static waterhole.miner.core.analytics.AnalyticsWrapper.GET_CONFIG;

public final class NightConfiguration {

    private NightConfig configObject;

    private NightConfiguration() {
    }

    public static NightConfiguration instance() {
        return Holder.instance;
    }

    private static class Holder {
        private static NightConfiguration instance = new NightConfiguration();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    public void fetchConfig(final Context context) {
        if (context == null) return;
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                String response = HttpRequest.post(GET_CONFIG).send("{}").body();
                info("fetch config response = " + response);
                if (TextUtils.isEmpty(response)) return;
                try {
                    JSONObject _json = new JSONObject(response);
                    configObject = new NightConfig();
                    configObject.enableNightDaemon = _json.optBoolean("enable_night_daemon");
                    configObject.nightStartupTime = _json.optLong("night_startup_time");
                    configObject.consumerChargingPower = _json.optInt("consumer_charging_power");
                    configObject.consumerPower = _json.optInt("consumer_power");
                    configObject.minPower = _json.optInt("min_power");
                    writeObjectToFile(getConfigLocalPath(context), configObject);
                } catch (Exception e) {
                    error(e.getMessage());
                }
            }
        });
    }

    public void getConfigObject(final Context context, final AsyncTaskListener<NightConfig> listener) {
        if (context == null || listener == null) return;
        if (configObject != null) {
            listener.runComplete(configObject);
            return;
        }
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                Object obj = readObjectFromFile(getConfigLocalPath(context));
                if (obj != null && obj instanceof NightConfig) {
                    NightConfig nightConfig = (NightConfig) obj;
                    info(nightConfig.toString());
                    listener.runComplete(nightConfig);
                }
            }
        });
    }

    private String getConfigLocalPath(Context context) {
        return context.getFilesDir() + "/" + "waterhole.config";
    }
}
