package client.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import client.domain.FullGameMap;
import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.MoveDirection;
import client.domain.Position;
import client.domain.TerrainType;
import client.exception.NoReachablePathException;
import org.junit.jupiter.api.Test;

class MovePlannerTest {

    @Test
    void adjacentRight_returnsMoveRight() {
        GameSnapshot snapshot = createSimpleSnapshot();
        PathFinder pathFinder = new PathFinder();
        MovePlanner movePlanner = new MovePlanner(pathFinder);

        MoveDirection move = movePlanner.planNextMove(
                snapshot,
                new Position(0, 0),
                new Position(1, 0)
        );

        assertEquals(MoveDirection.RIGHT, move);
    }

    @Test
    void unreachableTarget_throwsNoReachablePathException() {
        FullGameMap map = new FullGameMap();

        map.addMapNode(new FullMapNodeInfo(
                new Position(0, 0),
                TerrainType.GRASS,
                true,
                false,
                false,
                false,
                false
        ));

        map.addMapNode(new FullMapNodeInfo(
                new Position(1, 0),
                TerrainType.WATER,
                false,
                false,
                false,
                false,
                false
        ));

        map.addMapNode(new FullMapNodeInfo(
                new Position(2, 0),
                TerrainType.GRASS,
                false,
                false,
                false,
                false,
                false
        ));

        MovePlanner movePlanner = new MovePlanner(new PathFinder());
        GameSnapshot snapshot = new GameSnapshot(map);

        assertThrows(NoReachablePathException.class, () -> movePlanner.planNextMove(
                snapshot,
                new Position(0, 0),
                new Position(2, 0)
        ));
    }

    private GameSnapshot createSimpleSnapshot() {
        FullGameMap map = new FullGameMap();

        map.addMapNode(new FullMapNodeInfo(
                new Position(0, 0),
                TerrainType.GRASS,
                true,
                false,
                false,
                false,
                false
        ));

        map.addMapNode(new FullMapNodeInfo(
                new Position(1, 0),
                TerrainType.GRASS,
                false,
                false,
                false,
                false,
                false
        ));

        return new GameSnapshot(map);
    }
}