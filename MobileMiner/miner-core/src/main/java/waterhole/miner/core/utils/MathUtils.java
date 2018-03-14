package waterhole.miner.core.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * 基本math运算
 *
 * @author kzw on 2016/01/14.
 */
public final class MathUtils {

    // double默认值
    private static final double DEF_DOUBLE = 0.0;
    // float默认值
    private static final float DEF_FLOAT = 0.0f;
    // long 默认值
    private static final long DEF_LONG = 0;
    // int 默认值
    private static final int DEF_INT = 0;

    // 保留1位小数
    private static final CharSequence FORMAT_KEEP_1 = "0.0";
    // 保留2位小数
    private static final CharSequence FORMAT_KEEP_2 = "0.00";
    // 保留3位小数
    private static final CharSequence FORMAT_KEEP_3 = "0.000";
    // 保留8位小数
    private static final CharSequence FORMAT_KEEP_8 = "#0.########";

    private MathUtils() {
    }

    /**
     * String 解析成double
     */
    public static double parseDouble(String value) {
        if (TextUtils.isEmpty(value)) {
            return DEF_DOUBLE;
        }
        try {
            // 统一在这个地方处理输入法小数点适配问题（德语，俄语,表示.）
            return Double.parseDouble(StringUtils.fixGermanAndRussianInput(value));
        } catch (NumberFormatException e) {
            return DEF_DOUBLE;
        }
    }

    /**
     * String 解析成double，未处理异常，适用于外部想处理异常，做一些提示，特殊处理的情况
     */
    public static double parseDoubleWithoutHandleException(String value) {
        if (TextUtils.isEmpty(value)) {
            return DEF_DOUBLE;
        }
        // 统一在这个地方处理输入法小数点适配问题（德语，俄语,表示.）
        return Double.parseDouble(StringUtils.fixGermanAndRussianInput(value));
    }

    /**
     * String 解析成float
     */
    private static float parseFloat(String value) {
        if (TextUtils.isEmpty(value)) {
            return DEF_FLOAT;
        }
        try {
            return Float.parseFloat(StringUtils.fixGermanAndRussianInput(value));
        } catch (NumberFormatException e) {
            return DEF_FLOAT;
        }
    }

    /**
     * String 解析成long
     */
    public static long parseLong(String value) {
        if (TextUtils.isEmpty(value)) {
            return DEF_LONG;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return DEF_LONG;
        }
    }

    /**
     * String 解析成int
     */
    public static int parseInt(String value) {
        if (TextUtils.isEmpty(value)) {
            return DEF_INT;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return DEF_INT;
        }
    }

    /**
     * double保留1位小数
     */
    private static double parseDoubleKeep1(double value) {
        return parseDoubleKeepDecimal(value, FORMAT_KEEP_1);
    }

    /**
     * double保留1位小数
     */
    public static double parseDoubleKeep1(String value) {
        return parseDoubleKeep1(parseDouble(value));
    }

    /**
     * double保留2位小数
     */
    public static double parseDoubleKeep2(double value) {
        return parseDoubleKeepDecimal(value, FORMAT_KEEP_2);
    }

    /**
     * double保留2位小数
     */
    public static double parseDoubleKeep2(String value) {
        return parseDoubleKeep2(parseDouble(value));
    }

    /**
     * double保留3位小数
     */
    public static double parseDoubleKeep3(double value) {
        return parseDoubleKeepDecimal(value, FORMAT_KEEP_3);
    }

    /**
     * double保留3位小数
     */
    public static double parseDoubleKeep3(String value) {
        return parseDoubleKeep3(parseDouble(value));
    }

    /**
     * double保留8位小数
     */
    public static double parseDoubleKeep8(double value) {
        return parseDoubleKeepDecimal(value, FORMAT_KEEP_8);
    }

    /**
     * double保留8位小数
     */
    public static double parseDoubleKeep8(String value) {
        return parseDoubleKeep8(parseDouble(value));
    }

