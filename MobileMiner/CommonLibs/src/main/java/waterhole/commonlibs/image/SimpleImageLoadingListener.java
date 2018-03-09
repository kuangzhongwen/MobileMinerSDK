package waterhole.commonlibs.image;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 简单的{@link ImageLoadingListener}（适配器）
 * 当希望选择回调ImageLoadingListener的部分方法时，可以传入该类型。
 *
 * @author kzw on 2017/07/22.
 */
public class SimpleImageLoadingListener implements ImageLoadingListener {

    @Override
    public void onLoadingStarted(String s, View view) {
    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {
    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
    }

    @Override
    public void onLoadingCancelled(String s, View view) {
    }
}
