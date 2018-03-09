package waterhole.commonlibs.acache;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ACache缓存管理器
 *
 * @author kzw on 2015/12/24
 */
final class ACacheManager {

    private final AtomicLong mCacheSize;
    private final AtomicInteger mCacheCount;

    private final long mSizeLimit;
    private final int mCountLimit;

    private final Map<File, Long> mLastUsageDates = Collections.synchronizedMap(
            new HashMap<File, Long>());

    private File mCacheDir;

    ACacheManager(File cacheDir, long sizeLimit, int countLimit) {
        mCacheDir = cacheDir;
        mSizeLimit = sizeLimit;
        mCountLimit = countLimit;
        mCacheSize = new AtomicLong();
        mCacheCount = new AtomicInteger();

        calculateCacheSizeAndCacheCount();
    }

    /**
     * 计算 cacheSize和cacheCount
     */
    private void calculateCacheSizeAndCacheCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = 0;
                int count = 0;
                File[] cachedFiles = mCacheDir.listFiles();
                if (cachedFiles != null) {
                    for (File cachedFile : cachedFiles) {
                        size += calculateSize(cachedFile);
                        count += 1;
                        mLastUsageDates.put(cachedFile, cachedFile.lastModified());
                    }
                    mCacheSize.set(size);
                    mCacheCount.set(count);
                }
            }
        }).start();
    }

    public void put(File file) {
        int curCacheCount = mCacheCount.get();

        while (curCacheCount + 1 > mCountLimit) {
            long freedSize = removeNext();
            mCacheSize.addAndGet(-freedSize);
            curCacheCount = mCacheCount.addAndGet(-1);
        }

        mCacheCount.addAndGet(1);

        long valueSize = calculateSize(file);
        long curCacheSize = mCacheSize.get();
        while (curCacheSize + valueSize > mSizeLimit) {
            long freedSize = removeNext();
            curCacheSize = mCacheSize.addAndGet(-freedSize);
        }

        mCacheSize.addAndGet(valueSize);

        Long currentTime = System.currentTimeMillis();
        file.setLastModified(currentTime);
        mLastUsageDates.put(file, currentTime);
    }

    public File get(String key) {
        File file = newFile(key);
        Long currentTime = System.currentTimeMillis();
        file.setLastModified(currentTime);
        mLastUsageDates.put(file, currentTime);
        return file;
    }

    File newFile(String key) {
        return new File(mCacheDir, key.hashCode() + "");
    }

    public boolean remove(String key) {
        File image = get(key);
        return image.delete();
    }

    public void clear() {
        mLastUsageDates.clear();
        mCacheSize.set(0);
        File[] files = mCacheDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    /**
     * 移除旧的文件
     */
    private long removeNext() {
        if (mLastUsageDates.isEmpty()) {
            return 0;
        }
        Long oldestUsage = null;
        File mostLongUsedFile = null;
        Set<Map.Entry<File, Long>> entries = mLastUsageDates.entrySet();

        synchronized (mLastUsageDates) {
            for (Map.Entry<File, Long> entry : entries) {
                if (mostLongUsedFile == null) {
                    mostLongUsedFile = entry.getKey();
                    oldestUsage = entry.getValue();
                } else {
                    Long lastValueUsage = entry.getValue();
                    if (lastValueUsage < oldestUsage) {
                        oldestUsage = lastValueUsage;
                        mostLongUsedFile = entry.getKey();
                    }
                }
            }
        }

        long fileSize = calculateSize(mostLongUsedFile);
        if (mostLongUsedFile != null && mostLongUsedFile.delete()) {
            mLastUsageDates.remove(mostLongUsedFile);
        }
        return fileSize;
    }

    private static long calculateSize(File file) {
        return file.length();
    }
}
