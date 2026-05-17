package client.domain;

public class GameSnapshot {
    private final FullGameMap fullGameMap;

    public GameSnapshot(FullGameMap fullGameMap) {
        this.fullGameMap = fullGameMap;
    }

    public FullGameMap getFullGameMap() {
        return fullGameMap;
    }
}