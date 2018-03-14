package waterhole.miner.monero;

import android.content.Context;
import android.os.Looper;

import java.io.ObjectStreamException;
import java.util.concurrent.atomic.AtomicBoolean;

import waterhole.miner.core.ContextWrapper;
import waterhole.miner.core.MineCallback;
import waterhole.miner.core.CommonMinerIterface;

public final class MoneroMiner implements CommonMinerIterface {

    static {
        // load library
    }

    // 上下文对象
    private final Context mContext = ContextWrapper.getInstance().obtainContext();

    // 挖矿回调
    private MineCallback mMineCallback;

    // 句柄
    private final android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper());

    // 避免重复启动
    private AtomicBoolean isRunningMine = new AtomicBoolean(false);

    private MoneroMiner() {
    }

    public static MoneroMiner instance() {
        return Holder.instance;
    }

    private static class Holder {
        static MoneroMiner instance = new MoneroMiner();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    @Override
    public MoneroMiner setMineCallback(MineCallback callback) {
        mMineCallback = callback;
        return this;
    }

    @Override
    public void startMine() {

    }

    @Override
    public void stopMine() {

    }
}
