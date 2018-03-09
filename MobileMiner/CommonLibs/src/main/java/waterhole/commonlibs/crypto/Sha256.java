package waterhole.commonlibs.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 哈希值用作表示大量数据的固定大小的唯一值。数据的少量更改会在哈希值中产生不可预知的大量更改。
 * SHA256 算法的哈希值大小为 256 位。
 *
 * @author kzw on 2017/07/22.
 */
public final class Sha256 {

    public Sha256() {
        throw new AssertionError();
    }

    public static byte[] Sha256(byte[] data, int start, int len, int recursion) {
        if (recursion == 0) return data;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Arrays.copyOfRange(data, start, start + len));
            return Sha256(md.digest(), 0, 32, recursion - 1);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
