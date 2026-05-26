package client.validation.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import client.domain.GameHalfMap;
import client.domain.MapNode;
import client.domain.Position;
import client.domain.TerrainType;
import client.validation.notification.ValidationNotificationCollector;
import org.junit.jupiter.api.Test;

class TerrainAmountRuleTest {

    @Test
    void validTerrainAmount_addsNoValidationError() {
        GameHalfMap map = new GameHalfMap();

        for (int i = 0; i < 7; i++) {
            map.addMapNode(new MapNode(new Position(i, 0), TerrainType.WATER, false));
        }

        TerrainAmountRule rule = new TerrainAmountRule(TerrainType.WATER, 7);
        ValidationNotificationCollector notifications = new ValidationNotificationCollector();

        rule.validate(map, notifications);

        assertFalse(notifications.hasErrors());
    }

    @Test
    void missingTerrainAmount_addsValidationError() {
        GameHalfMap map = new GameHalfMap();

        for (int i = 0; i < 6; i++) {
            map.addMapNode(new MapNode(new Position(i, 0), TerrainType.WATER, false));
        }

        TerrainAmountRule rule = new TerrainAmountRule(TerrainType.WATER, 7);
        ValidationNotificationCollector notifications = new ValidationNotificationCollector();

        rule.validate(map, notifications);

        assertTrue(notifications.hasErrors());
    }
}