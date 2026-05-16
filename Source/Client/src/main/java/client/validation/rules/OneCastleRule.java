package client.validation.rules;

import client.domain.GameHalfMap;
import client.validation.MapValidationRule;
import client.validation.notification.ValidationNotificationCollector;

public class OneCastleRule implements MapValidationRule {

    @Override
    public void validate(GameHalfMap gameHalfMap, ValidationNotificationCollector notifications) {
        long castleCount = gameHalfMap.countCastle();

        if (castleCount != 1) {
            notifications.addError(
                    getRuleName(),
                    "Expected exactly one castle, but got " + castleCount + "."
            );
        }
    }

    @Override
    public String getRuleName() {
        return "OneCastleRule";
    }
}