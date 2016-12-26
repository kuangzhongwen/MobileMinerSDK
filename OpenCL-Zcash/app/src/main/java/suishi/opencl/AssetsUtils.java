package suishi.opencl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

/**
 * Assets工具
 *
 * @author kuang
 * @since 2016-1-5
 */
final class AssetsUtils {

	private AssetsUtils() {}

	static Bitmap getImageFromAsset(Context context, String file) {
		try {
			return BitmapFactory.decodeStream(context.getAssets().open(file));
		} catch (IOException e) {
			return null;
		}
	}
}
