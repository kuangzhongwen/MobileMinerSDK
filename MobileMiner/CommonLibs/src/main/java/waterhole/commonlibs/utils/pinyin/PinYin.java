package waterhole.commonlibs.utils.pinyin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import waterhole.commonlibs.utils.pinyin.HanziToPinyin3.Token;

public class PinYin implements Serializable {

    public static final String PINYIN_PREFIX = "#";

    public static class PinYinElement implements Serializable {

        public String pinyin;
        List<String> tokenPinyinList = new ArrayList<>();
        String tokenFirstChars = "";

        @Override
        public String toString() {
            StringBuilder part1 = new StringBuilder("PinYinElement [pinyin=" + pinyin
                    + ", firstChars=" + tokenFirstChars + "]");
            StringBuilder part2 = new StringBuilder("tokenPinyinList:");
            for (String tokenPinyin : tokenPinyinList) {
                part2.append(tokenPinyin).append(",");
            }

            return part1.append(part2).toString();
        }

        //@YM getPinYin 调用的入口太多，暂时临时解决
        public void clear() {
            tokenFirstChars = "";
            tokenPinyinList.clear();
            pinyin = null;
        }
    }

    // 汉字返回拼音，字母原样返回，都转换为小写
    // 函数的名称也是存在问题
    public static void getPinYin(String input, PinYinElement pinyinElement) {
        ArrayList<Token> tokens = HanziToPinyin3.getInstance().get(input);

        StringBuilder sb = new StringBuilder();
        pinyinElement.clear();
        if (tokens != null && tokens.size() > 0) {
            for (Token token : tokens) {

                if (Token.PINYIN == token.type) {
                    sb.append(token.target);
                    pinyinElement.tokenPinyinList.add(token.target);
                    pinyinElement.tokenFirstChars += token.target.substring(0, 1);
                } else {
                    sb.append(token.source);
                    for (int i = 0; i < token.source.length(); ++i) {
                        String childString = token.source.substring(i, i + 1).toUpperCase();
                        pinyinElement.tokenPinyinList.add(childString);
                        pinyinElement.tokenFirstChars += childString;
                    }
                }
            }
        }

        String ret = sb.toString().toUpperCase();
        if (!ret.isEmpty()) {
            int firstChar = ret.charAt(0);
            if (!(firstChar >= 'A' && firstChar <= 'Z')) {
                ret = PINYIN_PREFIX + ret;
            }
        }

        pinyinElement.pinyin = ret;
    }
}
