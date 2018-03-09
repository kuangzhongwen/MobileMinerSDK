package waterhole.commonlibs.image;

import static android.graphics.BitmapFactory.decodeStream;
import static waterhole.commonlibs.utils.IOUtils.closeSafely;
import static waterhole.commonlibs.utils.LogUtils.error;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 抽象的图片解码器。接入方可以实现{@link #isJustDecode(String)}过滤直接解码，不需要任何处理的图片，比如非app内置的图片.
 *
 * @author kzw on 2017/07/22.
 */
public abstract class AbsImageDecoder implements ImageDecoder {

    private static final String TAG = "AbsImageDecoder";

    // IO缓存大小
    private static final int IO_BUFFER = 1024;

    /**
     * 直接解码，不需要任何处理
     *
     * @param imageUri 图片的url
     */
    protected abstract boolean isJustDecode(String imageUri);

    /**
     * 对图片的字节码信息进行解码处理
     *
     * @param bytes 图片的字节码
     */
    protected abstract byte[] decode(byte[] bytes);

    @Override
    public final Bitmap decode(ImageDecodingInfo imageDecodingInfo) throws IOException {
        String imageUri = imageDecodingInfo.getOriginalImageUri();
        InputStream inputStream = null;
        // 如果直接解码，不需要任何处理
        if (isJustDecode(imageUri)) {
            try {
                inputStream = getImageInputStream(imageDecodingInfo);
                return decodeStream(inputStream);
            } catch (IOException e) {
                error(TAG, "Just decode error:" + e.getMessage());
                return null;
            } finally {
                closeSafely(inputStream);
            }
        }
        // 需要解码
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            inputStream = getImageInputStream(imageDecodingInfo);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] imageBytes = new byte[IO_BUFFER];
            while ((inputStream.read(imageBytes, 0, IO_BUFFER)) != -1) {
                byteArrayOutputStream.write(imageBytes);
                byteArrayOutputStream.flush();
            }
            byte[] basedImageBytes = byteArrayOutputStream.toByteArray();
            basedImageBytes = decode(basedImageBytes);
            // 根据流构建位图
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap decodedBitmap;
            try {
                decodedBitmap = decodeBitmapByBytes(basedImageBytes, options);
            } catch (OutOfMemoryError e) {
                error(TAG, "decodeBitmapByBytes error:" + e.getMessage());
                System.gc();
                options.inSampleSize = 4;
                decodedBitmap = decodeBitmapByBytes(basedImageBytes, options);
            }
            decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream);
            return decodedBitmap;
        } catch (Exception e) {
            error(TAG, "decode error:" + e.getMessage());
            return null;
        } finally {
            closeSafely(inputStream);
            closeSafely(byteArrayOutputStream);
        }
    }

    /**
     * 通过字节码解码生成位图
     *
     * @param basedImageBytes 字节码
     * @param options         {@link Options}
     */
    private static Bitmap decodeBitmapByBytes(byte[] basedImageBytes, Options options) {
        return decodeStream(new ByteArrayInputStream(basedImageBytes), null, options);
    }

    /**
     * 获取图片的输入流
     *
     * @param decodingInfo {@link ImageDecodingInfo}
     */
    private static InputStream getImageInputStream(ImageDecodingInfo decodingInfo) throws IOException {
        return decodingInfo.getDownloader().getStream(decodingInfo.getImageUri(), decodingInfo.getExtraForDownloader());
    }
}
