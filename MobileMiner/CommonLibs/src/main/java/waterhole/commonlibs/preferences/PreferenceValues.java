package waterhole.commonlibs.preferences;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import waterhole.commonlibs.utils.ObjectUtils;

/**
 * 存入SharedPerences的数据对象
 *
 * @author kzw on 2017/03/19.
 */
public final class PreferenceValues implements Parcelable {

    // 存储待放入SharedPerences中的对象
    private final HashMap<String, Object> mValues;

    public PreferenceValues() {
        mValues = new HashMap<>(8);
    }

    public PreferenceValues(int size) {
        // 容量利用率为1.0f，但是碰撞机率会增加
        mValues = new HashMap<>(size, 1.0f);
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    public PreferenceValues(final Parcel in) {
        mValues = in.readHashMap(null);
    }

    private PreferenceValues(final HashMap<String, Object> values) {
        mValues = values;
    }

    public void put(String key, String value) {
        mValues.put(key, value);
    }

    public void putAll(PreferenceValues other) {
        mValues.putAll(other.mValues);
    }

    public void put(String key, Byte value) {
        mValues.put(key, value);
    }

    public void put(String key, Short value) {
        mValues.put(key, value);
    }

    public void put(String key, Integer value) {
        mValues.put(key, value);
    }

    public void put(String key, Long value) {
        mValues.put(key, value);
    }

    public void put(String key, Float value) {
        mValues.put(key, value);
    }

    public void put(String key, Double value) {
        mValues.put(key, value);
    }

    public void put(String key, Boolean value) {
        mValues.put(key, value);
    }

    public void put(String key, byte[] value) {
        mValues.put(key, value);
    }

    public void putNull(String key) {
        mValues.put(key, null);
    }

    public int size() {
        return mValues.size();
    }

    public void remove(String key) {
        mValues.remove(key);
    }

    public void clear() {
        mValues.clear();
    }

    public boolean containsKey(String key) {
        return mValues.containsKey(key);
    }

    public Object get(String key) {
        return mValues.get(key);
    }

    public String getAsString(String key) {
        return ObjectUtils.getAsString(mValues.get(key));
    }

    public Long getAsLong(String key) {
        return ObjectUtils.getAsLong(mValues.get(key));
    }

    public Integer getAsInteger(String key) {
        return ObjectUtils.getAsInteger(mValues.get(key));
    }

    public Short getAsShort(String key) {
        return ObjectUtils.getAsShort(mValues.get(key));
    }

    public Byte getAsByte(String key) {
        return ObjectUtils.getAsByte(mValues.get(key));
    }

    public Double getAsDouble(String key) {
        return ObjectUtils.getAsDouble(mValues.get(key));
    }

    public Float getAsFloat(String key) {
        return ObjectUtils.getAsFloat(mValues.get(key));
    }

    public Boolean getAsBoolean(String key) {
        return ObjectUtils.getAsBoolean(mValues.get(key));
    }

    public byte[] getAsByteArray(String key) {
        return ObjectUtils.getAsByteArray(mValues.get(key));
    }

    /**
     * Returns a set of all of the keys and values
     *
     * @return a set of all of the keys and values
     */
    public Set<Map.Entry<String, Object>> valueSet() {
        return mValues.entrySet();
    }

    /**
     * Returns a set of all of the keys
     *
     * @return a set of all of the keys
     */
    public Set<String> keySet() {
        return mValues.keySet();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof PreferenceValues && mValues.equals(((PreferenceValues) object).mValues);
    }

    @Override
    public int hashCode() {
        return mValues.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String name : mValues.keySet()) {
            String value = getAsString(name);
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(name).append("=").append(value);
        }
        return sb.toString();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeMap(mValues);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PreferenceValues> CREATOR = new Creator<PreferenceValues>() {
        public PreferenceValues createFromParcel(final Parcel in) {
            return new PreferenceValues(in);
        }

        public PreferenceValues[] newArray(int size) {
            return new PreferenceValues[size];
        }
    };
}
