package waterhole.miner.core.controller;

import waterhole.miner.core.NoProGuard;

public interface ITempTask extends NoProGuard {

    void start(int[] temperatureSurface);

    void stop();
}
