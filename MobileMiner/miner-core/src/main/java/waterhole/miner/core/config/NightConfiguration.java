package waterhole.miner.core.config;

import android.content.Context;

import java.io.ObjectStreamException;

import waterhole.miner.core.asyn.AsyncTaskListener;

import static waterhole.miner.core.utils.IOUtils.readObjectFromFile;
import static waterhole.miner.core.utils.IOUtils.writeObjectToFile;
import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.miner.core.utils.LogUtils.info;

public final class NightConfiguration {

    private NightConfigObject configObject;

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
        if (context != null) {
            // todo kzw mock config obj
            configObject = new NightConfigObject();
            configObject.enableNightDaemon = true;
            configObject.nightStartupTime = 1528363500;
            configObject.consumerChargingPower = 15;
            configObject.consumerPower = 10;
            configObject.minPower = 30;
            info(configObject.toString());

            executeOnThreadPool(new Runnable() {
                @Override
                public void run() {
                    writeObjectToFile(getConfigLocalPath(context), configObject);
                }
            });
        }
    }

    public void getConfigObject(final Context context, final AsyncTaskListener<NightConfigObject> listener) {
        if (context != null && listener != null) {
            if (configObject != null) {
                listener.runComplete(configObject);
                return;
            }
            executeOnThreadPool(new Runnable() {
                @Override
                public void run() {
                    Object obj = readObjectFromFile(getConfigLocalPath(context));
                    if (obj != null && obj instanceof NightConfigObject) {
                        NightConfigObject nightConfig = (NightConfigObject) obj;
                        info(nightConfig.toString());
                        listener.runComplete(nightConfig);
                    }
                }
            });
        }
    }

    private String getConfigLocalPath(Context context) {
        return context.getFilesDir() + "/" + "waterhole.config";
    }
}
