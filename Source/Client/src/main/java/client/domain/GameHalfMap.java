package client.domain;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;


public class GameHalfMap {
    private final Map<Position, MapNode> mapNodes = new HashMap<>();

    public MapNode getMapNode(Position position) {
        return mapNodes.get(position);
    }
    public void addMapNode(MapNode mapNode) {
        mapNodes.put(mapNode.getPosition(), mapNode);
    }

    public int size() {
        return mapNodes.size();
    }

    public Collection<MapNode> getMapNodes() {
        return mapNodes.values();
    }

    public long countTerrainTypes(TerrainType terrainType) {
        return mapNodes.values().stream()
                .filter(mapNode -> mapNode.getTerrainType() == terrainType).count();
    }

    public long countWalkableColumn(int x) {
        return mapNodes.values().stream()
                .filter(mapNode -> mapNode.getPosition().getX() == x)
                .filter(MapNode::isWalkable).count();
    }

    public long countWalkableRow(int y) {
        return mapNodes.values().stream()
                .filter(mapNode -> mapNode.getPosition().getY() == y)
                .filter(MapNode::isWalkable).count();
    }

    public long countCastle() {
        return mapNodes.values().stream()
                .filter(MapNode::hasCastle).count();
    }
}