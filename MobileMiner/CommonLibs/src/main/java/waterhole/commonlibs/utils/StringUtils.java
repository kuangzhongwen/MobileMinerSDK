package waterhole.commonlibs.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import waterhole.commonlibs.R;

/**
 * 字符相关工具
 *
 * @author kzw on 2015/09/16.
 */
public final class StringUtils {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final String ELLIPSIS_SIGN = "…";

    // url 正则
    private static final String URL_REGEX = "\\b((ftp|https?)://[-\\w]+(\\.\\w[-\\w]*)+|(?i:[a-z0-9](?:[-a-z0-9]*[a-z0-9])?\\.)+(?-i:com\\b|edu\\b|biz\\b|gov\\b|in(?:t|fo)\\b|mil\\b|net\\b|org\\b|[a-z][a-z]\\b))(:\\d+)?(/[^.!,?;\"'<>()\\[\\]{}\\s\\x7F-\\xFF]*(?:[.!,?]+[^.!,?;\"'<>()\\[\\]{}\\s\\x7F-\\xFF]+)*)?";

    private static final String COLON_SIGN = ":";

    private StringUtils() {
    }

    /**
     * 根据多个分隔符进行分割
     *
     * @param string     被分割的字符串
     * @param delimiters 分割符
     * @return 分割后的结果数组
     */
    public static String[] splitDelimiters(String string, String... delimiters) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        if (ArraysUtils.isEmpty(delimiters)) {
            return new String[]{string};
        }

