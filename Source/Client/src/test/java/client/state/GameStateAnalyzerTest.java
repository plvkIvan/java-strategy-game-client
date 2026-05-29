package client.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import client.domain.Position;
import client.logic.mapgeneration.CastlePlacementTarget;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import org.junit.jupiter.api.Test;

class GameStateAnalyzerTest {

    @Test
    void findOwnPlayerState_whenPlayerExists_returnsPlayerState() {
        UniquePlayerIdentifier ownPlayerId = UniquePlayerIdentifier.of("own-player");
        UniquePlayerIdentifier enemyPlayerId = UniquePlayerIdentifier.of("enemy-player");

        PlayerState ownPlayerState = new PlayerState(
                "Ivan",
                "Polovko",
                "polovkoi98",
                EPlayerGameState.MustAct,
                ownPlayerId,
                false
        );

        PlayerState enemyPlayerState = new PlayerState(
                "Enemy",
                "Player",
                "enemy",
                EPlayerGameState.MustWait,
                enemyPlayerId,
                false
        );

        GameState gameState = new GameState(
                List.of(ownPlayerState, enemyPlayerState),
                "state-1"
        );

        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        PlayerState result = analyzer.findOwnPlayerState(gameState, ownPlayerId);

        assertEquals(ownPlayerState, result);
    }

    @Test
    void findOwnPlayerState_whenPlayerDoesNotExist_returnsNull() {
        UniquePlayerIdentifier ownPlayerId = UniquePlayerIdentifier.of("own-player");

        PlayerState enemyPlayerState = new PlayerState(
                "Enemy",
                "Player",
                "enemy",
                EPlayerGameState.MustWait,
                UniquePlayerIdentifier.of("enemy-player"),
                false
        );

        GameState gameState = new GameState(
                List.of(enemyPlayerState),
                "state-1"
        );

        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        PlayerState result = analyzer.findOwnPlayerState(gameState, ownPlayerId);

        assertNull(result);
    }

    @Test
    void findCurrentPosition_whenMyPlayerIsPresent_returnsPosition() {
        FullMapNode myPositionNode = new FullMapNode(
                ETerrain.Grass,
                EPlayerPositionState.MyPlayerPosition,
                ETreasureState.NoOrUnknownTreasureState,
                EFortState.NoOrUnknownFortState,
                3,
                4
        );

        GameState gameState = createGameStateWithNodes(List.of(myPositionNode));

        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        Position result = analyzer.findCurrentPosition(gameState);

        assertEquals(new Position(3, 4), result);
    }

    @Test
    void findCurrentPosition_whenMyPlayerIsMissing_returnsNull() {
        FullMapNode emptyNode = new FullMapNode(
                ETerrain.Grass,
                EPlayerPositionState.NoPlayerPresent,
                ETreasureState.NoOrUnknownTreasureState,
                EFortState.NoOrUnknownFortState,
                3,
                4
        );

        GameState gameState = createGameStateWithNodes(List.of(emptyNode));

        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        Position result = analyzer.findCurrentPosition(gameState);

        assertNull(result);
    }

    @Test
    void determineOwnHalf_whenMapHasLessThanHundredNodes_returnsNull() {
        GameState gameState = createGameStateWithNodes(List.of(
                new FullMapNode(
                        ETerrain.Grass,
                        EPlayerPositionState.NoPlayerPresent,
                        ETreasureState.NoOrUnknownTreasureState,
                        EFortState.MyFortPresent,
                        0,
                        0
                )
        ));

        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        Set<Position> result = analyzer.determineOwnHalf(gameState);

        assertNull(result);
    }

    @Test
    void determineOwnHalf_whenOwnCastleIsOnLeftSide_returnsLeftHalf() {
        GameState gameState = createFullHorizontalGameState(2, 2);

        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        Set<Position> ownHalf = analyzer.determineOwnHalf(gameState);

        assertEquals(50, ownHalf.size());
        assertTrue(ownHalf.contains(new Position(0, 0)));
        assertTrue(ownHalf.contains(new Position(9, 4)));
    }

