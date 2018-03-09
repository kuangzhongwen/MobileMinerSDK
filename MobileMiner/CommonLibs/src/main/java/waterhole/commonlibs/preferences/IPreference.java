package waterhole.commonlibs.preferences;

import android.content.Context;

/**
 * MTB cache preference interface
 *
 * @author kzw 2017/2/15.
 */
interface IPreference<K, V> {

    void save(Context context, PreferenceValues values);

    V get(Context context, K k);

    void remove(Context context, K key);

    void clear(Context context);
}