    /**
     * float保留1位小数
     */
    private static float parseFloatKeep1(float value) {
        return parseFloatKeepDecimal(value, FORMAT_KEEP_1);
    }

    /**
     * float保留1位小数
     */
    public static float parseLongKeep1(String value) {
        return parseFloatKeep1(parseFloat(value));
    }

    /**
     * float保留2位小数
     */
    private static float parseFloatKeep2(float value) {
        return parseFloatKeepDecimal(value, FORMAT_KEEP_2);
    }

    /**
     * float保留2位小数
     */
    public static float parseLongKeep2(String value) {
        return parseFloatKeep2(parseFloat(value));
    }

    /**
     * float保留3位小数
     */
    private static float parseFloatKeep3(float value) {
        return parseFloatKeepDecimal(value, FORMAT_KEEP_3);
    }

    /**
     * float保留3位小数
     */
    public static float parseFloatKeep3(String value) {
        return parseFloatKeep3(parseFloat(value));
    }

    /**
     * float保留8位小数
     */
    private static float parseFloatKeep8(float value) {
        return parseFloatKeepDecimal(value, FORMAT_KEEP_8);
    }

    /**
     * float保留8位小数
     */
    public static float parseFloatKeep8(String value) {
        return parseFloatKeep8(parseFloat(value));
    }

    /**
     * 转成String 保留1位小数
     */
    public static String parseStringKeep1(double value) {
        return parseStringKeepDecimal(value, FORMAT_KEEP_1);
    }

    /**
     * 转成String 保留2位小数
     */
    public static String parseStringKeep2(double value) {
        return parseStringKeepDecimal(value, FORMAT_KEEP_2);
    }

    /**
     * 转成String 保留3位小数
     */
    public static String parseStringKeep3(double value) {
        return parseStringKeepDecimal(value, FORMAT_KEEP_3);
    }

    /**
     * 转成String 保留8位小数
     */
    public static String parseStringKeep8(double value) {
        return parseStringKeepDecimal(value, FORMAT_KEEP_8);
    }

    /**
     * double保留几位小数
     */
    public static double parseDoubleKeepDecimal(String value, CharSequence format) {
        return parseDoubleKeepDecimal(parseDouble(value), format.toString());
    }

    /**
     * float保留几位小数
     */
    public static float parseFloatKeepDecimal(String value, CharSequence format) {
        return parseFloatKeepDecimal(parseFloat(value), format.toString());
    }

    private static String parseStringKeepDecimal(double value, CharSequence format) {
        return parseStringKeepDecimal(value, format.toString());
    }

    /**
     * double保留几位小数
     */
    private static double parseDoubleKeepDecimal(double value, CharSequence format) {
        return parseDoubleKeepDecimal(value, format.toString());
    }

    /**
     * float保留几位小数
     */
    private static float parseFloatKeepDecimal(float value, CharSequence format) {
        return parseFloatKeepDecimal(value, format.toString());
    }

    private static String parseStringKeepDecimal(double value, String format) {
        if (TextUtils.isEmpty(format)) {
            throw new IllegalArgumentException("format is null");
        }
        return DecimalFormatCreator(value, format);
    }

    /**
     * double保留几位小数
     */
    private static double parseDoubleKeepDecimal(double value, String format) {
        if (TextUtils.isEmpty(format)) {
            throw new IllegalArgumentException("format is null");
        }
        return parseDouble(DecimalFormatCreator(value, format));
    }

    /**
     * float保留几位小数
     */
    private static float parseFloatKeepDecimal(float value, String format) {
        if (TextUtils.isEmpty(format)) {
            throw new IllegalArgumentException("format is null");
        }
        return parseFloat(DecimalFormatCreator(value, format));
    }

    private static String DecimalFormatCreator(double value, String format) {
        return new DecimalFormat(format).format(value);
    }

    public static double getDiagonalLength(int width, int height) {
        return Math.sqrt(width * width + height * height);
    }

    /**
     * 保存double的整数部分
     */
    public static int keepDoubleInt(double value) {
        return Double.valueOf(value).intValue();
    }
}
