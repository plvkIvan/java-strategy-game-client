package client.validation.rules;

import client.domain.GameHalfMap;
import client.validation.MapValidationRule;

public class OneCastleRule implements MapValidationRule {

    @Override
    public boolean isSatisfiedBy(GameHalfMap gameHalfMap) {
        return gameHalfMap.countCastle() == 1;
    }

    @Override
    public String getRuleName() {
        return "OneCastleRule";
    }
}