package waterhole.commonlibs.net.okhttp.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import waterhole.commonlibs.net.okhttp.cookie.store.CookieStore;

/**
 * CookieJar的实现
 *
 * @author kzw on 2017/07/31.
 */
public class CookieJarImpl implements CookieJar {

    private CookieStore mCookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null.");
        }
        mCookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        mCookieStore.add(url, cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        return mCookieStore.get(url);
    }

    public CookieStore getCookieStore() {
        return mCookieStore;
    }
}
