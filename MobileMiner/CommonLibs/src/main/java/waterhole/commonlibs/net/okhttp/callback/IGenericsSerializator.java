package waterhole.commonlibs.net.okhttp.callback;

/**
 * Generics转换接口
 *
 * @author kzw on 2017/07/31.
 */
public interface IGenericsSerializator {

    <T> T transform(String response, Class<T> classOfT);
}
