package waterhole.miner.core;

/**
 * 错误码表.
 *
 * @author kzw on 2018/03/14.
 */
public final class ErrorCode implements NoProGuard {

    private ErrorCode() {
        throw new RuntimeException("ErrorCode stub!");
    }

    // 未知错误
    public static final int UNKNOW = -1;
    // 不支持openCL
    public static final int UNSUPPORT_OPENCL = 1;
    // 动态链接openCL库失败
    public static final int LOAD_OPENCL_SO_FAIL = 2;
    // 网络异常
    public static final int NET_ERROR = 3;
    // 握手失败
    public static final int HANDLE_SSL_FAIL = 4;
    // 程序中断
    public static final int RUN_INTERRUPT = 5;
}
