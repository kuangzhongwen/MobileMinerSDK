package waterhole.miner.core.temperature;

import waterhole.miner.core.NoProGuard;

public interface ITempTask extends NoProGuard {

    void start(int[] temperatureSurface);

    void stop();
}
