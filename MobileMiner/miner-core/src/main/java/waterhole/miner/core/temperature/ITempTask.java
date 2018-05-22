package waterhole.miner.core.temperature;

public interface ITempTask {
    void start(int[] temperatureSurface);

    void stop();
}
