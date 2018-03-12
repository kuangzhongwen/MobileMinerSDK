package waterhole.miner.zcash;

import java.io.ObjectStreamException;

/**
 * Zcash socket管理类.
 *
 * @author kzw on 2018/03/12.
 */
final class SocketManager {

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
}
