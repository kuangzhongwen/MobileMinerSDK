package waterhole.miner.zcash;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.net.Socket;

import waterhole.commonlibs.annotation.ExcuteOnAsyn;

import static waterhole.commonlibs.utils.LogUtils.error;
import static waterhole.commonlibs.utils.LogUtils.info;
import static waterhole.commonlibs.utils.Preconditions.checkOnChildThread;

/**
 * Zcash socket管理类，主要功能为连接矿池，绑定地址，提交share数据到矿池.
 *
 * @author kzw on 2018/03/12.
 */
final class SocketManager {

    private static final String TAG = "SocketManager";

    // 默认地址
    private static final String DEFAULT_HOST = "us1-zcash.flypool.org";
    // 默认端口
    private static final int DEFAULT_PORT = 3333;

    private Socket socket = null;

    private SocketManager() {
    }

    static SocketManager instance() {
        return Holder.instance;
    }

    private static class Holder {
        static SocketManager instance = new SocketManager();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    @ExcuteOnAsyn
    void connect() {
        checkOnChildThread();
        if (socket != null && socket.isConnected()) {
            return;
        }
        try {
            info(TAG, "begin connect ->");
            socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
            info(TAG, "end connect -> is connect = " + socket.isConnected());
        } catch (IOException e) {
            error(TAG, e.getMessage());
        }
    }

    @ExcuteOnAsyn
    void disconnect() {
        checkOnChildThread();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                error(TAG, e.getMessage());
            }
        }
    }
}
