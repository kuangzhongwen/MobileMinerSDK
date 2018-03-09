package waterhole.commonlibs.preferences;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.nio.charset.Charset;

import waterhole.commonlibs.crypto.Base64;
import waterhole.commonlibs.crypto.MD5;

/**
 * 抽象加密Preference，key(md5), value(base64)，子类可以重写加解密
 *
 * @author kzw on 2017/03/20.
 */
public abstract class BaseEncryPreference extends BasePreference {

    protected static final String TAG = "BaseEncryPreference";

    // 默认字符编码
    protected static final Charset CHARSET = Charset.forName("utf-8");

    @Override
    protected void saveValue(Context context, String key, String value) {
        SharedPreferencesUtils.setString(context, getTableName(), encryKey(key), encryValue(value));
    }

    @Override
    protected String getValue(Context context, String key) {
        String encryKey = encryKey(key);
        String encryptValue = SharedPreferencesUtils.getString(context, getTableName(), encryKey, "");
        if (TextUtils.isEmpty(encryptValue)) {
            return "";
        }
        return decryptValue(encryptValue);
    }

    @Override
    protected void removeValue(Context context, String key) {
        SharedPreferencesUtils.removeValue(context, getTableName(), encryKey(key));
    }

    @NonNull
    protected String encryKey(String key) {
        return MD5.toMD5(key);
    }

    @NonNull
    protected String encryValue(String value) {
        return new String(Base64.encode(value.getBytes(), Base64.DEFAULT), CHARSET);
    }

    @NonNull
    protected String decryptValue(String encrypt) {
        return new String(Base64.decode(encrypt.getBytes(), Base64.DEFAULT), CHARSET);
    }
}
