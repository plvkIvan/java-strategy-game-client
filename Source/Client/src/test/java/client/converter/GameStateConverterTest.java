package client.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.domain.TerrainType;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import org.junit.jupiter.api.Test;

class GameStateConverterTest {

    @Test
    void toDomain_convertsFullMapNodesToDomainSnapshot() {
        FullMapNode myPlayerNode = new FullMapNode(
                ETerrain.Grass,
                EPlayerPositionState.MyPlayerPosition,
                ETreasureState.NoOrUnknownTreasureState,
                EFortState.MyFortPresent,
                0,
                0
        );

        FullMapNode enemyTreasureNode = new FullMapNode(
                ETerrain.Mountain,
                EPlayerPositionState.EnemyPlayerPosition,
                ETreasureState.MyTreasureIsPresent,
                EFortState.EnemyFortPresent,
                1,
                0
        );

        FullMapNode waterNode = new FullMapNode(
                ETerrain.Water,
                EPlayerPositionState.NoPlayerPresent,
                ETreasureState.NoOrUnknownTreasureState,
                EFortState.NoOrUnknownFortState,
                2,
                0
        );

        FullMap fullMap = new FullMap(List.of(
                myPlayerNode,
                enemyTreasureNode,
                waterNode
        ));

        GameState gameState = new GameState(
                fullMap,
                List.of(),
                "game-state-1"
        );

        GameStateConverter converter = new GameStateConverter();

        GameSnapshot snapshot = converter.toDomain(gameState);

        assertNotNull(snapshot);
        assertEquals(3, snapshot.getFullGameMap().getMapNodes().size());

        FullMapNodeInfo myDomainNode =
                snapshot.getFullGameMap().getMapNode(new Position(0, 0));

        assertEquals(TerrainType.GRASS, myDomainNode.getTerrainType());
        assertTrue(myDomainNode.isMyPlayerPresent());
        assertTrue(myDomainNode.isMyFortPresent());

        FullMapNodeInfo enemyTreasureDomainNode =
                snapshot.getFullGameMap().getMapNode(new Position(1, 0));

        assertEquals(TerrainType.MOUNTAIN, enemyTreasureDomainNode.getTerrainType());
        assertTrue(enemyTreasureDomainNode.isEnemyPlayerPresent());
        assertTrue(enemyTreasureDomainNode.isMyTreasurePresent());
        assertTrue(enemyTreasureDomainNode.isEnemyFortPresent());

        FullMapNodeInfo waterDomainNode =
                snapshot.getFullGameMap().getMapNode(new Position(2, 0));

        assertEquals(TerrainType.WATER, waterDomainNode.getTerrainType());
    }
}