package client.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import client.domain.FullGameMap;
import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.domain.TerrainType;
import org.junit.jupiter.api.Test;

class TreasureSearchTest {

    @Test
    void chooseTarget_whenTreasureIsVisible_returnsTreasurePosition() {
        Position treasurePosition = new Position(2, 0);
        GameSnapshot snapshot = createSnapshotWithTreasure(treasurePosition);

        Set<Position> ownHalfPositions = Set.of(
                new Position(0, 0),
                new Position(1, 0),
                treasurePosition
        );

        TreasureSearch treasureSearch = new TreasureSearch(ownHalfPositions);

        Position result = treasureSearch.chooseTarget(
                snapshot,
                new Position(0, 0),
                new HashSet<>(),
                new PathFinder()
        );

        assertEquals(treasurePosition, result);
    }

    @Test
    void chooseTarget_whenNoTreasureVisible_returnsNearestUnvisitedOwnHalfPosition() {
        FullGameMap map = new FullGameMap();

        map.addMapNode(node(new Position(0, 0), TerrainType.GRASS));
        map.addMapNode(node(new Position(1, 0), TerrainType.GRASS));
        map.addMapNode(node(new Position(2, 0), TerrainType.GRASS));

        GameSnapshot snapshot = new GameSnapshot(map);

        Set<Position> ownHalfPositions = Set.of(
                new Position(0, 0),
                new Position(1, 0),
                new Position(2, 0)
        );

        Set<Position> visitedPositions = new HashSet<>();
        visitedPositions.add(new Position(0, 0));

        TreasureSearch treasureSearch = new TreasureSearch(ownHalfPositions);

        Position result = treasureSearch.chooseTarget(
                snapshot,
                new Position(0, 0),
                visitedPositions,
                new PathFinder()
        );

        assertEquals(new Position(1, 0), result);
    }

    private GameSnapshot createSnapshotWithTreasure(Position treasurePosition) {
        FullGameMap map = new FullGameMap();

        map.addMapNode(node(new Position(0, 0), TerrainType.GRASS));
        map.addMapNode(node(new Position(1, 0), TerrainType.GRASS));

        map.addMapNode(new FullMapNodeInfo(
                treasurePosition,
                TerrainType.GRASS,
                false,
                false,
                true,
                false,
                false
        ));

        return new GameSnapshot(map);
    }

    private FullMapNodeInfo node(Position position, TerrainType terrainType) {
        return new FullMapNodeInfo(
                position,
                terrainType,
                false,
                false,
                false,
                false,
                false
        );
    }
}