package waterhole.commonlibs.image;

import waterhole.commonlibs.utils.StringUtils;

/**
 * 抽象的图片解码器，属于内置提供的一种。接入方可以重写{@link #isJustDecode(String)}
 * 过滤直接解码，不需要任何处理的图片，比如非app内置的图片.
 *
 * @author kzw on 2017/07/22.
 */
public class Base64ImageDecoder extends AbsImageDecoder {

    @Override
    protected boolean isJustDecode(String imageUri) {
        // 这边默认都要进行解码处理
        return false;
    }

    @Override
    protected byte[] decode(byte[] bytes) {
        return StringUtils.decodeBase64(bytes);
    }
}
