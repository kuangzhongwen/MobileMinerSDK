package waterhole.miner.core;

/**
 * Gpu挖矿回调，供接入方使用.
 *
 * @author kzw on 2018/03/14.
 */
public interface GPUMinerCallback<T> extends CommonMinerCallback<T> {

    /**
     * 初始化openCL成功.
     */
    void onLoadOpenCLSuccess();

    /**
     * 初始化openCL失败，比如不支持openCL，部分手机不带openCL功能，比如google官方手机，
     * 推崇renderScript，可以引导用户去按照手机官方说明去下载驱动，如果可以的话. 或者动态链接cl so库失败.
     *
     * @param reason 失败原因
     */
    void onLoadOpenCLFail(String reason);
}
