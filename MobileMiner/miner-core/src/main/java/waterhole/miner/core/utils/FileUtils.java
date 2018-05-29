
package waterhole.miner.core.utils;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import waterhole.miner.core.crypto.MD5;

import static waterhole.miner.core.utils.IOUtils.closeSafely;
import static waterhole.miner.core.utils.LogUtils.printStackTrace;

/**
 * 文件util
 *
 * @author kzw on 2015/11/18.
 */
public final class FileUtils {

    private static String SDCardRoot;

    static {
        // 获取SD卡路径
        SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    private FileUtils() {
    }

    @SuppressWarnings("deprecation")
    public static float getSDAvailSize() {
        File sdcardDir = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(sdcardDir.getPath());
        return sf.getAvailableBlocks() * sf.getBlockSize();
    }

    /**
     * 检测文件是否存在
     */
    public static boolean isFileExist(String fileName, String path) {
        File file = new File(SDCardRoot + path + File.separator + fileName);
        return file.exists();
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 删除文件或文件夹
     *
     * @return 删除是否成功
     */
    public static boolean delete(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                return file.delete();
            }
            for (File childFile : childFiles) {
                delete(childFile);
            }
            return file.delete();
        }

        return false;
    }

    public static long getFileSize(File file) {
        long size = 0;
        FileInputStream fis = null;
        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                size = fis.available();
            }
        } catch (IOException e) {
            // do nothing
        } finally {
            IOUtils.closeSafely(fis);
        }
        return size;
    }

    public static boolean isSdCardAvailuable() {
        boolean bRet = false;
        do {
            if (!isSDCardExist()) {
                break;
            }
            if (getSDFreeSize() < 5) {
                break;
            }

            bRet = true;
        } while (false);

        return bRet;
    }

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1).toLowerCase();
            }
        }
        return filename;
    }

    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取sdcard可用空间的大小
     */
    @SuppressWarnings("deprecation")
    private static long getSDFreeSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 单位MB
        return (sf.getAvailableBlocks() * sf.getBlockSize()) / 1024 / 1024;
    }

    public static File copyFile(String oldPath, String newPath) {
        try {
            int byteread;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (newFile.exists()) {
                delete(newFile);
            }
            newFile.createNewFile();

            InputStream inStream = null;
            FileOutputStream fs = null;
            if (oldfile.exists()) {
                try {
                    // 读入原文件
                    inStream = new FileInputStream(oldPath);
                    fs = new FileOutputStream(newPath);
                    byte[] buffer = new byte[1024];
                    while ((byteread = inStream.read(buffer)) != -1) {
                        fs.write(buffer, 0, byteread);
                    }
                } catch (IOException e) {
                    // do nothing
                } finally {
                    IOUtils.closeSafely(inStream);
                    IOUtils.closeSafely(fs);
                }
            }
            return newFile;
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }

    public static File bitmapToFile(Bitmap bmp, String desFilePath, String mimeType) {
        try {
            final File imageFile = new File(desFilePath);
            if (imageFile.exists()) {
                return imageFile;
            }
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            if ("image/jpeg".equals(mimeType)) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
            if ("image/png".equals(mimeType)) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            fos.flush();
            fos.close();
            return imageFile;
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }

    /**
     * 获取amr文件的时长
     */
    public static long getAmrDuration(File file) throws IOException {
        if (file == null || !file.exists()) {
            return 0;
        }
        long duration = -1;
        int[] packedSize = {12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0};
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            // 文件的长度
            long length = file.length();
            // 设置初始位置
            int pos = 6;
            // 初始帧数
            int frameCount = 0;
            int packedPos;
            // 初始数据值
            byte[] datas = new byte[1];
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }
            // 帧数*20
            duration += frameCount * 20;
        } finally {
            IOUtils.closeSafely(randomAccessFile);
        }
        return duration;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @param kind      参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *                  其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        try {
            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } catch (OutOfMemoryError e) {
            System.gc();
        }
        return bitmap;
    }

    public static String getFileMd5(File file) {
        if (file == null || !file.exists()) {
            return "";
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] imageBytes = new byte[(int) file.length()];
            fis.read(imageBytes);
            return MD5.md5Hex(imageBytes);
        } catch (IOException e) {
            return "";
        } finally {
            if (fis != null) {
                IOUtils.closeSafely(fis);
            }
        }
    }

    /**
     * 获取文件夹大小，单位M
     */
    public static double getDirSize(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children) {
                    size += getDirSize(f);
                }
                return size;
            } else {
                return (double) file.length() / 1024 / 1024;
            }
        } else {
            return 0.0;
        }
    }

    public static String getMIMEByFilePath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return "";
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtensionName(filePath));
    }


    /**
     * Uri to Url
     */
    public static String uri2Url(Uri uri, Activity ac, String[] proj) {
        if (uri == null || ac == null || proj == null) {
            return "";
        }
        String url = Uri.decode(uri.toString());
        if (url.startsWith("content://")) {
            Cursor actualimagecursor = ac.managedQuery(uri, proj, null, null, null);
            if (actualimagecursor == null) {
                return "";
            }
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(proj[0]);
            actualimagecursor.moveToFirst();
            return actualimagecursor.getString(actual_image_column_index);
        } else if (url.startsWith("file://")) {
            url = url.substring("file://".length());
        }

        return url;
    }

    /**
     * Uri to Url
     */
    public static String uri2Url(Uri uri) {
        if (uri == null) {
            return "";
        }
        String url = Uri.decode(uri.toString());
        if (url.startsWith("file://")) {
            url = url.substring("file://".length());
        }

        return url;
    }

    public static String getFileNameFromPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return "";
        }
        return file.getName();
    }

    public static boolean isPDFFile(String extensionName) {
        return !TextUtils.isEmpty(extensionName) && "pdf".equals(extensionName.toLowerCase());
    }

    public static String getFilename(String url) {
        return StringUtils.getFileNameFromLink(url).replaceAll("%", "").replaceAll("/", "");
    }

    /**
     * 获取视频文件缩略图存储路径
     */
    public static String getVideoThumbPath(String originalVideoPath) {
        if (TextUtils.isEmpty(originalVideoPath)) {
            return "";
        }

        return originalVideoPath.substring(originalVideoPath.lastIndexOf("/"), originalVideoPath
                .lastIndexOf(".")) + ".jpg";
    }

    public static void clearDirectionFile(File imageFile) {
        if (imageFile != null && imageFile.isDirectory()) {
            File[] files = imageFile.listFiles();
            for (File f : files) {
                if (f != null && f.exists()) {
                    delete(f);
                }
            }
        }
    }

    public static File byteToFile(byte[] bytes, File file) {
        BufferedOutputStream stream = null;
        try {
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(bytes);
        } catch (Exception e) {
            // do nothing
        } finally {
            IOUtils.closeSafely(stream);
        }
        return file;
    }

    public static String getFileFormat(File f) {
        if (f != null && f.exists()) {
            String filename = f.getName();
            if (!TextUtils.isEmpty(filename) && filename.contains(".")) {
                return filename.substring(filename.lastIndexOf(".") + 1);
            } else {
                return "";
            }
        }
        return "";
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     */
    public static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item != null && item.exists()) {
                    delete(item);
                }
            }
        }
    }

    public static void cleanExpireFile(File file, long expireTime) {
        long currentTimeMillis = System.currentTimeMillis();
        if (file != null && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File f : files) {
                if (currentTimeMillis - f.lastModified() < expireTime && FileUtils
                        .isSDCardExist()) {
                    continue;
                }
                if (f.exists()) {
                    delete(f);
                }
            }
        }
    }

    public static byte[] getFileWave(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            byte[] buf = new byte[100];
            int n;
            while ((n = is.read(buf, 0, 100)) > 0) {
                baos.write(buf, 0, n);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            // do nothing
        } finally {
            IOUtils.closeSafely(baos);
            IOUtils.closeSafely(is);
        }
        return null;
    }

    public static String getFileSizeDesc(long sizeInByte) {
        if (sizeInByte < 0) {
            return "UNKNOWN SIZE";
        }
        String[] unit = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB"};
        long mainPart = sizeInByte;
        long changePart = 0;
        int unitIndex = 0;

        while (sizeInByte >= 1024 && unitIndex < unit.length - 1) {
            mainPart = sizeInByte / 1024;
            changePart = sizeInByte % 1024;
            sizeInByte = mainPart;
            unitIndex++;
        }

        StringBuilder res = new StringBuilder();
        res.append(mainPart);
        if (changePart != 0) {
            res.append(new DecimalFormat(".000").format(changePart * 1.0 / 1024));
        }
        res.append(" ").append(unit[unitIndex]);
        return res.toString();
    }

    public static void downloadFile(String url, String savePath, DownloadCallback callback) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(savePath)) {
            throw new IllegalArgumentException("URL or savePath is Null");
        }
        OutputStream output = null;
        try {
            File file = new File(savePath);
            if (file.exists()) {
                if (callback != null) {
                    callback.onDownloadSuccess(savePath);
                }
                return;
            }
            URL URL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) URL.openConnection();
            InputStream input = conn.getInputStream();
            output = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            int len;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            output.flush();
            if (callback != null) {
                callback.onDownloadSuccess(savePath);
            }
        } catch (MalformedURLException e) {
            printStackTrace(e);
            if (callback != null) {
                callback.onDownloadFail(savePath, e.getMessage());
            }
        } catch (IOException e) {
            printStackTrace(e);
            if (callback != null) {
                callback.onDownloadFail(savePath, e.getMessage());
            }
        } finally {
            closeSafely(output);
        }
    }

    public interface DownloadCallback {

        void onDownloadSuccess(String path);

        void onDownloadFail(String path, String reason);
    }

    public static void unzip(String zipFile, String targetDir, UnzipCallback callback) {
        if (TextUtils.isEmpty(zipFile) || TextUtils.isEmpty(targetDir)) {
            throw new IllegalArgumentException("zipFilePath or targetFilePath is Null");
        }
        int BUFFER = 4096;
        String strEntry;
        try {
            BufferedOutputStream dest;
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File entryFile = null;
                try {
                    strEntry = entry.getName();
                    if (strEntry.contains("__MACOSX")) {
                        continue;
                    }
                    int count;
                    byte data[] = new byte[BUFFER];
                    entryFile = new File(targetDir + "/" + strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex) {
                    printStackTrace(ex);
                    if (callback != null) {
                        callback.onUnzipEntryFail(entryFile != null ? entryFile.getAbsolutePath() : "", ex.getMessage());
                    }
                }
            }
            zis.close();
            if (callback != null) {
                callback.onUnzipComplete(zipFile);
            }
        } catch (Exception e) {
            printStackTrace(e);
            if (callback != null) {
                callback.onUnzipFail(zipFile, e.getMessage());
            }
        }
    }

    public interface UnzipCallback {

        void onUnzipComplete(String path);

        void onUnzipFail(String path, String reason);

        void onUnzipEntryFail(String path, String reason);
    }
}
