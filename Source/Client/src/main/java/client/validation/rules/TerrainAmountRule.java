package client.validation.rules;

import client.domain.GameHalfMap;
import client.domain.TerrainType;
import client.validation.MapValidationRule;
import client.validation.notification.ValidationNotificationCollector;

public class TerrainAmountRule implements MapValidationRule {
    private final TerrainType terrainType;
    private final int minimumAmount;

    public TerrainAmountRule(TerrainType terrainType, int minimumAmount) {
        this.terrainType = terrainType;
        this.minimumAmount = minimumAmount;
    }

    @Override
    public void validate(GameHalfMap gameHalfMap, ValidationNotificationCollector notifications) {
        long actualAmount = gameHalfMap.countTerrainTypes(terrainType);

        if (actualAmount < minimumAmount) {
            notifications.addError(
                    getRuleName(),
                    "Expected at least " + minimumAmount + " fields of type "
                            + terrainType + ", but got " + actualAmount + "."
            );
        }
    }

    @Override
    public String getRuleName() {
        return "TerrainAmountRule_" + terrainType;
    }
}