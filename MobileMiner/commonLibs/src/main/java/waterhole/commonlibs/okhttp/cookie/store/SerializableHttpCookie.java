package waterhole.commonlibs.okhttp.cookie.store;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import okhttp3.Cookie;

/**
 * from http://stackoverflow.com/questions/25461792/persistent-cookie-store-using-okhttp-2-on-android
 * and<br/>
 * http://www.geebr.com/post/okHttp3%E4%B9%8BCookies%E7%AE%A1%E7%90%86%E5%8F%8A%E6%8C%81%E4%B9%85%E5%8C%96
 *
 * @author kzw on 2017/03/31.
 */
public class SerializableHttpCookie implements Serializable {

    private static final long serialVersionUID = 6374381323722046732L;

    private transient final Cookie mCookie;
    private transient Cookie mClientCookie;

    public SerializableHttpCookie(Cookie cookie) {
        mCookie = cookie;
    }

    public Cookie getCookie() {
        Cookie bestCookie = mCookie;
        if (mClientCookie != null) {
            bestCookie = mClientCookie;
        }
        return bestCookie;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(mCookie.name());
        out.writeObject(mCookie.value());
        out.writeLong(mCookie.expiresAt());
        out.writeObject(mCookie.domain());
        out.writeObject(mCookie.path());
        out.writeBoolean(mCookie.secure());
        out.writeBoolean(mCookie.httpOnly());
        out.writeBoolean(mCookie.hostOnly());
        out.writeBoolean(mCookie.persistent());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        long expiresAt = in.readLong();
        String domain = (String) in.readObject();
        String path = (String) in.readObject();
        boolean secure = in.readBoolean();
        boolean httpOnly = in.readBoolean();
        boolean hostOnly = in.readBoolean();
        boolean persistent = in.readBoolean();
        Cookie.Builder builder = new Cookie.Builder();
        builder = builder.name(name);
        builder = builder.value(value);
        builder = builder.expiresAt(expiresAt);
        builder = hostOnly ? builder.hostOnlyDomain(domain) : builder.domain(domain);
        builder = builder.path(path);
        builder = secure ? builder.secure() : builder;
        builder = httpOnly ? builder.httpOnly() : builder;
        mClientCookie = builder.build();
    }
}