package client.logic.mapgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import client.domain.GameHalfMap;
import client.domain.MapNode;
import client.domain.Position;
import client.domain.TerrainType;

public class StrategicCastlePlacer implements CastlePlacer {
    private static final int LEFT_MIN_X = 0;
    private static final int LEFT_MAX_X = 4;
    private static final int RIGHT_MIN_X = 5;
    private static final int RIGHT_MAX_X = 9;

    private static final int TOP_MIN_Y = 0;
    private static final int TOP_MAX_Y = 2;
    private static final int BOTTOM_MIN_Y = 2;
    private static final int BOTTOM_MAX_Y = 4;

    private final Random random;

    public StrategicCastlePlacer(Random random) {
        this.random = random;
    }

    @Override
    public Position chooseCastlePosition(GameHalfMap gameHalfMap,
                                         CastlePlacementTarget target) {
        if (target == null || target == CastlePlacementTarget.ANY) {
            return null;
        }

        List<Position> possibleCastlePositions = findGrassPositionsInTargetRegion(
                gameHalfMap,
                target
        );

        if (possibleCastlePositions.isEmpty()) {
            return null;
        }

        return possibleCastlePositions.get(random.nextInt(possibleCastlePositions.size()));
    }

    private List<Position> findGrassPositionsInTargetRegion(GameHalfMap gameHalfMap,
                                                            CastlePlacementTarget target) {
        List<Position> possibleCastlePositions = new ArrayList<>();

        for (MapNode mapNode : gameHalfMap.getMapNodes()) {
            Position position = mapNode.getPosition();

            if (mapNode.getTerrainType() != TerrainType.GRASS) {
                continue;
            }

            if (isInTargetRegion(position, target)) {
                possibleCastlePositions.add(position);
            }
        }

        return possibleCastlePositions;
    }

    private boolean isInTargetRegion(Position position, CastlePlacementTarget target) {
        switch (target) {
            case TOP_LEFT:
                return isInside(position, LEFT_MIN_X, LEFT_MAX_X, TOP_MIN_Y, TOP_MAX_Y);

            case TOP_RIGHT:
                return isInside(position, RIGHT_MIN_X, RIGHT_MAX_X, TOP_MIN_Y, TOP_MAX_Y);

            case BOTTOM_LEFT:
                return isInside(position, LEFT_MIN_X, LEFT_MAX_X, BOTTOM_MIN_Y, BOTTOM_MAX_Y);

            case BOTTOM_RIGHT:
                return isInside(position, RIGHT_MIN_X, RIGHT_MAX_X, BOTTOM_MIN_Y, BOTTOM_MAX_Y);

            default:
                return false;
        }
    }

    private boolean isInside(Position position,
                             int minX,
                             int maxX,
                             int minY,
                             int maxY) {
        return position.getX() >= minX
                && position.getX() <= maxX
                && position.getY() >= minY
                && position.getY() <= maxY;
    }
}