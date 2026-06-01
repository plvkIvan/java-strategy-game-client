package client.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import client.domain.FullGameMap;
import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.MoveDirection;
import client.domain.Position;
import client.domain.TerrainType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MovePlannerParameterizedTest {

    @ParameterizedTest
    @MethodSource("directionCases")
    void directionCases_returnCorrectMove(Position start,
                                          Position target,
                                          MoveDirection expectedMove) {
        GameSnapshot snapshot = createSnapshotWithPositions(start, target);
        PathFinder pathFinder = new PathFinder();
        MovePlanner movePlanner = new MovePlanner(pathFinder);

        MoveDirection actualMove = movePlanner.planNextMove(snapshot, start, target);

        assertEquals(expectedMove, actualMove);
    }

    private static Stream<Arguments> directionCases() {
        return Stream.of(
                Arguments.of(new Position(1, 1), new Position(1, 0), MoveDirection.UP),
                Arguments.of(new Position(1, 1), new Position(1, 2), MoveDirection.DOWN),
                Arguments.of(new Position(1, 1), new Position(0, 1), MoveDirection.LEFT),
                Arguments.of(new Position(1, 1), new Position(2, 1), MoveDirection.RIGHT)
        );
    }

    private GameSnapshot createSnapshotWithPositions(Position start, Position target) {
        FullGameMap map = new FullGameMap();

        map.addMapNode(new FullMapNodeInfo(
                start,
                TerrainType.GRASS,
                true,
                false,
                false,
                false,
                false
        ));

        map.addMapNode(new FullMapNodeInfo(
                target,
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