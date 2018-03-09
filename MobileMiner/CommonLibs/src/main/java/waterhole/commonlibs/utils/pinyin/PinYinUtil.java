package waterhole.commonlibs.utils.pinyin;


import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 拼音util
 *
 * @author kzw on 2015/12/09.
 */
public final class PinYinUtil {

    private PinYinUtil() {
    }

    private static String toPinYin(String hanzhis) {
        if (TextUtils.isEmpty(hanzhis)) {
            return "";
        }
        PinYin.PinYinElement pinYinElement = new PinYin.PinYinElement();
        PinYin.getPinYin(hanzhis, pinYinElement);

        return pinYinElement.pinyin;
    }

    /**
     * 是否是英文
     */
    private static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }

    private static boolean isChinese(String str) {
        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        return m.find();
    }

    public static String getPinYinElement(String string) {
        String result;
        if (!TextUtils.isEmpty(string)) {
            String first = string.substring(0, 1);
            if (isChinese(first)) {
                try {
                    result = toPinYin(first).toUpperCase().substring(0, 1);
                } catch (Throwable e) {
                    result = PinYin.PINYIN_PREFIX;
                }
            } else if (isEnglish(first)) {
                result = first.toUpperCase();
            } else {
                result = PinYin.PINYIN_PREFIX;
            }
        } else {
            result = PinYin.PINYIN_PREFIX;
        }

        return result;
    }

    public static int getFirstLetter(String s) {
        if (TextUtils.isEmpty(s)) {
            return -1;
        }

        return s.charAt(0);
    }
}
