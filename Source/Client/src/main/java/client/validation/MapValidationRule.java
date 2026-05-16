package client.validation;

import client.domain.GameHalfMap;
import client.validation.notification.ValidationNotificationCollector;

public interface MapValidationRule {
    void validate(GameHalfMap gameHalfMap, ValidationNotificationCollector notifications);

    String getRuleName();
}