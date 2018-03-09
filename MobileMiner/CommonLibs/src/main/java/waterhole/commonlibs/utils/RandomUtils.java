package waterhole.commonlibs.utils;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

/**
 * Random Utils
 *
 * @author kzw on 2017/3/18.
 */
public final class RandomUtils {

    // Java default random instance.
    private static Random r = new Random();

    // Alphabetic letters.
    private static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            .toCharArray();

    // Numeric chars.
    private static char[] NUMERIC = "0123456789".toCharArray();

    public RandomUtils() {
        throw new RuntimeException("RandomUtils stub!");
    }

    /**
     * Generating random permutation
     *
     * @param array Input sorted array
     * @return A random ordering of input array elements
     */
    public static <T> T[] randomPermutation(final T[] array) {
        if (ArraysUtils.isEmpty(array)) {
            return null;
        }
        List<T[]> list = PermutationUtils.arrange(array);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(randomInt(list.size()));
    }

    /**
     * An integer that is randomly generated in the range of [0, n)
     */
    public static int randomInt(int n) {
        return (new Random()).nextInt(n);
    }

    /**
     * Sets this @KRandom to use secure random.
     * Initialize a new @SecureRandom instance.
     */
    public static void setUseSecure() {
        r = new SecureRandom();
    }

    /**
     * Sets this @KRandom to use default random.
     * Initialize a new @Random instance.
     */
    public static void setUseNotSecure() {
        r = new Random();
    }

    /**
     * @return random int.
     */
    public static int nextInt() {
        return r.nextInt();
    }

    /**
     * @return random int.
     */
    public static int nextPositiveInt() {
        return Math.abs(r.nextInt());
    }

