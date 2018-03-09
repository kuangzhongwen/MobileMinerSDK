package waterhole.commonlibs.crypto;

/**
 * Base58是用于Bitcoin中使用的一种独特的编码方式，主要用于产生Bitcoin的钱包地址.
 *
 * @author kzw on 2017/07/22.
 */
public final class Base58 {

    private final static String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    public Base58() {
        throw new AssertionError();
    }

    public static byte[] DecodeBase58(String input, int base, int len) {
        byte[] output = new byte[len];
        for (int i = 0; i < input.length(); i++) {
            char t = input.charAt(i);

            int p = ALPHABET.indexOf(t);
            if (p == -1) return null;
            for (int j = len - 1; j > 0; j--, p /= 256) {
                p += base * (output[j] & 0xFF);
                output[j] = (byte) (p % 256);
            }
            if (p != 0) return null;
        }
        return output;
    }
}
