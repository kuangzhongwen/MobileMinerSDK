package waterhole.commonlibs.utils;

/**
 * 查找算法工具类
 *
 * @author kzw on 2017/07/06.
 */
public final class SearchUtils {

    public SearchUtils() {
        throw new RuntimeException("SearchUtils stub!");
    }

    /**
     * 顺序查找，平均时间复杂度 O（n）
     *
     * @param searchKey 要查找的值
     * @param array     数组（从这个数组中查找）
     * @return 查找结果（数组的下标位置）
     */
    public static int orderSearch(int searchKey, int[] array) {
        if (array == null || array.length < 1) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == searchKey) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 二分查找又称折半查找，它是一种效率较高的查找方法。 【二分查找要求】：1.必须采用顺序存储结构 2.必须按关键字大小有序排列。
     *
     * @param array     有序数组 *
     * @param searchKey 查找元素 *
     * @return searchKey的数组下标，没找到返回-1
     */
    public static int binarySearch(int[] array, int searchKey) {
        int low = 0;
        int high = array.length - 1;
        while (low <= high) {
            int middle = (low + high) / 2;
            if (searchKey == array[middle]) {
                return middle;
            } else if (searchKey < array[middle]) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        return -1;
    }

    /**
     * 分块查找
     * <p>
     * a. 首先将查找表分成若干块，在每一块中数据元素的存放是任意的，但块与块之间必须是有序的
     * （假设这种排序是按关键字值递增的，也就是说在第一块中任意一个数据元素的关键字都小于第二块中所有数据元素的关键字，
     * 第二块中任意一个数据元素的关键字都小于第三块中所有数据元素的关键字，依次类推）；
     * <p>
     * b. 建立一个索引表，把每块中最大的关键字值按块的顺序存放在一个辅助数组中，这个索引表也按升序排列；
     * <p>
     * c. 查找时先用给定的关键字值在索引表中查找，确定满足条件的数据元素存放在哪个块中，查找方法既可以是折半方法，也可以是顺序查找。
     * <p>
     * d. 再到相应的块中顺序查找，便可以得到查找的结果。
     */
    public static int blockSearch(int[] index, int[] st, int key, int m) {
        // 在序列st数组中，用分块查找方法查找关键字为key的记录
        // 1.在index[ ] 中折半查找，确定要查找的key属于哪个块中
        int i = binarySearch(index, key);
        if (i >= 0) {
            int j = i > 0 ? i * m : i;
            int len = (i + 1) * m;
            // 在确定的块中用顺序查找方法查找key
            for (int k = j; k < len; k++) {
                if (key == st[k]) {
                    System.out.println("查询成功");
                    return k;
                }
            }
        }
        System.out.println("查找失败");
        return -1;
    }

    /**
     * 哈希查找
     * <p>
     * 哈希表查找是通过对记录的关键字值进行运算，直接求出结点的地址，是关键字到地址的直接转换方法，
     * 不用反复比较。假设f包含n个结点，Ri为其中某个结点（1≤i≤n），
     * keyi是其关键字值，在keyi与Ri的地址之间建立某种函数关系，可以通过这个函数把关键字值转换成相应结点的地址，
     * 有：addr(Ri)=H(keyi)，addr(Ri)为哈希函数。
     * <p>
     * 解决冲突的方法有以下两种：
     * (1)开放地址法
     * 如果两个数据元素的哈希值相同，则在哈希表中为后插入的数据元素另外选择一个表项。
     * 当程序查找哈希表时，如果没有在第一个对应的哈希表项中找到符合查找要求的数据元素，
     * 程序就会继续往后查找，直到找到一个符合查找要求的数据元素，或者遇到一个空的表项。
     * <p>
     * (2)链地址法
     * 将哈希值相同的数据元素存放在一个链表中，在查找哈希表的过程中，当查找到这个链表时，必须采用线性查找方法。
     */
    public static int searchHash(int[] hash, int hashLength, int key) {
        // 哈希函数
        int hashAddress = key % hashLength;
        // 指定hashAdrress对应值存在但不是关键值，则用开放寻址法解决
        while (hash[hashAddress] != 0 && hash[hashAddress] != key) {
            hashAddress = (++hashAddress) % hashLength;
        }
        // 查找到了开放单元，表示查找失败
        if (hash[hashAddress] == 0) {
            return -1;
        }
        return hashAddress;
    }

    /***
     * 数据插入Hash表
     *
     * @param hash
     *     哈希表
     */
    public static void insertHash(int[] hash, int hashLength, int data) {
        // 哈希函数
        int hashAddress = data % hashLength;
        // 如果key存在，则说明已经被别人占用，此时必须解决冲突
        while (hash[hashAddress] != 0) {
            // 用开放寻址法找到
            hashAddress = (++hashAddress) % hashLength;
        }
        // 将data存入字典中
        hash[hashAddress] = data;
    }
}
