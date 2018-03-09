package waterhole.commonlibs.utils;

import android.graphics.Color;

/**
 * 颜色相关工具类
 *
 * @author kzw on 2015/07/28.
 */
public final class ColorUtils {

    /**
     * Private constructor to prohibit nonsense instance creation.
     */
    private ColorUtils() {
    }

    /**
     * Set the alpha value of the {@code color} to be the given {@code alpha} value.
     */
    public static int setColorAlpha(int color, byte alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Blend {@code color1} and {@code color2} using the given ratio.
     *
     * @param ratio of which to blend. 1.0 will return {@code color1}, 0.5 will give an even blend,
     *              0.0 will return {@code color2}.
     */
    public static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }
}