    /**
     * Generates a random int in range [start, end].
     *
     * @param start left bound for the random int.
     * @param end   right bound for the random int.
     * @return random int in range [start, end]
     */
    public static int nextInt(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("right bound must be bigger then left bound.");
        }
        return (nextPositiveInt() % (end - start)) + start;
    }

    /**
     * @return even random int.
     */
    public static int nextEvenInt() {
        int rValue = nextInt();
        if (rValue % 2 != 0) {
            return rValue + 1;
        }
        return rValue;
    }

    /**
     * @return odd random int.
     */
    public static int nextOddInt() {
        return nextEvenInt() + 1;
    }

    /**
     * Generates an even random int in range [start, end].
     *
     * @param start left bound for the even random int.
     * @param end   right bound for the even random int.
     * @return even random int in range [start, end]
     */
    public static int nextEvenInt(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("right bound must be bigger then left bound.");
        }
        if (end == start && start % 2 != 0) {
            throw new IllegalArgumentException("can't find even number in those bounds.");
        }

        int rValue = nextInt(start, end);
        if (rValue % 2 != 0) {
            if (rValue < end) {
                return rValue + 1;
            } else if (rValue > start) {
                return rValue - 1;
            }
        }
        return rValue;
    }

    /**
     * Generates an odd random int in range [start, end].
     *
     * @param start left bound for the odd random int.
     * @param end   right bound for the odd random int.
     * @return odd random int in range [start, end]
     */
    public static int nextOddInt(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("right bound must be bigger then left bound.");
        }
        if (end == start && start % 2 != 0) {
            throw new IllegalArgumentException("can't find even number in those bounds.");
        }
        int rValue = nextInt(start, end);
        if (rValue % 2 == 0) {
            if (rValue < end) {
                return rValue + 1;
            } else if (rValue > start) {
                return rValue - 1;
            }
        }

        return rValue;
    }

    /**
     * Generates random @int array.
     *
     * @param size requested array size.
     * @return int array of size @size filled with random numbers.
     */
    public static int[] nextIntArray(int size) {
        int[] randomArray = new int[size];

        for (int i = 0; i < randomArray.length; i++) {
            randomArray[i] = r.nextInt(10);
        }

        return randomArray;
    }

    /**
     * Generates random @long array.
     *
     * @param size requested array size.
     * @return long array of size @size filled with random numbers.
     */
    public static long[] nextLongArray(int size) {
        long[] randomArray = new long[size];

        for (int i = 0; i < randomArray.length; i++) {
            randomArray[i] = r.nextLong();
        }

        return randomArray;
    }

    /**
     * Generates random @float array.
     *
     * @param size requested array size.
     * @return float array of size @size filled with random numbers.
     */
    public static float[] nextFloatArray(int size) {
        float[] randomArray = new float[size];

        for (int i = 0; i < randomArray.length; i++) {
            randomArray[i] = r.nextFloat();
        }

        return randomArray;
    }

    /**
     * Generates random @double array.
     *
     * @param size requested array size.
     * @return double array of size @size filled with random numbers.
     */
    public static double[] nextDoubleArray(int size) {
        double[] randomArray = new double[size];

        for (int i = 0; i < randomArray.length; i++) {
            randomArray[i] = r.nextDouble();
        }

        return randomArray;
    }

    /**
     * Generates random @boolean array.
     *
     * @param size requested array size.
     * @return int array of size @size filled with random numbers.
     */
    public static boolean[] nextBooleanArray(int size) {
        return nextBooleanArray(size, 0.5);
    }

    /**
     * Generates random boolean array with defined probability to get @true and @false.
     *
     * @param size            requested array size.
     * @param onesProbability the probability to get 'true' or '1' in the returned array.
     * @return boolean array of size @size with @onesProbability to get @true (or '1')
     * and (1 - @onesProbability) to get @false (or '0').
     */
    public static boolean[] nextBooleanArray(int size, double onesProbability) {
        if (onesProbability < 0 || onesProbability > 1) {
            throw new IllegalArgumentException("probability needs to be in range: [0,1]");
        }
        boolean[] randomArray = new boolean[size];

        for (int i = 0; i < randomArray.length; i++) {
            randomArray[i] = (r.nextDouble() <= onesProbability);
        }

        return randomArray;
    }

    /**
     * Generates random 2D @int array of size @rows*@cols
     *
     * @param rows number of rows.
     * @param cols number of cols.
     * @return random 2D array of size @rows*@cols filled with random numbers.
     */
    public static int[][] next2DIntArray(int rows, int cols) {
        int[][] randomArray = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            randomArray[i] = nextIntArray(cols);
        }

        return randomArray;
    }

    /**
     * Generates random 2D @long array of size @rows*@cols
     *
     * @param rows number of rows.
     * @param cols number of cols.
     * @return random 2D array of size @rows*@cols filled with random numbers.
     */
    public static long[][] next2DLongArray(int rows, int cols) {
        long[][] randomArray = new long[rows][cols];

        for (int i = 0; i < rows; i++) {
            randomArray[i] = nextLongArray(cols);
        }

        return randomArray;
    }

    /**
     * Generates random 2D @float array of size @rows*@cols
     *
     * @param rows number of rows.
     * @param cols number of cols.
     * @return random 2D array of size @rows*@cols filled with random numbers.
     */
    public static float[][] next2DFloatArray(int rows, int cols) {
        float[][] randomArray = new float[rows][cols];

        for (int i = 0; i < rows; i++) {
            randomArray[i] = nextFloatArray(cols);
        }

        return randomArray;
    }

    /**
     * Generates random 2D @double array of size @rows*@cols
     *
     * @param rows number of rows.
     * @param cols number of cols.
     * @return random 2D array of size @rows*@cols filled with random numbers.
     */
    public static double[][] next2DDoubleArray(int rows, int cols) {
        double[][] randomArray = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            randomArray[i] = nextDoubleArray(cols);
        }

        return randomArray;
    }

    /**
     * Generates random 2D @boolean array of size @rows*@cols
     *
     * @param rows number of rows.
     * @param cols number of cols.
     * @return random 2D array of size @rows*@cols filled with random numbers.
     */
    public static boolean[][] next2DBooleanArray(int rows, int cols) {
        return next2DBooleanArray(rows, cols, 0.5);
    }

    /**
     * Generates random 2D @boolean array of size @rows*@cols
     *
     * @param rows            number of rows.
     * @param cols            number of cols.
     * @param onesProbability the probability to get @true or '1' in the returned 2D array.
     * @return random 2D array of size @rows*@cols with @onesProbability to get @true (or '1')
     * and (1 - @onesProbability) to get @false (or '0').
     */
    public static boolean[][] next2DBooleanArray(int rows, int cols, double onesProbability) {
        boolean[][] randomArray = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            randomArray[i] = nextBooleanArray(cols, onesProbability);
        }

        return randomArray;
    }

    /**
     * Generates a random @String of length @len from visible characters
     * according to the ASCII table: [32,126].
     *
     * @param len requested string length.
     * @return random @String of length @len.
     */
    public static String nextString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append((char) nextInt(32, 126));
        }

        return sb.toString();
    }

    /**
     * Generates a random @String of length @len from character set:
     * 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
     *
     * @param len requested string length.
     * @return random alphabetic @String of length @len.
     */
    public static String nextAlphabeticString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(ALPHABET[nextInt(0, ALPHABET.length)]);
        }

        return sb.toString();
    }

    /**
     * Generates a random @String of length @len from character set: '0123456789'.
     *
     * @param len requested string length.
     * @return random numeric @String of length @len.
     */
    public static String nextNumericString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(NUMERIC[nextInt(0, NUMERIC.length)]);
        }

        return sb.toString();
    }
}
