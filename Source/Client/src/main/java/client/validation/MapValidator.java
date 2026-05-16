package client.validation;

import java.util.List;

import client.domain.GameHalfMap;
import client.validation.notification.ValidationNotificationCollector;

public class MapValidator {
    private final List<MapValidationRule> validationRules;

    public MapValidator(List<MapValidationRule> validationRules) {
        this.validationRules = validationRules;
    }

    public ValidationNotificationCollector validate(GameHalfMap gameHalfMap) {
        ValidationNotificationCollector notifications = new ValidationNotificationCollector();

        for (MapValidationRule validationRule : validationRules) {
            validationRule.validate(gameHalfMap, notifications);
        }

        return notifications;
    }

    public boolean isValid(GameHalfMap gameHalfMap) {
        return !validate(gameHalfMap).hasErrors();
    }
}