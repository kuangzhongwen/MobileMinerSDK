package waterhole.miner.core.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 生成全排列
 *
 * @author kzw on 2017/3/18.
 */
public final class PermutationUtils {

    public PermutationUtils() {
        throw new RuntimeException("PermutationUtils stub!");
    }

    /**
     * 获得全排列
     *
     * @param in T类型的数组
     * @return 所有数组元素全排列的结果
     */
    public static <T> List<T[]> arrange(final T[] in) {
        if (in == null) {
            return null;
        }
        List<T[]> list = new LinkedList<>();
        arrange(in, 0, in.length, list);
        return list;
    }


    private static <T> void arrange(final T[] in, int start, int len, final List<T[]> out) {
        if (in == null) {
            return;
        }
        if (out == null) {
            throw new IllegalArgumentException("out is null");
        }
        if (start == len - 1) {
            out.add(Arrays.copyOf(in, in.length));
        } else {
            for (int i = start; i < len; i++) {
                swap(in, start, i);
                arrange(in, start + 1, len, out);
                swap(in, start, i);
            }
        }
    }

    private static void swap(final Object[] str, int i, int j) {
        Object temp = str[i];
        str[i] = str[j];
        str[j] = temp;
    }
}