package waterhole.miner.core.utils;

import java.util.Arrays;
import java.util.List;

/**
 * 排序工具类
 *
 * @author kzw on 2017/07/06.
 */
public final class SortUtils {

    public SortUtils() {
        throw new RuntimeException("SortUtils stub!");
    }

    /**
     * 找出一个数组中是否有重复元素，给出最坏情况就是遍历的二次简单方法。但是如果我们对数组进行排序，
     * 则重复元素必定相邻，通过单一时间扫描就能检测到重复元素，排序时间在这个算法中占主导作用。
     */
    public static boolean duplicates(Object[] a) {
        for (int i = 0, len = a.length; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (a[i].equals(a[j])) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 插入排序
     * <p>
     * 插入排序是一种简单的排序，适用于少量数据。如果只是对几个元素排序，插入排序是是一种很好的解决方案。
     * 因为插入排序是这么短的算法，并且排序的时间不成问题。不过我们要处理大量数据，插入排序不是一种好的选择。
     * <p>
     * 插入排序的工作原理，第一个元素自身是有序的，每一个元素都比较一下。
     * <p>
     * 每步将一个待排序的记录，按其顺序码大小插入到前面已经排序的字序列的合适位置
     * （从后向前找到合适位置后），直到全部插入排序完为止。
     */
    public static <AnyType extends Comparable<? super AnyType>> void insertSort(
            List<AnyType> list) {
        for (int i = 1, len = list.size(); i < len; i++) {
            AnyType tmp = list.get(i);
            int j = i;
            for (; j > 0 && tmp.compareTo(list.get(j - 1)) < 0; j--) {
                list.set(j, list.get(j - 1));
            }
            list.set(j, tmp);
        }
    }

    /**
     * 希尔排序
     * <p> 希尔排序是次二次算法，它的代码只比插入排序多一点，是更快算法中最简单的算法
     * 希尔排序避免大量的数据移动，通过先比较相距较远的元素，再比较相对较近的元素。
     *
     * @param list 集合
     */
    public static <AnyType extends Comparable<? super AnyType>> void shellSort(
            final List<AnyType> list) {
        for (int size = list.size(), gap = size / 2; gap > 0;
             gap = gap == 2 ? 1 : (int) (gap / 2.2f)) {
            for (int i = gap; i < size; i++) {
                AnyType tmp = list.get(i);
                int j = i;
                for (; j >= gap && tmp.compareTo(list.get(j - gap)) < 0; j -= gap) {
                    list.set(j, list.get(j - gap));
                }
                list.set(j, tmp);
            }
        }
    }

    /**
     * 归并排序
     * <p>
     * 归并排序的步骤分为三步：
     * 1.如果待p排序的个数为0或1，则直接返回。
     * 2.分别递归地对前半部分和后半部分排序。
     * 3.将两个有序部分归并成一个有序组。
     * <p>
     * 我们将学习如何将两个数组进行归并，并将结果放在第三个数组中。
     * <p>
     * 尽管归并排序的时间复杂度为O(Nlog(N))，但是空间复杂度高，归并需要两个额外的数组，在整个算法中，还有一个
     * 额外的操作，就是将元素数组复制到临时数组，再将临时数组复制回原数组，这会大大降低排序速度。
     * 通过在递归中交替层明智而谨慎地切换a和tmpArray的角色，就可以避免这种复制。
     * <p>
     * 归并排序的运行时间很大程度上取决于在数组和临时数组中比较元素和移动元素的代价。
     * 在java中，在通过对对象进行排序时，元素代价比较大，因此在通用设置中，是通过函数对象对元素进行比较。
     * 另一方面，移动元素的代价比较小，因为不进行元素的复制，而只是简单的引用改变。
     * <p>
     * 在所有流行算法中，归并排序的比较次数最少，因而在java中，它是通用算法的最佳算法。
     * 在java中，Arrays.sort()，对象数组使用的排序就是归并排序。这些基本相对代价既不适合于其他语言，
     * 也不适合于java的基本数据类型。另一种可选的排序方法是快速排序算法。Arrays.sort()对基本数据类型的排序
     * 采用的就是快速排序。
     */
    public static <AnyType extends Comparable<? super AnyType>> void mergeSort(final AnyType[] a) {
        AnyType[] tmpArray = (AnyType[]) new Comparable[a.length];
        mergeSort(a, tmpArray, 0, a.length - 1);
    }

    private static <AnyType extends Comparable<? super AnyType>> void mergeSort(final AnyType[] a,
                                                                                final AnyType[] tmpArray, int left, int right) {
        if (left < right) {
            int center = (left + right) >>> 1;
            mergeSort(a, tmpArray, left, center);
            mergeSort(a, tmpArray, center + 1, right);
            merge(a, tmpArray, left, center + 1, right);
        }
    }

    private static <AnyType extends Comparable<? super AnyType>> void merge(final AnyType[] a,
                                                                            final AnyType[] tmpArray, int left, int right, int rightEnd) {
        int leftEnd = right - 1;
        int tmp = left;
        int nums = right - left + 1;
        // main loop
        while (left <= leftEnd && right <= rightEnd) {
            if (a[left].compareTo(a[right]) <= 0) {
                tmpArray[tmp++] = a[left++];
            } else {
                tmpArray[tmp++] = a[right++];
            }
        }
        while (left <= leftEnd) {
            // copy rest of left half
            tmpArray[tmp++] = a[left++];
        }
        while (right <= rightEnd) {
            // copy rest of right half
            tmpArray[tmp++] = a[right++];
        }
        // copy tmpArray back
        for (int i = 0; i < nums; i++, rightEnd--) {
            a[rightEnd] = tmpArray[rightEnd];
        }
    }

    /**
     * 快速排序
     * <p>
     * 快速排序和归并排序都使用分治法来设计算法，区别在于归并排序把数组分为两个基本等长的子数组，
     * 分别排好序之后还要进行归并(Merge)操作，而快速排序拆分子数组的时候显得更有艺术，取一个基准元素，
     * 拆分之后基准元素左边的元素都比基准元素小，右边的元素都不小于基准元素，
     * 这样只需要分别对两个子数组排序即可，不再像归并排序一样需要归并操作。
     * 基准元素的选取对算法的效率影响很大，最好的情况是两个子数组大小基本相当。
     * 为简单起见，我们选择最后一个元素，更高级的做法可以先找一个中位数并把中位数与最后一个元素交换，
     * 之后再进行相同的操作步骤。
     * <p>
     * 拆分是快速排序的核心。快速排序的最坏运行时间是O(n2)，但期望的运行时间是O(nlgn)。
     * <p>
     * <p>
     * 快速排序算法Java实现:
     * 1.把数组拆分为两个子数组加上一个基准元素: 选取最后一个元素作为基准元素，index变量记录最近一个小于基准元素的元素所在的位置，初始化为start- 1，发现新的小于基准元素的元素，index加1。从第一个元素到倒数第二个元素，依次与基准元素比较，小于基准元素，index加1，交换位置index和当前位置的元素。循环结束之后index+1得到基准元素应该在的位置，交换index+1和最后一个元素。
     * 2. 分别排序[start, index], 和[index+2, end]两个子数组
     */
    public static void quickSort(int[] array) {
        subQuickSort(array, 0, array.length - 1);
    }

    private static void subQuickSort(int[] array, int start, int end) {
        if (array == null || (end - start + 1) < 2) {
            return;
        }

        int part = partition(array, start, end);

        if (part == start) {
            subQuickSort(array, part + 1, end);
        } else if (part == end) {
            subQuickSort(array, start, part - 1);
        } else {
            subQuickSort(array, start, part - 1);
            subQuickSort(array, part + 1, end);
        }
    }

    private static int partition(int[] array, int start, int end) {
        int value = array[end];
        int index = start - 1;
        for (int i = start; i < end; i++) {
            if (array[i] < value) {
                index++;
                if (index != i) {
                    exchangeElements(array, index, i);
                }
            }
        }
        if ((index + 1) != end) {
            exchangeElements(array, index + 1, end);
        }
        return index + 1;
    }

    private static void exchangeElements(int[] array, int index1, int index2) {
        int temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }

    /**
     * 冒泡排序
     *
     * 冒泡排序是一种简单的排序算法。它重复地走访过要排序的数列，一次比较两个元素，
     * 如果他们的顺序错误就把他们交换过来。走访数列的工作是重复地进行直到没有再需要交换，
     * 也就是说该数列已经排序完成。这个算法的名字由来是因为越小的元素会经由交换慢慢“浮”到数列的顶端。
     */

    /**
     * 比较相邻的元素。如果第一个比第二个大，就交换他们两个。
     * 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。在这一点，最后的元素应该会是最大的数。
     * 针对所有的元素重复以上的步骤，除了最后一个。
     * 持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。
     *
     * @param numbers 需要排序的整型数组
     */
    public static void bubbleSort(int[] numbers) {
        int temp = 0;
        int len = numbers.length;
        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                if (numbers[j] > numbers[j + 1]) {
                    temp = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temp;
                }
            }
        }
    }

