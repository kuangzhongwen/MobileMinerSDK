package waterhole.miner.core.analytics;

import android.content.Context;
import android.content.SharedPreferences;

public final class AnalyticsSP {

    static final int DEF_VALUE = -1;

    private static final String CACHE_FILENAME = "WATERHOLE_CORE_MINE";
    private static final String KEY_DEVICE_ID = "WATERHOLE_CORE_DEVICE_ID";
    private static final String KEY_MINE_ID = "WATERHOLE_CORE_MINE_ID";
    private static final String KEY_MINE_COIN = "WATERHOLE_CORE_MINE_COIN";
    private static final String KEY_MINE_CPU_USE_THREADS = "WATERHOLE_CORE_MINE_CPU_USE_THREADS";
    private static final String KEY_MINE_CPU_USE = "WATERHOLE_CORE_MINE_CPU_USE";
    private static final String KEY_CORE_MINING_SCENE = "WATERHOLE_CORE_MINING_SCENE";
    private static final String KEY_CPU_TEMPERATURE = "WATERHOLE_CORE_CPU_TEMPERATURE";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(CACHE_FILENAME, Context.MODE_MULTI_PROCESS);
    }

    static boolean cacheDeviceID(final Context context, final int _deviceID) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.edit().putInt(KEY_DEVICE_ID, _deviceID).commit();
    }

    static int getDeviceID(final Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(KEY_DEVICE_ID, DEF_VALUE);
    }

    static boolean cacheMineID(final Context context, final int _deviceID) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.edit().putInt(KEY_MINE_ID, _deviceID).commit();
    }

    static int getMineID(final Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(KEY_MINE_ID, DEF_VALUE);
    }

    public static boolean cacheMineCoin(final Context context, final String coin) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.edit().putString(KEY_MINE_COIN, coin).commit();
    }

    static String getMineCoin(final Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(KEY_MINE_COIN, "");
    }

    public static boolean cacheCpuUseThreads(final Context context, final int cpuUseThreads) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.edit().putInt(KEY_MINE_CPU_USE_THREADS, cpuUseThreads).commit();
    }

    static int getCacheCpuUseThreads(final Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(KEY_MINE_CPU_USE_THREADS, DEF_VALUE);
    }

    public static boolean cacheCpuUse(final Context context, final int cpuUse) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.edit().putInt(KEY_MINE_CPU_USE, cpuUse).commit();
    }

    static int getCacheCpuUse(final Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(KEY_MINE_CPU_USE, DEF_VALUE);
    }

    public static boolean cacheMineScene(final Context context, final String scene) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.edit().putString(KEY_CORE_MINING_SCENE, scene).commit();
    }

    static String getMineScene(final Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(KEY_CORE_MINING_SCENE, "");
    }

    public static boolean cacheCpuTemperature(final Context context, final double temperature) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.edit().putFloat(KEY_CPU_TEMPERATURE, (float) temperature).commit();
    }

    static float getCpuTemperature(final Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getFloat(KEY_CPU_TEMPERATURE, DEF_VALUE);
    }
}
