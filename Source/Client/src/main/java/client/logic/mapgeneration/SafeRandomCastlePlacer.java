package client.logic.mapgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import client.domain.GameHalfMap;
import client.domain.GameMapConstants;
import client.domain.MapNode;
import client.domain.Position;
import client.domain.TerrainType;

public class SafeRandomCastlePlacer implements CastlePlacer {
    private final Random random;

    public SafeRandomCastlePlacer(Random random) {
        this.random = random;
    }

    @Override
    public Position chooseCastlePosition(GameHalfMap gameHalfMap,
                                         CastlePlacementTarget target) {
        List<Position> possibleCastlePositions = new ArrayList<>();

        for (MapNode mapNode : gameHalfMap.getMapNodes()) {
            Position position = mapNode.getPosition();

            boolean isInside = position.getX() > 0
                    && position.getX() < GameMapConstants.HALF_MAP_WIDTH - 1
                    && position.getY() > 0
                    && position.getY() < GameMapConstants.HALF_MAP_HEIGHT - 1;

            if (isInside && mapNode.getTerrainType() == TerrainType.GRASS) {
                possibleCastlePositions.add(position);
            }
        }

        if (possibleCastlePositions.isEmpty()) {
            return null;
        }

        return possibleCastlePositions.get(random.nextInt(possibleCastlePositions.size()));
    }
}