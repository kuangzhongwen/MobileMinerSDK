package waterhole.miner.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 在Application中一定要先配置
 * SharedPreferences 存储
 */
public class SpUtil {
    public static final String PREFERENCE_FILE_NAME = "wt_share";

    private static Context mContext;

    private SpUtil() {
    }

    public static void config(Context context) {
        if (mContext == null && context != null)
            mContext = context;
    }

    /**
     * 获取string，默认值为""
     *
     * @param key
     * @return
     */
    public static String getShareData(String key) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }


    /**
     * 获取string
     *
     * @param key
     * @param defValue
     * @return
     */
    public static String getShareData(String key, String defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    /**
     * 获取int
     *
     * @param key
     * @param defValue
     * @return
     */
    public static int getIntShareData(String key, int defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static int getIntShareData(String key) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public static boolean getBooleanShareData(String key) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    public static boolean getBooleanShareData(String key, boolean defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 存储string
     *
     * @param key
     * @param value
     */
    public static void putShareData(String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        Editor et = sp.edit();
        et.putString(key, value);
        et.commit();
    }

    /**
     * 存储int
     *
     * @param key
     * @param value
     */
    public static void putIntShareData(String key, int value) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        Editor et = sp.edit();
        et.putInt(key, value);
        et.commit();
    }

    /**
     * 存储boolean
     *
     * @param key
     * @param value
     */
    public static void putBooleanShareData(String key, boolean value) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        Editor et = sp.edit();
        et.putBoolean(key, value);
        et.commit();
    }

    public static void remove(String key) {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        Editor et = sp.edit();
        et.remove(key);
        et.commit();
    }
}
