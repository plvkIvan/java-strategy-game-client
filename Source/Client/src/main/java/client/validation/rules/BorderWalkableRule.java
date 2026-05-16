package client.validation.rules;

import client.domain.GameHalfMap;
import client.validation.MapValidationRule;

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
    public boolean isSatisfiedBy(GameHalfMap gameHalfMap) {
        boolean topRowIsValid = gameHalfMap.countWalkableRow(0) >= minimumWalkableRowFields;
        boolean bottomRowIsValid = gameHalfMap.countWalkableRow(height - 1) >= minimumWalkableRowFields;

        boolean leftColumnIsValid = gameHalfMap.countWalkableColumn(0) >= minimumWalkableColumnFields;
        boolean rightColumnIsValid = gameHalfMap.countWalkableColumn(width - 1) >= minimumWalkableColumnFields;

        return topRowIsValid && bottomRowIsValid && leftColumnIsValid && rightColumnIsValid;
    }

    @Override
    public String getRuleName() {
        return "BorderWalkableRule";
    }
}