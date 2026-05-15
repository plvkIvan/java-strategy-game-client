package client.logic;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import client.domain.GameHalfMap;
import client.domain.Position;
import client.domain.TerrainType;
import client.domain.MapNode;
import client.logic.mapgeneration.CastlePlacementTarget;
import client.logic.mapgeneration.SafeRandomCastlePlacer;
import client.logic.mapgeneration.StrategicCastlePlacer;

public class HalfMapGenerator {
    private static final int HEIGHT = 5;
    private static final int WIDTH = 10;

    private static final int MIN_MOUNTAIN = 5;
    private static final int MIN_WATER = 7;
    private static final int MIN_GRASS = 24;

    private static final int MIN_WALKABLE_ROW = 6;
    private static final int MIN_WALKABLE_COLUMN = 3;

    private final Random rand = new Random();

    private final SafeRandomCastlePlacer randomCastlePlacer = new SafeRandomCastlePlacer(rand);
    private final StrategicCastlePlacer strategicCastlePlacer = new StrategicCastlePlacer(rand);

    public GameHalfMap generate() {
        return generate(CastlePlacementTarget.ANY);
    }

    public GameHalfMap generate(CastlePlacementTarget castlePlacementTarget) {
        while (true) {
            GameHalfMap gameHalfMap = generateCandidate(castlePlacementTarget);
            if (gameHalfMap != null && isValidMap(gameHalfMap)) {
                return gameHalfMap;
            }
        }
    }

    private GameHalfMap generateCandidate(CastlePlacementTarget castlePlacementTarget) {
        List<TerrainType> terrainTypes = new ArrayList<>();

        for (int i = 0; i < MIN_MOUNTAIN; i++) {
            terrainTypes.add(TerrainType.MOUNTAIN);
        }

        for (int i = 0; i < MIN_WATER; i++) {
            terrainTypes.add(TerrainType.WATER);
        }

        for (int i = 0; i < MIN_GRASS; i++) {
            terrainTypes.add(TerrainType.GRASS);
        }

        int numberEmptyNodes = WIDTH * HEIGHT - terrainTypes.size();
        for (int i = 0; i < numberEmptyNodes; i++) {
            terrainTypes.add(TerrainType.GRASS);
        }

        Collections.shuffle(terrainTypes, rand);

        GameHalfMap gameHalfMap = new GameHalfMap();
        int index = 0;

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                TerrainType terrainType = terrainTypes.get(index++);
                gameHalfMap.addMapNode(new MapNode(new Position(j, i), terrainType, false));
            }
        }

        Position castlePosition = chooseCastlePosition(gameHalfMap, castlePlacementTarget);

        if (castlePosition == null) {
            return null;
        }

        MapNode oldCastleNode = gameHalfMap.getMapNode(castlePosition);
        gameHalfMap.addMapNode(new MapNode(castlePosition, oldCastleNode.getTerrainType(), true));

        return gameHalfMap;
    }

    private Position chooseCastlePosition(GameHalfMap gameHalfMap,
                                          CastlePlacementTarget castlePlacementTarget) {
        if (castlePlacementTarget == null || castlePlacementTarget == CastlePlacementTarget.ANY) {
            return randomCastlePlacer.chooseCastlePosition(
                    gameHalfMap,
                    CastlePlacementTarget.ANY
            );
        }

        return strategicCastlePlacer.chooseCastlePosition(
                gameHalfMap,
                castlePlacementTarget
        );
    }

    private boolean isValidMap(GameHalfMap gameHalfMap) {
        if (gameHalfMap.size() != WIDTH * HEIGHT) {
            return false;
        }

        if (gameHalfMap.countCastle() != 1) {
            return false;
        }

        if (gameHalfMap.countTerrainTypes(TerrainType.MOUNTAIN) < MIN_MOUNTAIN) {
            return false;
        }

        if (gameHalfMap.countTerrainTypes(TerrainType.WATER) < MIN_WATER) {
            return false;
        }

        if (gameHalfMap.countTerrainTypes(TerrainType.GRASS) < MIN_GRASS) {
            return false;
        }

        if (gameHalfMap.countWalkableRow(HEIGHT - 1) < MIN_WALKABLE_ROW
                || gameHalfMap.countWalkableRow(0) < MIN_WALKABLE_ROW) {
            return false;
        }

        if (gameHalfMap.countWalkableColumn(WIDTH - 1) < MIN_WALKABLE_COLUMN
                || gameHalfMap.countWalkableColumn(0) < MIN_WALKABLE_COLUMN) {
            return false;
        }

        return isConnected(gameHalfMap);
    }

    private boolean isConnected(GameHalfMap gameHalfMap) {
        Position start = null;

        for (MapNode mapNode : gameHalfMap.getMapNodes()) {
            if (mapNode.isWalkable()) {
                start = mapNode.getPosition();
                break;
            }
        }

        if (start == null) {
            return false;
        }

        List<Position> queue = new ArrayList<>();
        List<Position> visited = new ArrayList<>();

        queue.add(start);
        visited.add(start);

        int index = 0;

        while (index < queue.size()) {
            Position current = queue.get(index++);

            for (Position neighbor : MapUtils.getNeighbors(current, WIDTH, HEIGHT)) {
                MapNode mapNode = gameHalfMap.getMapNode(neighbor);

                if (mapNode != null && mapNode.isWalkable() && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        long walkableMapNodes = gameHalfMap.getMapNodes().stream()
                .filter(MapNode::isWalkable)
                .count();

        return visited.size() == walkableMapNodes;
    }
}