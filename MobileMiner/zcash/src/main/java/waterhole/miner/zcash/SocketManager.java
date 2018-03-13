package waterhole.miner.zcash;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import waterhole.commonlibs.annotation.ExcuteOnAsyn;

import static waterhole.commonlibs.utils.LogUtils.error;
import static waterhole.commonlibs.utils.LogUtils.info;
import static waterhole.commonlibs.utils.Preconditions.checkOnChildThread;
import static waterhole.commonlibs.utils.IOUtils.closeSafely;

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
        if (isConnected()) {
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
            socket = null;
        }
    }

    void sendMessage(String message) {
        if (!TextUtils.isEmpty(message) && isConnected()) {
            OutputStream os = null;
            PrintWriter pw = null;
            InputStream is = null;
            BufferedReader br = null;
            try {
                os = socket.getOutputStream();
                pw = new PrintWriter(os);
                pw.write(message);
                pw.flush();
                socket.shutdownOutput();

                is = socket.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String info;
                while ((info = br.readLine()) != null) {
                    info(TAG, "server-> " + info);
                }
            } catch (IOException e) {
                error(TAG, e.getMessage());
            } finally {
                closeSafely(os);
                closeSafely(pw);
                closeSafely(is);
                closeSafely(br);
            }
        }
    }

    private boolean isConnected() {
        return socket != null && socket.isConnected();
    }
}
