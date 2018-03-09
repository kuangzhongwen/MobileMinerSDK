package waterhole.commonlibs.utils;

import android.util.ArrayMap;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 集合工具类
 *
 * @author kzw on 2017/05/23.
 */
public final class CollectionUtils {

    public CollectionUtils() {
        throw new RuntimeException("CollectionUtils stub!");
    }

    public static boolean isEmpty(final List<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean isEmpty(final Set<?> set) {
        return set == null || set.isEmpty();
    }

    /**
     * List通过转换成Array输出成String
     */
    public static String listToStringByArray(final List<?> list) {
        if (isEmpty(list)) {
            return null;
        }
        return Arrays.toString(list.toArray());
    }

    /**
     * Set通过转换成Array输出成String
     */
    public static String setToStringByArray(final Set<?> set) {
        if (isEmpty(set)) {
            return null;
        }
        return Arrays.toString(set.toArray());
    }

    /**
     * 打印集合
     */
    public static <AnyType> void printCollection(Collection<AnyType> c) {
        Iterator<AnyType> iterator = c.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next());
        }
    }

    /**
     * 将list转化成一个不可修改的list
     *
     * @param list 原始list
     */
    public static <AnyType> List<AnyType> unmodifiableList(List<AnyType> list) {
        if (list == null) {
            throw new RuntimeException("list is Null!");
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * 将set转化成一个不可修改的set
     *
     * @param set 原始set
     */
    public static <AnyType> Set<AnyType> unmodifiableList(Set<AnyType> set) {
        if (set == null) {
            throw new RuntimeException("list is Null!");
        }
        return Collections.unmodifiableSet(set);
    }


    /**
     * 获取空集合
     *
     * @return 根据不同平台构建的空集合
     */
    public static <K, V> Map<K, V> getEmptyMap() {
        Map<K, V> map;

        if (APIUtils.hasKitKat()) {
            map = new ArrayMap<>();
        } else {
            map = new HashMap<>();
        }
        return map;
    }

    /**
     * @return 创建一个项，它表示从指定键到指定值的映射关系
     */
    public static <K, V> AbstractMap.SimpleEntry<K, V> getSimpleEntry(K k, V v) {
        return new AbstractMap.SimpleEntry<>(k, v);
    }

    /**
     * 将map转化成一个不可修改的map
     *
     * @param map 原始map
     * @param <K> key
     * @param <V> value
     */
    public static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
        if (map == null) {
            throw new RuntimeException("map is Null!");
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * 比较两个集合的元素是否相等
     */
    public static <T extends Comparable<T>> boolean compare(List<T> a, List<T> b) {
        if (a.size() != b.size()) {
            return false;
        }
        Collections.sort(a);
        Collections.sort(b);
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i))) {
                return false;
            }
        }
        return true;
    }
}
