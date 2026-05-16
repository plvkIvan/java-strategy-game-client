package client.validation.rules;

import client.domain.GameHalfMap;
import client.validation.MapValidationRule;
import client.validation.notification.ValidationNotificationCollector;

public class BorderWalkableRule implements MapValidationRule {
    private final int width;
    private final int height;
    private final int minimumWalkableRowFields;
    private final int minimumWalkableColumnFields;

    public BorderWalkableRule(int width,
                              int height,
                              int minimumWalkableRowFields,
                              int minimumWalkableColumnFields) {
        this.width = width;
        this.height = height;
        this.minimumWalkableRowFields = minimumWalkableRowFields;
        this.minimumWalkableColumnFields = minimumWalkableColumnFields;
    }

    @Override
    public void validate(GameHalfMap gameHalfMap, ValidationNotificationCollector notifications) {
        long topRowWalkableFields = gameHalfMap.countWalkableRow(0);
        long bottomRowWalkableFields = gameHalfMap.countWalkableRow(height - 1);
        long leftColumnWalkableFields = gameHalfMap.countWalkableColumn(0);
        long rightColumnWalkableFields = gameHalfMap.countWalkableColumn(width - 1);

        if (topRowWalkableFields < minimumWalkableRowFields) {
            notifications.addError(
                    getRuleName(),
                    "Top border needs at least " + minimumWalkableRowFields
                            + " walkable fields, but got " + topRowWalkableFields + "."
            );
        }

        if (bottomRowWalkableFields < minimumWalkableRowFields) {
            notifications.addError(
                    getRuleName(),
                    "Bottom border needs at least " + minimumWalkableRowFields
                            + " walkable fields, but got " + bottomRowWalkableFields + "."
            );
        }

        if (leftColumnWalkableFields < minimumWalkableColumnFields) {
            notifications.addError(
                    getRuleName(),
                    "Left border needs at least " + minimumWalkableColumnFields
                            + " walkable fields, but got " + leftColumnWalkableFields + "."
            );
        }

        if (rightColumnWalkableFields < minimumWalkableColumnFields) {
            notifications.addError(
                    getRuleName(),
                    "Right border needs at least " + minimumWalkableColumnFields
                            + " walkable fields, but got " + rightColumnWalkableFields + "."
            );
        }
    }

    @Override
    public String getRuleName() {
        return "BorderWalkableRule";
    }
}