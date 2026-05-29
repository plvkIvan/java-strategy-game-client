package client.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import client.domain.GameHalfMap;
import client.domain.MapNode;
import client.domain.Position;
import client.domain.TerrainType;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import org.junit.jupiter.api.Test;

class HalfMapConverterTest {

    @Test
    void toDto_convertsAllMapNodes() {
        GameHalfMap gameHalfMap = new GameHalfMap();

        gameHalfMap.addMapNode(new MapNode(
                new Position(0, 0),
                TerrainType.GRASS,
                true
        ));

        gameHalfMap.addMapNode(new MapNode(
                new Position(1, 0),
                TerrainType.WATER,
                false
        ));

        gameHalfMap.addMapNode(new MapNode(
                new Position(2, 0),
                TerrainType.MOUNTAIN,
                false
        ));

        UniquePlayerIdentifier playerId = UniquePlayerIdentifier.of("player-1");

        HalfMapConverter converter = new HalfMapConverter();

        PlayerHalfMap playerHalfMap = converter.toDto(gameHalfMap, playerId);

        assertEquals("player-1", playerHalfMap.getUniquePlayerID());
        assertEquals(3, playerHalfMap.getMapNodes().size());

        assertTrue(containsNode(
                playerHalfMap,
                0,
                0,
                true,
                ETerrain.Grass
        ));

        assertTrue(containsNode(
                playerHalfMap,
                1,
                0,
                false,
                ETerrain.Water
        ));

        assertTrue(containsNode(
                playerHalfMap,
                2,
                0,
                false,
                ETerrain.Mountain
        ));
    }

    private boolean containsNode(PlayerHalfMap playerHalfMap,
                                 int x,
                                 int y,
                                 boolean fortPresent,
                                 ETerrain terrain) {
        for (PlayerHalfMapNode node : playerHalfMap.getMapNodes()) {
            if (node.getX() == x
                    && node.getY() == y
                    && node.isFortPresent() == fortPresent
                    && node.getTerrain() == terrain) {
                return true;
            }
        }

        return false;
    }
}