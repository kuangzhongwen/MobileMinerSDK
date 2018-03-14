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

    /**
     * 从输入流中获得字符串.
     *
     * @param inputStream {@link InputStream}
     * @return 字符串
     */
    public static String getStringFromInputStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LogUtils.printStackTrace(e);
        } finally {
            closeSafely(reader);
        }
        return sb.toString();
    }

    /**
     * 将对象写入到本地文件中，对象必须是序列化的
     *
     * @param path   保存路径
     * @param object 保存对象
     */
    public static void writeObjectToFile(String path, Object object) {
        if (!TextUtils.isEmpty(path) && object != null) {
            File file = new File(path);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                ObjectOutputStream objOut = new ObjectOutputStream(out);
                objOut.writeObject(object);
                objOut.flush();
            } catch (IOException e) {
                LogUtils.printStackTrace(e);
            } finally {
                closeSafely(out);
            }
        }
    }

    /**
     * 从本地读取序列化对象
     *
     * @param path 存储路径
     */
    public static Object readObjectFromFile(String path) {
        Object object;
        File file = new File(path);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            object = objIn.readObject();
            objIn.close();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        } finally {
            closeSafely(in);
        }
        return object;
    }
}