    /**
     * 选择排序
     *
     * 在要排序的一组数中，选出最小的一个数与第一个位置的数交换；
     * 然后在剩下的数当中再找最小的与第二个位置的数交换，
     * 如此循环到倒数第二个数和最后一个数比较为止。
     */

    /**
     * 选择排序算法
     * 在未排序序列中找到最小或最大元素，存放到排序序列的起始位置
     * 再从剩余未排序元素中继续寻找最小元素，然后放到排序序列末尾。
     * 以此类推，直到所有元素均排序完毕。
     */
    public static void selectSort(int[] numbers) {
        int len = numbers.length;
        int temp; //中间变量

        for (int i = 0; i < len; i++) {
            //待确定的位置
            int k = i;
            //选择出应该在第i个位置的数
            for (int j = len - 1; j > i; j--) {
                if (numbers[j] < numbers[k]) {
                    k = j;
                }
            }
            //交换两个数
            temp = numbers[i];
            numbers[i] = numbers[k];
            numbers[k] = temp;
        }
    }

    /**
     * 堆排序
     * <p>
     * 堆排序是一种树形选择排序，是对直接选择排序的有效改进。
     * 堆的定义下：具有n个元素的序列 （h1,h2,...,hn),当且仅当满足（hi>=h2i,hi>=2i+1）或（hi<=h2i,hi<=2i+1） (i=1,2,...,n/2)时称之为堆。
     * 在这里只讨论满足前者条件的堆。由堆的定义可以看出，堆顶元素（即第一个元素）必为最大项（大顶堆）。
     * 完全二叉树可以很直观地表示堆的结构。堆顶为根，其它为左子树、右子树。
     * <p>
     * 思想:
     * 初始时把要排序的数的序列看作是一棵顺序存储的二叉树，调整它们的存储序，使之成为一个堆，
     * 这时堆的根节点的数最大。然后将根节点与堆的最后一个节点交换。然后对前面(n-1)个数重新调整使之成为堆。
     * 依此类推，直到只有两个节点的堆，并对 它们作交换，最后得到有n个节点的有序序列。
     * 从算法描述来看，堆排序需要两个过程，一是建立堆，二是堆顶与堆的最后一个元素交换位置。
     * 所以堆排序有两个函数组成。一是建堆的渗透函数，二是反复调用渗透函数实现排序的函数。
     */
    public static void heapSort(int[] a) {
        int len = a.length;
        //循环建堆
        for (int i = 0; i < len - 1; i++) {
            //建堆
            buildMaxHeap(a, len - 1 - i);
            //交换堆顶和最后一个元素
            swap(a, 0, len - 1 - i);
            System.out.println(Arrays.toString(a));
        }
    }

