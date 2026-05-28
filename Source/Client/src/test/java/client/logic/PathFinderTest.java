package client.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import client.domain.FullGameMap;
import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.domain.TerrainType;
import org.junit.jupiter.api.Test;

class PathFinderTest {

    @Test
    void findPath_connectedGrassFields_returnsPath() {
        GameSnapshot snapshot = createLineSnapshot();
        PathFinder pathFinder = new PathFinder();

        List<Position> path = pathFinder.findPath(
                snapshot,
                new Position(0, 0),
                new Position(2, 0)
        );

        assertEquals(3, path.size());
        assertEquals(new Position(0, 0), path.get(0));
        assertEquals(new Position(1, 0), path.get(1));
        assertEquals(new Position(2, 0), path.get(2));
    }

    @Test
    void findPath_targetNotInMap_returnsEmptyPath() {
        GameSnapshot snapshot = createLineSnapshot();
        PathFinder pathFinder = new PathFinder();

        List<Position> path = pathFinder.findPath(
                snapshot,
                new Position(0, 0),
                new Position(9, 9)
        );

        assertTrue(path.isEmpty());
    }

    private GameSnapshot createLineSnapshot() {
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

        map.addMapNode(new FullMapNodeInfo(
                new Position(2, 0),
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