package client.validation.rules;

import client.domain.GameHalfMap;
import client.validation.MapValidationRule;

public class MapSizeRule implements MapValidationRule {
    private final int expectedSize;

    public MapSizeRule(int expectedSize) {
        this.expectedSize = expectedSize;
    }

    @Override
    public boolean isSatisfiedBy(GameHalfMap gameHalfMap) {
        return gameHalfMap.size() == expectedSize;
    }

    @Override
    public String getRuleName() {
        return "MapSizeRule";
    }
}