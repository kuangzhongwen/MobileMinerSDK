package waterhole.commonlibs.net.okhttp.builder;

import waterhole.commonlibs.net.okhttp.OkHttpUtils;
import waterhole.commonlibs.net.okhttp.request.OtherRequest;
import waterhole.commonlibs.net.okhttp.request.RequestCall;

/**
 * Head建造者
 *
 * @author kzw on 2017/07/31.
 */
public class HeadBuilder extends GetBuilder {

    @Override
    public RequestCall build() {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, mUrl, mTag, mParams, mHeaders, mID).build();
    }
}
