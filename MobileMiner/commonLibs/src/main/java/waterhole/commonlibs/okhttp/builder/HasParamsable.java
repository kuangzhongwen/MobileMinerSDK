package waterhole.commonlibs.okhttp.builder;

import java.util.Map;

/**
 * 请求参数接口
 *
 * @author kzw on 2017/07/31.
 */
public interface HasParamsable {

    OkHttpRequestBuilder params(Map<String, String> params);

    OkHttpRequestBuilder addParams(String key, String val);
}
