package waterhole.commonlibs.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Static functions to simplifiy common {@link MessageDigest}
 * tasks. This class is thread safe.
 */
public class MD5 {

    private static char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private MD5() {
    }

    public static String toMD5(String s) {
        if (s != null) {
            try {
                byte[] bs = s.getBytes("UTF-8");
                return encrypt(bs);
            } catch (UnsupportedEncodingException e) {
                // do nothing
            }
        }
        return null;
    }

    private synchronized static String encrypt(byte[] obj) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(obj);
            byte[] bs = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bs.length; i++) {
                sb.append(Integer
                        .toHexString((0x000000ff & bs[i]) | 0xffffff00)
                        .substring(6));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    /**
     * Returns a MessageDigest for the given <code>algorithm</code>.
     * <p>
     * The MessageDigest algorithm name.
     *
     * @return An MD5 digest instance.
     * @throws RuntimeException when a {@link NoSuchAlgorithmException} is
     *                          caught
     */

    static MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculates the MD5 digest and returns the value as a 16 element
     * <code>byte[]</code>.
     *
     * @param data Data to digest
     * @return MD5 digest
     */
    public static byte[] md5(byte[] data) {
        return getDigest().digest(data);
    }

    /**
     * Calculates the MD5 digest and returns the value as a 16 element
     * <code>byte[]</code>.
     *
     * @param data Data to digest
     * @return MD5 digest
     */
    public static byte[] md5(String data) {
        return md5(data.getBytes());
    }

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex
     * string.
     *
     * @param data Data to digest
     * @return MD5 digest as a hex string
     */
    public static String md5Hex(byte[] data) {
        return bytesToHexString(md5(data));
    }

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex
     * string.
     *
     * @param data Data to digest
     * @return MD5 digest as a hex string
     */
    public static String md5Hex(String data) {
        return bytesToHexString(md5(data));
    }

    /**
     * Convert a byte array to a hex-encoding string: "a33bff00..."
     */
    private static String bytesToHexString(final byte[] bytes) {
        return bytesToHexString(bytes, null);
    }

    /**
     * Convert a byte array to a hex-encoding string with the specified
     * delimiter: "a3&lt;delimiter&gt;3b&lt;delimiter&gt;ff..."
     */
    private static String bytesToHexString(final byte[] bytes,
                                           Character delimiter) {
        StringBuffer hex = new StringBuffer(bytes.length * (delimiter == null ? 2 : 3));
        int nibble1, nibble2;
        for (int i = 0; i < bytes.length; i++) {
            nibble1 = (bytes[i] >>> 4) & 0xf;
            nibble2 = bytes[i] & 0xf;
            if (i > 0 && delimiter != null) hex.append(delimiter.charValue());
            hex.append(hexChars[nibble1]);
            hex.append(hexChars[nibble2]);
        }
        return hex.toString();
    }
}
