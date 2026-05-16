package client.validation;

import java.util.List;

import client.domain.GameHalfMap;

public class MapValidator {
    private final List<MapValidationRule> validationRules;

    public MapValidator(List<MapValidationRule> validationRules) {
        this.validationRules = validationRules;
    }

    public boolean isValid(GameHalfMap gameHalfMap) {
        for (MapValidationRule validationRule : validationRules) {
            if (!validationRule.isSatisfiedBy(gameHalfMap)) {
                return false;
            }
        }

        return true;
    }
}