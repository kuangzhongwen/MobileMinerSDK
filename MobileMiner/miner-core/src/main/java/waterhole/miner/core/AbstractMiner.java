package waterhole.miner.core;

import android.content.Context;

import static waterhole.miner.core.utils.Preconditions.checkNotNull;

/**
 * 抽象Miner类. 如没有特殊处理，继承此类即可，否则可选择实现{@link CommonMinerIterface}
 *
 * @author kzw on 2018/03/15.
 */
public abstract class AbstractMiner implements CommonMinerIterface {

    // 上下文对象
    private Context mContext;

    // 挖矿回调
    private MineCallback mMineCallback;

    // 是否开启多挖，如果支持多核gpu
    private boolean isUseMultGpusIfSupport;

    @Override
    public CommonMinerIterface setContext(Context context) {
        mContext = context;
        return this;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public AbstractMiner setMineCallback(MineCallback callback) {
        mMineCallback = callback;
        return this;
    }

    @Override
    public MineCallback getMineCallback() {
        return mMineCallback;
    }

    @Override
    public AbstractMiner useMultGpusIfSupport(boolean use) {
        isUseMultGpusIfSupport = use;
        return this;
    }

    @Override
    public boolean isUseMultGpusIfSupport() {
        return isUseMultGpusIfSupport;
    }

    protected void asserts() {
        checkNotNull(mMineCallback, "Context must be not Null");
        checkNotNull(mMineCallback, "MineCallback must be not Null");
    }
}