        StringBuilder regex = new StringBuilder();
        regex.append('[');
        for (String delimiter : delimiters) {
            regex.append(delimiter).append('|');
        }
        regex.deleteCharAt(regex.length() - 1).append(']');
        return string.split(regex.toString());
    }

    /**
     * 连接字符串，效率比StringBuilder低，但是调用方便
     */
    public static String concat(String... sources) {
        if (ArraysUtils.isEmpty(sources)) {
            return null;
        }
        String result = "";
        for (String source : sources) {
            result.concat(source);
        }
        return result;
    }

    /**
     * Splits a String based on a single character, which is usually faster than regex-based
     * String.split().
     */
    public static String[] fastSplit(String string, char delimiter) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        List<String> list = new ArrayList<>();
        int size = string.length();
        int start = 0;

        for (int i = 0; i < size; i++) {
            if (string.charAt(i) == delimiter) {
                if (start < i) {
                    // substring在jdk 1.7后已解决了内存泄漏问题
                    list.add(string.substring(start, i));
                } else {
                    list.add("");
                }
                start = i + 1;
            } else if (i == size - 1) {
                list.add(string.substring(start, size));
            }
        }

        String[] elements = new String[list.size()];
        list.toArray(elements);
        return elements;
    }

    /**
     * String 原生的startsWith效率远远低于chatAt
     *
     * @param origin 原内容字符
     * @param prefix 前缀字符
     * @return 是否以preix开头
     */
    public static boolean startsWith(String origin, String prefix) {
        if (TextUtils.isEmpty(origin) || TextUtils.isEmpty(prefix) ||
                prefix.length() > origin.length()) {
            return false;
        }

        boolean isStartWith = false;
        char[] chars = prefix.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            isStartWith = origin.charAt(i) == chars[i];
        }
        return isStartWith;
    }

    /**
     * String 原生的endsWith效率远远低于chatAt
     *
     * @param origin 原内容字符
     * @param suffix 后缀字符
     * @return 是否以suffix结尾
     */
    public static boolean endsWith(String origin, String suffix) {
        if (TextUtils.isEmpty(origin) || TextUtils.isEmpty(suffix)) {
            return false;
        }
        int originLen = origin.length();
        int suffixLen = suffix.length();
        if (suffixLen > originLen) {
            return false;
        }

        boolean isEndWith = false;
        char[] chars = suffix.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            isEndWith = origin.charAt(originLen - (i + 1)) == chars[i];
        }
        return isEndWith;
    }

    /**
     * String 原生的contains效率远远低于chatAt
     *
     * @return string中是否包含字符character
     */
    public static boolean contains(String origin, char character) {
        if (TextUtils.isEmpty(origin)) {
            return false;
        }

        char[] chars = origin.toCharArray();
        for (char c : chars) {
            if (character == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将list转换成字符串，中间每个字符串之间加入符号
     */
    public static String listToStringWithDelimiters(final List<String> list, char delimiters) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : list) {
            stringBuilder.append(s).append(delimiters);
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }

    /**
     * 字符串通过分隔符转成list
     */
    public static List<String> stringToListWithDelimiters(String s, char delimiters) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        String[] array = splitDelimiters(s, String.valueOf(delimiters));
        if (array == null) {
            return null;
        }

        return Arrays.asList(array);
    }

    public static String replaceString(int src, int condition, String what) {
        if (src == condition) {
            return what;
        }
        return String.valueOf(src);
    }

    public static String predicatString(String a, String defText) {
        return android.text.TextUtils.isEmpty(a) ? defText : a;
    }

    /**
     * bugfix 解决德语，俄语的逗号代表小数点的问题，在德语和俄语中，输入法的逗号表示小数点，
     * 导致输入框中输入的为带逗号的小数，如1,23。为了解决这个问题，在解析时需要做下兼容。
     *
     * @param input 输入
     * @return 经过兼容处理的字符串
     */
    public static String fixGermanAndRussianInput(String input) {
        return TextUtils.isEmpty(input) ? "" : input.replaceAll(",", "\\.");
    }

    /**
     * 格式化手机号
     */
    public static String formatMobile(String mobile) {
        if (TextUtils.isEmpty(mobile) || mobile.contains("x1234")) {
            return "";
        }
        if (mobile.startsWith("+")) {
            return "+" + trimToDigit(mobile);
        } else {
            return trimToDigit(mobile);
        }
    }

    public static String trimToDigit(String mobile) {
        // 如果包含非数字，则去除
        if (mobile.matches("[0-9]*") == false) {
            char[] srcChar = mobile.toCharArray();
            StringBuffer buffer = new StringBuffer();
            for (char c : srcChar) {
                if (Character.isDigit(c)) {
                    buffer.append(c);
                }
            }
            mobile = buffer.toString();
        }
        return mobile;
    }

    public static String getFileNameFromLink(String linkName) {
        if (TextUtils.isEmpty(linkName)) {
            return "";
        }

        int firstIndex = linkName.indexOf("?");
        if (firstIndex == -1) {
            firstIndex = linkName.length();
        }

        String tmp = linkName.substring(0, firstIndex);
        int secondIndex = tmp.lastIndexOf("/");
        if (secondIndex == -1) {
            return "";
        }
        return tmp.substring(secondIndex + 1);
    }

    public static String encodeBase64String(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return encodeBase64String(getBytesUtf8(str));
    }

    public static String encodeBase64String(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return newStringUtf8(Base64.encodeBase64(bytes));
    }

    public static byte[] encodeBase64(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return encodeBase64(getBytesUtf8(str));
    }

    public static byte[] encodeBase64(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return Base64.encodeBase64(bytes);
    }

    public static String decodeBase64String(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return decodeBase64String(getBytesUtf8(str));
    }

    public static String decodeBase64String(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return newStringUtf8(Base64.decodeBase64(bytes));
        } catch (Exception e) {
            return newStringUtf8(bytes);
        }
    }

    public static byte[] decodeBase64(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return decodeBase64(getBytesUtf8(str));
    }

    public static byte[] decodeBase64(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return Base64.decodeBase64(bytes);
        } catch (Exception e) {
            return bytes;
        }
    }

    public static void autoLinkRefine(TextView textView) {
        CharSequence text = textView.getText();

        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) text;
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            // should clear old spans
            style.clearSpans();
            for (URLSpan url : urls) {
                NonLongClickableUrlSpan myURLSpan = new NonLongClickableUrlSpan(url.getURL());
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url),
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            textView.setText(style);
        }
    }

    /**
     * 获取String的值 如果为空 则返回默认值  否则返回 原值
     */
    public static String getEnsuredString(String str, String defaultStr) {
        return str == null ? (defaultStr == null ? "" : defaultStr) : str;
    }

    /**
     * 获取String的值 如果为空 则返回默认值(空字符串)  否则返回 原值
     */
    public static String getEnsuredString(String str) {
        return getEnsuredString(str, "");
    }

    /**
     * 给字符串加前后引号
     */
    public static String formatStringWithQutotesInBothEnd(String str) {
        return str == null ? "" : new StringBuilder("\"").append(str).append("\"").toString();
    }

    /**
     * 判断两个字符串是否一致，如果两字符串都为空 仍视为一致
     */
    public static boolean strEqualsIngoreEmpty(String str1, String str2) {
        return (str1 == null && str2 == null) || (str1 != null && str2 != null && str1
                .equals(str2));
    }

    public static String getHidePhoneNum(String mobile) {
        StringBuilder sb = new StringBuilder(50);
        sb.append(mobile.substring(0, 3));
        sb.append("******");
        sb.append(mobile.substring(mobile.length() - 2));
        return sb.toString();
    }

    public static byte[] getBytesUtf8(String string) {
        return getBytes(string, UTF_8);
    }

    private static byte[] getBytes(String string, Charset charset) {
        return string == null ? null : string.getBytes(charset);
    }

    private static String newStringUtf8(byte[] bytes) {
        return newString(bytes, UTF_8);
    }

    private static String newString(byte[] bytes, Charset charset) {
        return bytes == null ? null : new String(bytes, charset);
    }

    public static boolean isUrl(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        String httpPattern = "(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        String plainPattern = "^((http(s?))\\://)?(www.|[a-zA-Z].)[a-zA-Z0-9\\-\\.]+\\.(com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk)(\\:[0-9]+)*(/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\\\+&amp;%\\$#\\=~_\\-]+))*$";
        return str.matches(httpPattern) || str.matches(plainPattern);
    }

    public static String removePrefix(String str, String prefix) {
        return str.substring(prefix.length());
    }

    public static String prependStr(String str, String prefix) {
        return new StringBuilder().append(prefix).append(str).toString();
    }

    public static boolean isStartWithHttp(String str) {
        String tmpStr = str.toLowerCase();
        return tmpStr.startsWith("http://") || tmpStr.startsWith("https://");
    }

    public static ClipboardManager getClipboardManager(Context context) {
        if (context == null) {
            return null;
        }
        return (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public static boolean addStringToClipboard(Context context, CharSequence textToCopy) {
        ClipboardManager clipboardManager = getClipboardManager(context);
        if (clipboardManager == null) {
            return false;
        }
        clipboardManager.setText(textToCopy);
        return true;
    }

    public static boolean isDigitOrLetter(char ch) {
        return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ('0' <= ch && ch <= '9');
    }

    /**
     * 隐藏部分字符
     */
    public static String hidePartialDigit(String source) {
        if (TextUtils.isEmpty(source) || source.length() < 9) {
            return source;
        }
        char[] chars = source.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (i < 6 || i > len - 3) {
                continue;
            }
            chars[i] = '*';
        }
        return new String(chars);
    }

    /**
     * 在字符串后面加上 “...”
     */
    public static String appendDot(String s) {
        return s + ELLIPSIS_SIGN;
    }

    public static String appendDot(Context context, int res) {
        return UIUtils.getString(context, res) + ELLIPSIS_SIGN;
    }

    public static String appendColon(CharSequence s) {
        return s + COLON_SIGN;
    }

    public static String appendColon(Context context, int res) {
        return UIUtils.getString(context, res) + COLON_SIGN;
    }

    public static String replaceAllSpace(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }

        return text.replaceAll(" ", "");
    }

    /**
     * //判断文本中是否含有URL
     *
     * @param text 获取浏览器分享出来的text文本
     */
    public static boolean containsUrl(String text) {
        Pattern p = Pattern.compile(
                "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        return matcher.find();
    }

    /**
     * //获取完整的域名
     *
     * @param text 获取浏览器分享出来的text文本
     */
    public static String getCompleteUrl(String text) {
        String url;
        Pattern p = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        matcher.find();
        try {
            url = matcher.group();
        } catch (Exception e) {
            url = "";
        }
        return url;
    }

    public static String getStringValueFromMap(Map map, String key) {
        if (map == null || TextUtils.isEmpty(key) || !map.containsKey(key)) {
            return "";
        }

        Object object = map.get(key);
        if (object != null && object instanceof String) {
            return (String) object;
        }

        return "";
    }

    public static Double getDoubleValueFromMap(Map map, String key) {
        if (map == null || TextUtils.isEmpty(key) || !map.containsKey(key)) {
            return 0d;
        }

        Object object = map.get(key);
        if (object != null) {
            if (object instanceof Double) {
                return (Double) object;
            } else if (object instanceof String) {
                return MathUtils.parseDouble((String) object);
            }
        }

        return 0d;
    }

    public static Object getValueFromMap(Map map, String key) {
        if (map == null || TextUtils.isEmpty(key) || !map.containsKey(key)) {
            return null;
        }

        Object object = map.get(key);
        if (object != null) {
            return object;
        }

        return null;
    }

    public static SpannableStringBuilder setStrikethroughTextView(CharSequence text, int from,
                                                                  int to) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        StrikethroughSpan ss = new StrikethroughSpan();
        ssb.setSpan(ss, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ssb;
    }

    public static class NonLongClickableUrlSpan extends ClickableSpan {

        public static final String TAG = "NonLongClickableUrlSpan";

        private final String mUrl;

        NonLongClickableUrlSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            if (widget.getTag(R.integer.non_long_clickable_url_span_tag) != null) {
                widget.setTag(R.integer.non_long_clickable_url_span_tag, null);
                return;
            }
            Uri uri = Uri.parse(mUrl);
            Context context = widget.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            context.startActivity(intent);
        }
    }
}
