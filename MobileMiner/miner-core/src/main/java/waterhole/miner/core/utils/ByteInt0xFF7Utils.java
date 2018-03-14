package waterhole.miner.core.utils;

/**
 * Byte Int互转工具类
 *
 * @author kzw on 2017/07/06.
 */
public final class ByteInt0xFF7Utils {

    public ByteInt0xFF7Utils() {
        throw new RuntimeException("ByteInt0xFF7Utils stub!");
    }

    /**
     * <p>
     * java中，byte转为int为何要&0xff:
     * <p>
     * 第一，0xff默认为整形，二进制位最低8位是11111111，前面24位都是0
     * 第二，&运算: 如果2个bit都是1，则得1，否则得0；
     * 第三，byte的8位和0xff进行&运算后，最低8位中，原来为1的还是1，
     * 原来为0的还是0，而0xff其他位都是0，所以&后仍然得0.
     * <p>
     * 1.byte的大小为8bits而int的大小为32bits
     * 2.java的二进制采用的是补码形式
     * <p>
     * Integer.toHexString的参数是int，如果不进行&0xff，那么当一个byte会转换成int时，
     * 由于int是32位，而byte只有8位这时会进行补位，例如补码11111111的十进制数为-1转换为int
     * 时变为11111111111111111111111111111111好多1啊，呵呵！即0xffffffff但是这个数是不对的，
     * 这种补位就会造成误差。和0xff相与后，高24比特就会被清0了，结果就对了。
     */
    public static String bytes2HexString(byte[] bytes) {
        String ret = "";
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /**
     * 整型转换为4位字节数组
     */
    public static byte[] int2Byte(int intValue) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
        }
        return b;
    }

    /**
     * 4位字节数组转换为整型
     */
    public static int byte2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }
}
