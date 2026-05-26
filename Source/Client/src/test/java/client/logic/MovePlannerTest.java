package client.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import client.domain.FullGameMap;
import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.domain.TerrainType;
import messagesbase.messagesfromclient.EMove;
import org.junit.jupiter.api.Test;

class MovePlannerTest {

    @Test
    void adjacentRight_returnsMoveRight() {
        GameSnapshot snapshot = createSimpleSnapshot();
        PathFinder pathFinder = new PathFinder();
        MovePlanner movePlanner = new MovePlanner(pathFinder);

        EMove move = movePlanner.planNextMove(
                snapshot,
                new Position(0, 0),
                new Position(1, 0)
        );

        assertEquals(EMove.Right, move);
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