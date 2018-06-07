package waterhole.miner.core.utils;


import android.database.Cursor;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * IO工具类
 *
 * @author kzw on 2015/11/21.
 */
public final class IOUtils {

    public IOUtils() {
        throw new AssertionError();
    }

    /**
     * 安全关闭.
     *
     * @param closeable Closeable.
     */
    public static void closeSafely(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Throwable t) {
            LogUtils.printStackTrace(t);
        }
    }

    /**
     * 安全关闭.
     *
     * @param cursor Cursor.
     */
    public static void closeSafely(Cursor cursor) {
        try {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Throwable t) {
            LogUtils.printStackTrace(t);
        }
    }
}
