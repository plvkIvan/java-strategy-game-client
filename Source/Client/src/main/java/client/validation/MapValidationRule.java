package client.validation;

import client.domain.GameHalfMap;

public interface MapValidationRule {
    boolean isSatisfiedBy(GameHalfMap gameHalfMap);

    String getRuleName();
}