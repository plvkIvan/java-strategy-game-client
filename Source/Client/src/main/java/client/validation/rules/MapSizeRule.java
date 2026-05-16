package client.validation.rules;

import client.domain.GameHalfMap;
import client.validation.MapValidationRule;
import client.validation.notification.ValidationNotificationCollector;

public class MapSizeRule implements MapValidationRule {
    private final int expectedSize;

    public MapSizeRule(int expectedSize) {
        this.expectedSize = expectedSize;
    }

    @Override
    public void validate(GameHalfMap gameHalfMap, ValidationNotificationCollector notifications) {
        int actualSize = gameHalfMap.size();

        if (actualSize != expectedSize) {
            notifications.addError(
                    getRuleName(),
                    "Expected map size " + expectedSize + ", but got " + actualSize + "."
            );
        }
    }

    @Override
    public String getRuleName() {
        return "MapSizeRule";
    }
}