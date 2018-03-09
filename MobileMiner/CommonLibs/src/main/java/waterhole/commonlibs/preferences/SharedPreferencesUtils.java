/*
 * Copyright (C) 2014 Zlianjie Inc. All rights reserved.
 */
package waterhole.commonlibs.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Utility for {@link SharedPreferences} access. Currently just support the default one.
 *
 * @author kzw on 2014/10/10.
 */
public final class SharedPreferencesUtils {

    private SharedPreferencesUtils() {
    }

    private static SharedPreferences getDefaultSharedPreferences(Context context) {
        if (context != null) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        }
        return null;
    }

    public static SharedPreferences getSharedPreferences(Context context, String prefName) {
        if (context != null) {
            return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        }
        return null;
    }

    /**
     * Get a boolean value
     */
    public static boolean getBoolean(Context context, String key, boolean defVal) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        return pref != null && pref.getBoolean(key, defVal);
    }

    /**
     * Set a boolean value
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        if (pref != null) {
            pref.edit().putBoolean(key, value).apply();
        }
    }

    /**
     * Get a int value
     */
    public static int getInt(Context context, String key, int defVal) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        if (pref != null) {
            return pref.getInt(key, defVal);
        }
        return 0;
    }

    /**
     * Set a int value
     */
    public static void setInt(Context context, String key, int value) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        if (pref != null) {
            pref.edit().putInt(key, value).apply();
        }
    }

    /**
     * Get a long value
     */
    public static long getLong(Context context, String key, long defVal) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        if (pref != null) {
            return pref.getLong(key, defVal);
        }
        return 0;
    }

    /**
     * Set a long value
     */
    public static void setLong(Context context, String key, long value) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        if (pref != null) {
            pref.edit().putLong(key, value).apply();
        }
    }

    /**
     * Get a string value
     */
    public static String getString(Context context, String key, String defVal) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        if (pref != null) {
            return pref.getString(key, defVal);
        }
        return "";
    }

    /**
     * Set a string value
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        if (pref != null) {
            pref.edit().putString(key, value).apply();
        }
    }

    /**
     * Remove the value (of any type) for given key in prefName pref
     */
    public static void removeValue(Context context, String prefName, String key) {
        SharedPreferences pref = getSharedPreferences(context, prefName);
        if (pref != null) {
            pref.edit().remove(key).apply();
        }
    }

    /**
     * Remove the value (of any type) for given key
     */
    public static void removeValue(Context context, String key) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        if (pref != null) {
            pref.edit().remove(key).apply();
        }
    }

    /**
     * Checks whether default preferences contains the preference.
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        return pref != null && pref.contains(key);
    }

    /**
     * Get a boolean value
     */
    public static boolean getBoolean(Context context, String prefName, String key, boolean defVal) {
        SharedPreferences pref = getSharedPreferences(context, prefName);
        return pref != null && pref.getBoolean(key, defVal);
    }

    /**
     * Set a boolean value
     */
    public static void setBoolean(Context context, String prefName, String key, boolean value) {
        SharedPreferences pref = getSharedPreferences(context, prefName);
        if (pref != null) {
            pref.edit().putBoolean(key, value).apply();
        }
    }

    /**
     * Get a int value
     */
    public static int getInt(Context context, String prefName, String key, int defVal) {
        SharedPreferences pref = getSharedPreferences(context, prefName);
        if (pref != null) {
            return pref.getInt(key, defVal);
        }
        return 0;
    }

    /**
     * Set a int value
     */
    public static void setInt(Context context, String prefName, String key, int value) {
        SharedPreferences pref = getSharedPreferences(context, prefName);
        if (pref != null) {
            pref.edit().putInt(key, value).apply();
        }
    }

    /**
     * Get a long value
     */
    public static long getLong(Context context, String prefName, String key, long defVal) {
        SharedPreferences pref = getSharedPreferences(context, prefName);
        if (pref != null) {
            return pref.getLong(key, defVal);
        }
        return 0L;
    }

    /**
     * Set a long value
     */
    public static void setLong(Context context, String prefName, String key, long value) {
        SharedPreferences pref = getSharedPreferences(context, prefName);
        if (pref != null) {
            pref.edit().putLong(key, value).apply();
        }
    }

    /**
     * Get a string value
     */
    public static String getString(Context context, String prefName, String key, String defVal) {
        SharedPreferences pref = getSharedPreferences(context, prefName);
        return pref.getString(key, defVal);
    }

    /**
     * Set a string value
     */
    public static void setString(Context context, String prefName, String key, String value) {
        SharedPreferences pref = getSharedPreferences(context, prefName);
        if (pref != null) {
            pref.edit().putString(key, value).apply();
        }
    }

    /**
     * Clear all the value in given preference.
     */
    public static void clear(Context context, String prefName) {
        SharedPreferences pref = getSharedPreferences(context, prefName);
        if (pref != null) {
            pref.edit().clear().apply();
        }
    }

    public static void clearDefaultPrefer(Context context) {
        SharedPreferences pref = getDefaultSharedPreferences(context);
        if (pref != null) {
            pref.edit().clear().apply();
        }
    }

    /**
     * Checks whether the preferences contains the preference.
     */
    public static boolean contains(Context context, String prefName, String key) {
        SharedPreferences pref = getSharedPreferences(context, prefName);
        return pref != null && pref.contains(key);
    }
}
