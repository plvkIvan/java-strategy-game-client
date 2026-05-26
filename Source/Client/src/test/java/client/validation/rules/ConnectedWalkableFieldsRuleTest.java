package client.validation.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import client.domain.GameHalfMap;
import client.domain.MapNode;
import client.domain.Position;
import client.domain.TerrainType;
import client.validation.notification.ValidationNotificationCollector;
import org.junit.jupiter.api.Test;

class ConnectedWalkableFieldsRuleTest {

    @Test
    void connectedWalkableFields_addsNoValidationError() {
        GameHalfMap map = new GameHalfMap();

        map.addMapNode(new MapNode(new Position(0, 0), TerrainType.GRASS, false));
        map.addMapNode(new MapNode(new Position(1, 0), TerrainType.GRASS, false));
        map.addMapNode(new MapNode(new Position(2, 0), TerrainType.MOUNTAIN, false));

        ConnectedWalkableFieldsRule rule = new ConnectedWalkableFieldsRule(10, 5);
        ValidationNotificationCollector notifications = new ValidationNotificationCollector();

        rule.validate(map, notifications);

        assertFalse(notifications.hasErrors());
    }

    @Test
    void disconnectedWalkableFields_addsValidationError() {
        GameHalfMap map = new GameHalfMap();

        map.addMapNode(new MapNode(new Position(0, 0), TerrainType.GRASS, false));
        map.addMapNode(new MapNode(new Position(9, 4), TerrainType.GRASS, false));

        ConnectedWalkableFieldsRule rule = new ConnectedWalkableFieldsRule(10, 5);
        ValidationNotificationCollector notifications = new ValidationNotificationCollector();

        rule.validate(map, notifications);

        assertTrue(notifications.hasErrors());
    }
}