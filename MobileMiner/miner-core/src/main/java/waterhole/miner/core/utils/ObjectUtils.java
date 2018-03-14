package waterhole.miner.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Object相关工具封装
 *
 * @author kzw on 17/03/19.
 */
public final class ObjectUtils {

    private ObjectUtils() {
    }

    public static String getAsString(final Object obj) {
        return obj != null ? obj.toString() : "";
    }

    public static Long getAsLong(final Object obj) {
        if (obj == null) {
            return 0L;
        }
        try {
            return ((Number) obj).longValue();
        } catch (ClassCastException e) {
            if (obj instanceof CharSequence) {
                try {
                    return Long.valueOf(obj.toString());
                } catch (NumberFormatException e2) {
                    return 0L;
                }
            } else {
                return 0L;
            }
        }
    }

    public static Integer getAsInteger(final Object obj) {
        if (obj == null) {
            return 0;
        }
        try {
            return ((Number) obj).intValue();
        } catch (ClassCastException e) {
            if (obj instanceof CharSequence) {
                try {
                    return Integer.valueOf(obj.toString());
                } catch (NumberFormatException e2) {
                    return 0;
                }
            } else {
                return 0;
            }
        }
    }

    public static Short getAsShort(final Object obj) {
        if (obj == null) {
            return 0;
        }
        try {
            return ((Number) obj).shortValue();
        } catch (ClassCastException e) {
            if (obj instanceof CharSequence) {
                try {
                    return Short.valueOf(obj.toString());
                } catch (NumberFormatException e2) {
                    return 0;
                }
            } else {
                return 0;
            }
        }
    }

    public static Byte getAsByte(final Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return ((Number) obj).byteValue();
        } catch (ClassCastException e) {
            if (obj instanceof CharSequence) {
                try {
                    return Byte.valueOf(obj.toString());
                } catch (NumberFormatException e2) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public static Double getAsDouble(final Object obj) {
        if (obj == null) {
            return 0.0d;
        }
        try {
            return ((Number) obj).doubleValue();
        } catch (ClassCastException e) {
            if (obj instanceof CharSequence) {
                try {
                    return Double.valueOf(obj.toString());
                } catch (NumberFormatException e2) {
                    return 0.0d;
                }
            } else {
                return 0.0d;
            }
        }
    }

    public static Float getAsFloat(final Object obj) {
        if (obj == null) {
            return 0.0f;
        }
        try {
            return ((Number) obj).floatValue();
        } catch (ClassCastException e) {
            if (obj instanceof CharSequence) {
                try {
                    return Float.valueOf(obj.toString());
                } catch (NumberFormatException e2) {
                    return 0.0f;
                }
            } else {
                return 0.0f;
            }
        }
    }

    public static Boolean getAsBoolean(final Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            return (Boolean) obj;
        } catch (ClassCastException e) {
            if (obj instanceof CharSequence) {
                return Boolean.valueOf(obj.toString());
            } else if (obj instanceof Number) {
                return ((Number) obj).intValue() != 0;
            } else {
                return false;
            }
        }
    }

    public static byte[] getAsByteArray(final Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return (byte[]) obj;
        } else {
            return null;
        }
    }

    /**
     * 深度克隆一个对象
     */
    public static Object deepClone(Object src) {
        Object o = null;
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;

        try {
            if (src != null) {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(src);
                bais = new ByteArrayInputStream(baos.toByteArray());
                ois = new ObjectInputStream(bais);
                o = ois.readObject();
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        } finally {
            IOUtils.closeSafely(baos);
            IOUtils.closeSafely(oos);
            IOUtils.closeSafely(bais);
            IOUtils.closeSafely(ois);
        }
        return o;
    }

    /**
     * compare two object
     *
     * @return <ul>
     * <li>if both are null, return true</li>
     * <li>return actual.{@link Object#equals(Object)}</li>
     * </ul>
     */
    public static boolean isEquals(Object actual, Object expected) {
        return actual == expected || (actual == null ? expected == null : actual.equals(expected));
    }
}
