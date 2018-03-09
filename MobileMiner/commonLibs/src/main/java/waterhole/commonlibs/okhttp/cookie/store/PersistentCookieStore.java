package waterhole.commonlibs.okhttp.cookie.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import waterhole.commonlibs.utils.LogUtils;

/**
 * <pre>
 *     OkHttpClient client = new OkHttpClient.Builder()
 *             .cookieJar(new JavaNetCookieJar(new CookieManager(
 *                     new PersistentCookieStore(getApplicationContext()),
 *                             CookiePolicy.ACCEPT_ALL))
 *             .build();
 *
 * </pre>
 * <p/>
 * from http://stackoverflow.com/questions/25461792/persistent-cookie-store-using-okhttp-2-on-android
 * <p/>
 * <br/>
 * A persistent cookie store which implements the Apache HttpClient CookieStore interface.
 * Cookies are stored and will persist on the user's device between application sessions since they
 * are serialized and stored in SharedPreferences. Instances of this class are
 * designed to be used with AsyncHttpClient#setCookieStore, but can also be used with a
 * regular old apache HttpClient/HttpContext if you prefer.
 *
 * @author kzw on 2017/03/31.
 */
public class PersistentCookieStore implements CookieStore {

    private static final String LOG_TAG = "PersistentCookieStore";
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String COOKIE_NAME_PREFIX = "cookie_";

    private final HashMap<String, ConcurrentHashMap<String, Cookie>> mCookies;
    private final SharedPreferences mCookiePrefs;

    /**
     * Construct a persistent cookie store.
     *
     * @param context Context to attach cookie store to
     */
    public PersistentCookieStore(Context context) {
        mCookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        mCookies = new HashMap<>();

        // Load any previously stored cookies into the store
        Map<String, ?> prefsMap = mCookiePrefs.getAll();
        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            if (entry.getValue() != null && !((String) entry.getValue()).startsWith(COOKIE_NAME_PREFIX)) {
                String[] cookieNames = TextUtils.split((String) entry.getValue(), ",");
                for (String name : cookieNames) {
                    String encodedCookie = mCookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                    if (encodedCookie != null) {
                        Cookie decodedCookie = decodeCookie(encodedCookie);
                        if (decodedCookie != null) {
                            if (!mCookies.containsKey(entry.getKey())) {
                                mCookies.put(entry.getKey(), new ConcurrentHashMap<String, Cookie>());
                            }
                            mCookies.get(entry.getKey()).put(name, decodedCookie);
                        }
                    }
                }
            }
        }
    }

    protected void add(HttpUrl uri, Cookie cookie) {
        String name = getCookieToken(cookie);
        if (cookie.persistent()) {
            if (!mCookies.containsKey(uri.host())) {
                mCookies.put(uri.host(), new ConcurrentHashMap<String, Cookie>());
            }
            mCookies.get(uri.host()).put(name, cookie);
        } else {
            if (mCookies.containsKey(uri.host())) {
                mCookies.get(uri.host()).remove(name);
            } else {
                return;
            }
        }

        // Save cookie into persistent store
        SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
        prefsWriter.putString(uri.host(), TextUtils.join(",", mCookies.get(uri.host()).keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableHttpCookie(cookie)));
        prefsWriter.apply();
    }

    protected String getCookieToken(Cookie cookie) {
        return cookie.name() + cookie.domain();
    }

    @Override
    public void add(HttpUrl uri, List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            add(uri, cookie);
        }
    }

    @Override
    public List<Cookie> get(HttpUrl uri) {
        ArrayList<Cookie> ret = new ArrayList<>();
        if (mCookies.containsKey(uri.host())) {
            Collection<Cookie> cookies = mCookies.get(uri.host()).values();
            for (Cookie cookie : cookies) {
                if (isCookieExpired(cookie)) {
                    remove(uri, cookie);
                } else {
                    ret.add(cookie);
                }
            }
        }
        return ret;
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    @Override
    public boolean removeAll() {
        SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        mCookies.clear();
        return true;
    }

    @Override
    public boolean remove(HttpUrl uri, Cookie cookie) {
        String name = getCookieToken(cookie);

        if (mCookies.containsKey(uri.host()) && mCookies.get(uri.host()).containsKey(name)) {
            mCookies.get(uri.host()).remove(name);

            SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
            if (mCookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + name);
            }
            prefsWriter.putString(uri.host(), TextUtils.join(",", mCookies.get(uri.host()).keySet()));
            prefsWriter.apply();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Cookie> getCookies() {
        ArrayList<Cookie> ret = new ArrayList<>();
        for (String key : mCookies.keySet()) {
            ret.addAll(mCookies.get(key).values());
        }
        return ret;
    }

    protected String encodeCookie(SerializableHttpCookie cookie) {
        if (cookie == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e);
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    protected Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableHttpCookie) objectInputStream.readObject()).getCookie();
        } catch (IOException e) {
            LogUtils.error(LOG_TAG, "IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            LogUtils.error(LOG_TAG, "ClassNotFoundException in decodeCookie", e);
        }

        return cookie;
    }

    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't have to rely on any
     * large Base64 libraries. Can be overridden if you like!
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    /**
     * Converts hex values from strings to byte arra
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
                    .digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
