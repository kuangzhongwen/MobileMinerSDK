package waterhole.miner.zcash;

import java.io.ObjectStreamException;

import waterhole.miner.core.AbstractMiner;
import waterhole.miner.core.CommonMinerInterface;

/**
 * Zcash挖矿类.
 *
 * @author kzw on 2018/03/12.
 */
public final class ZcashMiner extends AbstractMiner {

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
    public CommonMinerInterface setWalletAddr(String walletAddr) {
        return null;
    }

    @Override
    public void startMine() {
        asserts();
        MineService.startService(getContext());
    }

    @Override
    public void stopMine() {
        asserts();
        MineService.stopService(getContext());
    }
}
