package client.validation.rules;

import client.domain.GameHalfMap;
import client.domain.TerrainType;
import client.validation.MapValidationRule;

public class TerrainAmountRule implements MapValidationRule {
    private final TerrainType terrainType;
    private final int minimumAmount;

    public TerrainAmountRule(TerrainType terrainType, int minimumAmount) {
        this.terrainType = terrainType;
        this.minimumAmount = minimumAmount;
    }

    @Override
    public boolean isSatisfiedBy(GameHalfMap gameHalfMap) {
        return gameHalfMap.countTerrainTypes(terrainType) >= minimumAmount;
    }

    @Override
    public String getRuleName() {
        return "TerrainAmountRule_" + terrainType;
    }
}