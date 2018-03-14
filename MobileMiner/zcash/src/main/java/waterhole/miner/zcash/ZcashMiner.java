package waterhole.miner.zcash;

import android.content.Context;
import android.os.Looper;

import java.io.ObjectStreamException;
import java.util.concurrent.atomic.AtomicBoolean;

import waterhole.miner.core.CommonMinerIterface;
import waterhole.miner.core.ContextWrapper;
import waterhole.miner.core.MineCallback;

import static waterhole.miner.core.utils.Preconditions.checkNotNull;
import static waterhole.miner.core.asyn.AsyncTaskAssistant.executeOnThreadPool;

/**
 * Zcash挖矿类.
 *
 * @author kzw on 2018/03/12.
 */
public final class ZcashMiner implements CommonMinerIterface {

    // 上下文对象
    private final Context mContext = ContextWrapper.getInstance().obtainContext();

    // 挖矿回调
    private MineCallback mMineCallback;

    // 句柄
    private final android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper());

    // 避免重复启动
    private AtomicBoolean isRunningMine = new AtomicBoolean(false);

    private ZcashMiner() {
    }

    public static ZcashMiner instance() {
        return Holder.instance;
    }

    private static class Holder {
        static ZcashMiner instance = new ZcashMiner();
    }

    private Object readResolve() throws ObjectStreamException {
        return instance();
    }

    @Override
    public ZcashMiner setMineCallback(MineCallback callback) {
        mMineCallback = callback;
        return this;
    }

    @Override
    public void startMine() {
        if (!isRunningMine.get()) {
            asserts();
            MineService.startService(mContext, mMineCallback);
            isRunningMine.set(true);
        }
    }

    @Override
    public void stopMine() {
        asserts();
        executeOnThreadPool(new Runnable() {
            @Override
            public void run() {
                MineService.stopService(mContext);
                isRunningMine.set(false);
                // 主线程回调接口
                mHandler.postAtFrontOfQueue(new Runnable() {
                    @Override
                    public void run() {
                        mMineCallback.onMiningStop();
                    }
                });
            }
        });
    }

    private void asserts() {
        checkNotNull(mMineCallback, "MineCallback must be not Null");
    }
}
