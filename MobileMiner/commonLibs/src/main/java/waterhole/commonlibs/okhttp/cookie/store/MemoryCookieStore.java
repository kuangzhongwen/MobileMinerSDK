package waterhole.commonlibs.okhttp.cookie.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * 内存型CookieStore
 *
 * @author kzw on 2017/07/31.
 */
public class MemoryCookieStore implements CookieStore {

    private final HashMap<String, List<Cookie>> mAllCookies = new HashMap<>();

    @Override
    public void add(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> oldCookies = mAllCookies.get(url.host());

        if (oldCookies != null) {
            Iterator<Cookie> itNew = cookies.iterator();
            Iterator<Cookie> itOld = oldCookies.iterator();
            while (itNew.hasNext()) {
                String va = itNew.next().name();
                while (va != null && itOld.hasNext()) {
                    String v = itOld.next().name();
                    if (v != null && va.equals(v)) {
                        itOld.remove();
                    }
                }
            }
            oldCookies.addAll(cookies);
        } else {
            mAllCookies.put(url.host(), cookies);
        }
    }

    @Override
    public List<Cookie> get(HttpUrl uri) {
        List<Cookie> cookies = mAllCookies.get(uri.host());
        if (cookies == null) {
            cookies = new ArrayList<>();
            mAllCookies.put(uri.host(), cookies);
        }
        return cookies;

    }

    @Override
    public boolean removeAll() {
        mAllCookies.clear();
        return true;
    }

    @Override
    public List<Cookie> getCookies() {
        List<Cookie> cookies = new ArrayList<>();
        Set<String> httpUrls = mAllCookies.keySet();
        for (String url : httpUrls) {
            cookies.addAll(mAllCookies.get(url));
        }
        return cookies;
    }


    @Override
    public boolean remove(HttpUrl uri, Cookie cookie) {
        List<Cookie> cookies = mAllCookies.get(uri.host());
        if (cookie != null) {
            return cookies.remove(cookie);
        }
        return false;
    }
}
