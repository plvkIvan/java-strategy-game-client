package client.domain;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;


public class GameHalfMap {
    private final Map<Position, MapNode> mapNodes = new HashMap<>();

    public MapNode getMapNode(Position position) {
        return mapNodes.get(position);
    }
    public void addMapNode(MapNode mapNode) {
        mapNodes.put(mapNodes.getPosition(), mapNode);
    }

    public int size() {
        return mapNodes.size();
    }

    public Collection<MapNode> getMapNodes() {
        return mapNodes.values();
    }

    public int countTerrainTypes(TerrainType terrainType) {
        return mapNodes.values().stream()
                .filter(mapNode -> mapNode.getTerrain() == terrainType).count();
    }

    public int countWalkableColumn(int x) {
        return mapNodes.values().stream()
                .filter(mapNode -> mapNode.getPosition().getX() == x)
                .filter(MapNode::isWalkable).count();
    }

    public int countWalkableRow(int y) {
        return mapNodes.values().stream()
                .filter(mapNode -> mapNode.getPosition().getY() == y)
                .filter(MapNode::isWalkable).count();
    }

    public int countCastle() {
        return mapNodes.values().stream()
                .filter(MapNode::hasCastle).count();
    }
}