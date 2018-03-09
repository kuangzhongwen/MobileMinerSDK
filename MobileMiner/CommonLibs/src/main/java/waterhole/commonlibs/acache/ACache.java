package waterhole.commonlibs.acache;

import static waterhole.commonlibs.asyn.AsyncTaskAssistant.executeOnThreadPool;
import static waterhole.commonlibs.utils.LogUtils.printStackTrace;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import waterhole.commonlibs.utils.IOUtils;

/**
 * Google ACache, ACache是一个为android制定的轻量级的开源缓存框架，轻量到只有一个java文件（由十几个类精简而来）
 * <br>
 * 1、它可以缓存什么东西？
 * 普通的字符串、json、序列化的java对象，和字节数字。
 * <p>
 * 2. 它有什么特色？
 * 轻，轻到只有一个JAVA文件；
 * 可配置，可以配置缓存路径，缓存大小，缓存数量等；
 * 可以设置缓存超时时间，缓存超时自动失效，并被删除；
 * 多进程的支持；
 * <p>
 * 3. 使用：
 * 初始化ACache组件
 * <code>ACache acache = ACache.get(context)</code> 或
 * <code>ACache acache = ACache.get(context, max_size, max_count)</code>
 *
 * @author kzw on 2015/12/24
 * @see #get(Context)
 * @see #get(File, long, int)
 * <p>
 * 参数说明
 * max_size: 设置限制缓存大小，默认为50M
 * 　　max_count: 设置缓存数据的数量，默认不限制
 * <p>
 * 设置缓存数据
 * 　　 <code>acache.put(key,data,time)</code>或<code>acache.put(key,data)</code>
 * 　　 将数据同时上存入一级缓存（内存Map）和二级缓存（文件）中
 * @see #put(String, byte[])
 * @see #put(String, JSONObject)
 * @see #put(String, Serializable) ...
 * <p>
 * 参数说明
 * 　　 Key: 为存入缓存的数据设置唯一标识，取数据时就根据key来获得的
 * 　　 Data: 要存入的数据，有String、可序列化的对象、字节数组、Drawable等
 * Time: 设置缓存数据的有效时间，单位秒
 * </br>
 */
public final class ACache {

    // 最大缓存大小 50MB
    private static final int MAX_SIZE = 1024 * 1024 * 50;
    // 无最大缓存个数
    private static final int MAX_COUNT = Integer.MAX_VALUE;

    private static final String CACHE_NAME = "ACache";

    private static Map<String, ACache> mInstanceMap = new HashMap<>();

    private ACacheManager mCache;

    public static ACache get(Context ctx) {
        return get(ctx, CACHE_NAME);
    }

    public static ACache get(Context ctx, String cacheName) {
        return get(new File(ctx.getFilesDir(), cacheName), MAX_SIZE, MAX_COUNT);
    }

    public static ACache get(File cacheDir) {
        return get(cacheDir, MAX_SIZE, MAX_COUNT);
    }

    public static ACache get(File cacheDir, long max_zise, int max_count) {
        ACache manager = mInstanceMap.get(cacheDir.getAbsoluteFile() + myPid());
        if (manager == null) {
            manager = new ACache(cacheDir, max_zise, max_count);
            mInstanceMap.put(cacheDir.getAbsolutePath() + myPid(), manager);
        }
        return manager;
    }

    private static String myPid() {
        return "_" + android.os.Process.myPid();
    }

