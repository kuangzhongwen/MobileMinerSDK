package waterhole.miner.core.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Assets工具
 *
 * @author kzw on 2016/01/05.
 */
public final class AssetsUtils {

    private AssetsUtils() {
    }

    public static String getFromAssets(Context context, String filename) {
        ByteArrayOutputStream outStream = null;
        InputStream inStream = null;
        try {
            outStream = new ByteArrayOutputStream();
            AssetManager assetManager = context.getResources().getAssets();
            inStream = assetManager.open(filename);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            // do nothing
        } finally {
            IOUtils.closeSafely(outStream);
            IOUtils.closeSafely(inStream);
        }
        return outStream.toString();
    }

    public static String[] openDirFromAsset(Context context, String file) {
        try {
            return context.getAssets().list(file);
        } catch (IOException e) {
            return null;
        }
    }

    public static Bitmap getImageFromAsset(Context context, String file) throws IOException {
        return context != null && !TextUtils.isEmpty(file) ?
                BitmapFactory.decodeStream(context.getAssets().open(file)) : null;
    }

    public static Bitmap getImageFromAssetCatchException(Context context, String file) {
        try {
            return getImageFromAsset(context, file);
        } catch (IOException e) {
            return null;
        }
    }

    public static void copyAssetsFile(Context context, String assetFilePath, String localFilePath) {
        try {
            InputStream in = context.getAssets().open(assetFilePath);
            FileOutputStream out = new FileOutputStream(localFilePath);
            int read;
            byte[] buffer = new byte[4096];
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            out.close();
            in.close();

            File bin = new File(localFilePath);
            bin.setExecutable(true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
