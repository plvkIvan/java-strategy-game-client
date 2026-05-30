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

class CastleSearchTest {

    @Test
    void chooseTarget_whenEnemyCastleIsVisible_returnsEnemyCastlePosition() {
        Position enemyCastlePosition = new Position(2, 0);

        FullGameMap map = new FullGameMap();
        map.addMapNode(node(new Position(0, 0), TerrainType.GRASS));
        map.addMapNode(node(new Position(1, 0), TerrainType.GRASS));
        map.addMapNode(enemyCastleNode(enemyCastlePosition));

        GameSnapshot snapshot = new GameSnapshot(map);

        CastleSearch castleSearch = new CastleSearch(Set.of(new Position(0, 0)));

        Position result = castleSearch.chooseTarget(
                snapshot,
                new Position(0, 0),
                new HashSet<>(),
                new PathFinder()
        );

        assertEquals(enemyCastlePosition, result);
    }

    @Test
    void chooseTarget_whenCastleIsNotVisible_returnsNearestEnemyHalfPosition() {
        FullGameMap map = new FullGameMap();

        map.addMapNode(node(new Position(0, 0), TerrainType.GRASS));
        map.addMapNode(node(new Position(1, 0), TerrainType.GRASS));
        map.addMapNode(node(new Position(2, 0), TerrainType.GRASS));

        GameSnapshot snapshot = new GameSnapshot(map);

        Set<Position> ownHalfPositions = Set.of(new Position(0, 0));
        CastleSearch castleSearch = new CastleSearch(ownHalfPositions);

        Position result = castleSearch.chooseTarget(
                snapshot,
                new Position(0, 0),
                new HashSet<>(),
                new PathFinder()
        );

        assertEquals(new Position(1, 0), result);
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

    private FullMapNodeInfo enemyCastleNode(Position position) {
        return new FullMapNodeInfo(
                position,
                TerrainType.GRASS,
                false,
                false,
                false,
                false,
                true
        );
    }
}