    private ACache(File cacheDir, long max_size, int max_count) {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new RuntimeException("can't make dirs in " + cacheDir.getAbsolutePath());
        }
        mCache = new ACacheManager(cacheDir, max_size, max_count);
    }

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的String数据
     */
    public void put(String key, String value) {
        File file = mCache.newFile(key);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file), 1024);
            out.write(value);
            out.flush();
        } catch (IOException e) {
            printStackTrace(e);
        } finally {
            IOUtils.closeSafely(out);
            mCache.put(file);
        }
    }

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的String数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, String value, int saveTime) {
        put(key, ACacheUtils.newStringWithDateInfo(saveTime, value));
    }

    /**
     * 读取 String数据
     *
     * @return String 数据
     */
    public String getAsString(String key) {
        File file = mCache.get(key);
        if (!file.exists()) {
            return null;
        }
        boolean removeFile = false;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String readString = "";
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                readString += currentLine;
            }
            if (!ACacheUtils.isDue(readString)) {
                return ACacheUtils.clearDateInfo(readString);
            } else {
                removeFile = true;
                return null;
            }
        } catch (IOException e) {
            return null;
        } finally {
            IOUtils.closeSafely(in);
            if (removeFile) {
                remove(key);
            }
        }
    }

    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的JSON数据
     */
    public void put(String key, JSONObject value) {
        put(key, value.toString());
    }

    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的JSONObject数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, JSONObject value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    /**
     * 保存 JSONArray数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的JSONArray数据
     */
    public void put(String key, JSONArray value) {
        put(key, value.toString());
    }

    /**
     * 保存 JSONArray数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的JSONArray数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, JSONArray value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的数据
     */
    public void put(String key, byte[] value) {
        File file = mCache.newFile(key);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(value);
            out.flush();
        } catch (Exception e) {
            printStackTrace(e);
        } finally {
            IOUtils.closeSafely(out);
            mCache.put(file);
        }
    }

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, byte[] value, int saveTime) {
        put(key, ACacheUtils.newByteArrayWithDateInfo(saveTime, value));
    }

    /**
     * 获取 byte 数据
     *
     * @return byte 数据
     */
    private byte[] getAsBinary(String key) {
        RandomAccessFile RAFile = null;
        boolean removeFile = false;
        try {
            File file = mCache.get(key);
            if (!file.exists()) {
                return null;
            }
            RAFile = new RandomAccessFile(file, "r");
            byte[] byteArray = new byte[(int) RAFile.length()];
            RAFile.read(byteArray);
            if (!ACacheUtils.isDue(byteArray)) {
                return ACacheUtils.clearDateInfo(byteArray);
            } else {
                removeFile = true;
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeSafely(RAFile);
            if (removeFile) {
                remove(key);
            }
        }
    }

    /**
     * 保存 Serializable数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的value
     */
    public void put(String key, Serializable value) {
        put(key, value, -1);
    }

    /**
     * 保存 Serializable数据到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的value
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, Serializable value, int saveTime) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            byte[] data = baos.toByteArray();
            if (saveTime != -1) {
                put(key, data, saveTime);
            } else {
                put(key, data);
            }
        } catch (Exception e) {
            printStackTrace(e);
        } finally {
            IOUtils.closeSafely(baos);
            IOUtils.closeSafely(oos);
        }
    }

    /**
     * 读取 Serializable数据
     *
     * @return Serializable 数据
     */
    public Object getAsObject(String key) {
        byte[] data = getAsBinary(key);
        if (data != null) {
            ByteArrayInputStream bais = null;
            ObjectInputStream ois = null;
            try {
                bais = new ByteArrayInputStream(data);
                ois = new ObjectInputStream(bais);
                return ois.readObject();
            } catch (Exception e) {
                return null;
            } finally {
                IOUtils.closeSafely(bais);
                IOUtils.closeSafely(ois);
            }
        }
        return null;

    }

    /**
     * 获取缓存文件
     *
     * @return value 缓存的文件
     */
    public File file(String key) {
        File f = mCache.newFile(key);
        if (f.exists()) {
            return f;
        }
        return null;
    }

    /**
     * 移除某个key
     *
     * @return 是否移除成功
     */
    public boolean remove(String key) {
        return mCache.remove(key);
    }

    /**
     * 清除所有数据
     */
    public synchronized void clear() {
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                mCache.clear();
            }
        });
    }
}
