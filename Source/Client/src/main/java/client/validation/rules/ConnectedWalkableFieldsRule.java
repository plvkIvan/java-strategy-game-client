package client.validation.rules;

import java.util.ArrayList;
import java.util.List;

import client.domain.GameHalfMap;
import client.domain.MapNode;
import client.domain.Position;
import client.logic.MapUtils;
import client.validation.MapValidationRule;
import client.validation.notification.ValidationNotificationCollector;

public class ConnectedWalkableFieldsRule implements MapValidationRule {
    private final int width;
    private final int height;

    public ConnectedWalkableFieldsRule(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void validate(GameHalfMap gameHalfMap, ValidationNotificationCollector notifications) {
        Position start = findFirstWalkablePosition(gameHalfMap);

        if (start == null) {
            notifications.addError(
                    getRuleName(),
                    "Map has no walkable field."
            );
            return;
        }

        List<Position> queue = new ArrayList<>();
        List<Position> visited = new ArrayList<>();

        queue.add(start);
        visited.add(start);

        int index = 0;

        while (index < queue.size()) {
            Position current = queue.get(index++);

            for (Position neighbor : MapUtils.getNeighbors(current, width, height)) {
                MapNode mapNode = gameHalfMap.getMapNode(neighbor);

                if (mapNode != null && mapNode.isWalkable() && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        long walkableMapNodes = gameHalfMap.getMapNodes().stream()
                .filter(MapNode::isWalkable)
                .count();

        if (visited.size() != walkableMapNodes) {
            notifications.addError(
                    getRuleName(),
                    "Not all walkable fields are connected. Reachable: "
                            + visited.size() + ", walkable total: " + walkableMapNodes + "."
            );
        }
    }

    @Override
    public String getRuleName() {
        return "ConnectedWalkableFieldsRule";
    }

    private Position findFirstWalkablePosition(GameHalfMap gameHalfMap) {
        for (MapNode mapNode : gameHalfMap.getMapNodes()) {
            if (mapNode.isWalkable()) {
                return mapNode.getPosition();
            }
        }

        return null;
    }
}