    @Test
    void expandMountainVision_whenCurrentPositionIsMountain_addsNeighborPositions() {
        FullMapNode mountainNode = new FullMapNode(
                ETerrain.Mountain,
                EPlayerPositionState.MyPlayerPosition,
                ETreasureState.NoOrUnknownTreasureState,
                EFortState.NoOrUnknownFortState,
                5,
                5
        );

        GameState gameState = createGameStateWithNodes(List.of(mountainNode));
        Set<Position> visitedPositions = new HashSet<>();
        Position currentPosition = new Position(5, 5);

        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        analyzer.expandMountainVision(gameState, currentPosition, visitedPositions);

        assertEquals(8, visitedPositions.size());
        assertTrue(visitedPositions.contains(new Position(4, 4)));
        assertTrue(visitedPositions.contains(new Position(5, 4)));
        assertTrue(visitedPositions.contains(new Position(6, 6)));
    }

    @Test
    void expandMountainVision_whenCurrentPositionIsGrass_doesNotAddNeighborPositions() {
        FullMapNode grassNode = new FullMapNode(
                ETerrain.Grass,
                EPlayerPositionState.MyPlayerPosition,
                ETreasureState.NoOrUnknownTreasureState,
                EFortState.NoOrUnknownFortState,
                5,
                5
        );

        GameState gameState = createGameStateWithNodes(List.of(grassNode));
        Set<Position> visitedPositions = new HashSet<>();

        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        analyzer.expandMountainVision(gameState, new Position(5, 5), visitedPositions);

        assertTrue(visitedPositions.isEmpty());
    }

    @Test
    void determineCastlePlacementTarget_whenGameStateIsNull_returnsAny() {
        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        CastlePlacementTarget result = analyzer.determineCastlePlacementTarget(null);

        assertEquals(CastlePlacementTarget.ANY, result);
    }

    @Test
    void determineCastlePlacementTarget_whenEnemyHalfStartsAtOrigin_returnsBottomRight() {
        GameState gameState = createGameStateWithNodes(List.of(
                createNodeAt(0, 0)
        ));

        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        CastlePlacementTarget result = analyzer.determineCastlePlacementTarget(gameState);

        assertEquals(CastlePlacementTarget.BOTTOM_RIGHT, result);
    }

    @Test
    void determineCastlePlacementTarget_whenEnemyHalfIsRight_returnsBottomLeft() {
        GameState gameState = createGameStateWithNodes(List.of(
                createNodeAt(10, 0)
        ));

        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        CastlePlacementTarget result = analyzer.determineCastlePlacementTarget(gameState);

        assertEquals(CastlePlacementTarget.BOTTOM_LEFT, result);
    }

    @Test
    void determineCastlePlacementTarget_whenEnemyHalfIsBottom_returnsTopRight() {
        GameState gameState = createGameStateWithNodes(List.of(
                createNodeAt(0, 5)
        ));

        GameStateAnalyzer analyzer = new GameStateAnalyzer();

        CastlePlacementTarget result = analyzer.determineCastlePlacementTarget(gameState);

        assertEquals(CastlePlacementTarget.TOP_RIGHT, result);
    }

    private GameState createGameStateWithNodes(List<FullMapNode> nodes) {
        FullMap fullMap = new FullMap(nodes);

        return new GameState(
                fullMap,
                List.of(),
                "state"
        );
    }

    private FullMapNode createNodeAt(int x, int y) {
        return new FullMapNode(
                ETerrain.Grass,
                EPlayerPositionState.NoPlayerPresent,
                ETreasureState.NoOrUnknownTreasureState,
                EFortState.NoOrUnknownFortState,
                x,
                y
        );
    }

    private GameState createFullHorizontalGameState(int castleX, int castleY) {
        List<FullMapNode> nodes = new ArrayList<>();

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 20; x++) {
                EFortState fortState = EFortState.NoOrUnknownFortState;

                if (x == castleX && y == castleY) {
                    fortState = EFortState.MyFortPresent;
                }

                nodes.add(new FullMapNode(
                        ETerrain.Grass,
                        EPlayerPositionState.NoPlayerPresent,
                        ETreasureState.NoOrUnknownTreasureState,
                        fortState,
                        x,
                        y
                ));
            }
        }

        return createGameStateWithNodes(nodes);
    }
}