package client.converter;

import java.util.Set;
import java.util.stream.Collectors;

import client.domain.GameHalfMap;
import client.domain.TerrainType;
import client.domain.MapNode;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;

public class HalfMapConverter {
    public PlayerHalfMap toDto(GameHalfMap gameHalfMap, UniquePlayerIdentifier playerId) {
        Set<PlayerHalfMapNode> nodes = gameHalfMap.getMapNodes().stream()
                .map(this::toNodeDto).collect(Collectors.toSet());
        return new PlayerHalfMap(playerId, nodes);
    }

    private PlayerHalfMapNode toNodeDto(MapNode mapNode) {
        return new PlayerHalfMapNode(mapNode.getPosition().getX(),
                mapNode.getPosition().getY(), mapNode.hasCastle(),
                toDtoTerrain(mapNode.getTerrainType()));
    }

    private ETerrain toDtoTerrain(TerrainType terrainType) {
        switch (terrainType) {
            case MOUNTAIN:
                return ETerrain.Mountain;
            case WATER:
                return ETerrain.Water;
            case GRASS:
                return ETerrain.Grass;
            default:
                throw new IllegalStateException("Unknown terrain type :" + terrainType);
        }
    }

}