    /**
     * 对data数组从0到lastIndex建大顶堆
     */
    private static void buildMaxHeap(int[] data, int lastIndex) {
        //从lastIndex处节点（最后一个节点）的父节点开始
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            //k保存正在判断的节点
            int k = i;
            //如果当前k节点的子节点存在
            while (k * 2 + 1 <= lastIndex) {
                //k节点的左子节点的索引
                int biggerIndex = 2 * k + 1;
                //如果biggerIndex小于lastIndex，即biggerIndex+1代表的k节点的右子节点存在
                if (biggerIndex < lastIndex) {
                    //若果右子节点的值较大
                    if (data[biggerIndex] < data[biggerIndex + 1]) {
                        //biggerIndex总是记录较大子节点的索引
                        biggerIndex++;
                    }
                }
                //如果k节点的值小于其较大的子节点的值
                if (data[k] < data[biggerIndex]) {
                    //交换他们
                    swap(data, k, biggerIndex);
                    //将biggerIndex赋予k，开始while循环的下一次循环，重新保证k节点的值大于其左右子节点的值
                    k = biggerIndex;
                } else {
                    break;
                }
            }
        }
    }

    private static void swap(int[] data, int i, int j) {
        int tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    /**
     * 桶排序/基数排序
     * <p>
     * 基本思想：是将阵列分到有限数量的桶子里。每个桶子再个别排序（有可能再使用别的排序算法或是以递回方式继续使用桶排序进行排序）。
     * 桶排序是鸽巢排序的一种归纳结果。当要被排序的阵列内的数值是均匀分配的时候，桶排序使用线性时间（Θ（n））。
     * 但桶排序并不是 比较排序，他不受到 O(n log n) 下限的影响。
     * 简单来说，就是把数据分组，放在一个个的桶中，然后对每个桶里面的在进行排序。
     * <p>
     * 例如要对大小为[1..1000]范围内的n个整数A[1..n]排序
     * 首先，可以把桶设为大小为10的范围，具体而言，设集合B[1]存储[1..10]的整数，集合B[2]存储
     * (10..20]的整数，……集合B[i]存储(   (i-1)*10,   i*10]的整数，i   =   1,2,..100。总共有  100个桶。
     * 然后，对A[1..n]从头到尾扫描一遍，把每个A[i]放入对应的桶B[j]中。
     * 再对这100个桶中每个桶里的数字排序，这时可用冒泡，选择，乃至快排，一般来说任  何排序法都可以。
     * 最后，依次输出每个桶里面的数字，且每个桶中的数字从小到大输出，这  样就得到所有数字排好序的一个序列了。
     * 假设有n个数字，有m个桶，如果数字是平均分布的，则每个桶里面平均有n/m个数字。如果
     * 对每个桶中的数字采用快速排序，那么整个算法的复杂度是
     * O(n   +   m   *   n/m*log(n/m))   =   O(n   +   nlogn   -   nlogm)
     * 从上式看出，当m接近n的时候，桶排序复杂度接近O(n)
     * <p>
     * 当然，以上复杂度的计算是基于输入的n个数字是平均分布这个假设的。这个假设是很强的，
     * 实际应用中效果并没有这么好。如果所有的数字都落在同一个桶中，那就退化成一般的排序了。
     * 前面说的几大排序算法 ，大部分时间复杂度都是O（n2），也有部分排序算法时间复杂度是O(nlogn)。
     * 而桶式排序却能实现O（n）的时间复杂度。但桶排序的缺点是：
     * <p>
     * 1）首先是空间复杂度比较高，需要的额外开销大。排序有两个数组的空间开销，一个存放待排序数组，
     * 一个就是所谓的桶，比如待排序值是从0到m-1，那就需要m个桶，这个桶数组就要至少m个空间。
     * 2）其次待排序的元素都要在一定的范围内等等。
     * 桶式排序是一种分配排序。分配排序的特定是不需要进行关键码的比较，但前提是要知道待排序列的一些具体情况。
     */
    public static void radixSort(int[] data, int radix, int d) {
        // 缓存数组
        int[] tmp = new int[data.length];
        // buckets用于记录待排序元素的信息
        // buckets数组定义了max-min个桶
        int[] buckets = new int[radix];
        for (int i = 0, rate = 1; i < d; i++) {
            // 重置count数组，开始统计下一个关键字
            Arrays.fill(buckets, 0);
            // 将data中的元素完全复制到tmp数组中
            System.arraycopy(data, 0, tmp, 0, data.length);
            // 计算每个待排序数据的子关键字
            for (int j = 0; j < data.length; j++) {
                int subKey = (tmp[j] / rate) % radix;
                buckets[subKey]++;
            }
            for (int j = 1; j < radix; j++) {
                buckets[j] = buckets[j] + buckets[j - 1];
            }
            // 按子关键字对指定的数据进行排序
            for (int m = data.length - 1; m >= 0; m--) {
                int subKey = (tmp[m] / rate) % radix;
                data[--buckets[subKey]] = tmp[m];
            }
            rate *= radix;
        }
    }
}
