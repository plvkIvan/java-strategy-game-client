package client.logic;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;


import client.domain.GameHalfMap;
import client.domain.Position;
import client.domain.TerrainType;
import client.domain.MapNode;

public class HalfMapGenerator {
    private static final int HEIGHT = 5;
    private static final int WIDTH = 10;

    private static final int MIN_MOUNTAIN = 5;
    private static final int MIN_WATER = 7;
    private static final int MIN_GRASS = 24;

    private static final int MIN_WALKABLE_ROW = 6;
    private static final int MIN_WALKABLE_COLUMN = 3;

    private final Random rand = new Random();


    public GameHalfMap generate() {
        while (true) {
            GameHalfMap gameHalfMap = generateCandidate();
            if (gameHalfMap != null && isValidMap(gameHalfMap)) {
                return gameHalfMap;
            }
        }
    }

    private GameHalfMap generateCandidate() {
        List<TerrainType> terrainTypes = new ArrayList<>();

        for(int i = 0; i < MIN_MOUNTAIN; i++) {
            terrainTypes.add(TerrainType.MOUNTAIN);
        }

        for(int i = 0; i < MIN_WATER; i++) {
            terrainTypes.add(TerrainType.WATER);
        }

        for(int i = 0; i < MIN_GRASS; i++) {
            terrainTypes.add(TerrainType.GRASS);
        }

        int numberEmptyNodes = WIDTH*HEIGHT - terrainTypes.size();
        for(int i = 0; i < numberEmptyNodes; i++) {
            terrainTypes.add(TerrainType.GRASS);
        }

        Collections.shuffle(terrainTypes, rand);

        GameHalfMap gameHalfMap= new GameHalfMap();
        int index = 0;

        for(int i = 0; i < HEIGHT; i++) {
            for(int j = 0; j < WIDTH; j++) {
                TerrainType terrainType = terrainTypes.get(index++);
                gameHalfMap.addMapNode(new MapNode(new Position(j, i), terrainType, false));

            }
        }

        Position castlePosition = chooseCastlePosition(gameHalfMap);
        if( castlePosition == null) {
            return null;
        }

        MapNode oldCastleNode = gameHalfMap.getMapNode(castlePosition);
        gameHalfMap.addMapNode(new MapNode(castlePosition, oldCastleNode.getTerrainType(), true));

        return gameHalfMap;
    }

    private Position chooseCastlePosition(GameHalfMap gameHalfMap) {
        List<Position> possibleCastlePositions = new ArrayList<>();

        for(MapNode mapNode : gameHalfMap.getMapNodes()) {
            Position position = mapNode.getPosition();

            boolean isInside = position.getX() > 0 && position.getX() < WIDTH-1
                    && position.getY() > 0 && position.getY() < HEIGHT -1;
            if(isInside && mapNode.getTerrainType() == TerrainType.GRASS) {
                possibleCastlePositions.add(position);
            }
        }
        if(possibleCastlePositions.isEmpty()) {
            return null;
        }

        return possibleCastlePositions.get(rand.nextInt(possibleCastlePositions.size()));
    }

    private boolean isValidMap(GameHalfMap gameHalfMap) {
        if(gameHalfMap.size() != WIDTH*HEIGHT) {
            return false;
        }
        if(gameHalfMap.countCastle() != 1) {
            return false;
        }
        if(gameHalfMap.countTerrainTypes(TerrainType.MOUNTAIN) < MIN_MOUNTAIN) {
            return false;
        }
        if(gameHalfMap.countTerrainTypes(TerrainType.WATER) < MIN_WATER) {
            return false;
        }
        if(gameHalfMap.countTerrainTypes(TerrainType.GRASS) < MIN_GRASS) {
            return false;
        }

        if (gameHalfMap.countWalkableRow(HEIGHT-1) < MIN_WALKABLE_ROW
                || gameHalfMap.countWalkableRow(0) < MIN_WALKABLE_ROW) {
            return false;
        }

        if (gameHalfMap.countWalkableColumn(WIDTH-1) < MIN_WALKABLE_COLUMN
                || gameHalfMap.countWalkableColumn(0) < MIN_WALKABLE_COLUMN) {
            return false;
        }

        return isConnected(gameHalfMap);
    }

    private boolean isConnected(GameHalfMap gameHalfMap) {
        Position start = null;
        for(MapNode mapNode : gameHalfMap.getMapNodes()) {
            if (mapNode.isWalkable()) {
                start = mapNode.getPosition();
                break;
            }
        }
        if(start == null) {
            return false;
        }

        List<Position> queue = new ArrayList<>();
        List<Position> visited = new ArrayList<>();

        queue.add(start);
        visited.add(start);

        int index = 0;

        while(index < queue.size()) {
            Position current = queue.get(index++);

            for (Position neighbor : neighbors(current)) {
                MapNode mapNode = gameHalfMap.getMapNode(neighbor);
                if(mapNode != null && mapNode.isWalkable() && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        long walkableMapNodes = gameHalfMap.getMapNodes().stream()
                .filter(MapNode :: isWalkable).count();
        return visited.size() == walkableMapNodes;
    }

    private List<Position> neighbors(Position position) {
        List<Position> result = new ArrayList<>();

        addIfInside(result, position.getX(), position.getY()-1);
        addIfInside(result, position.getX(), position.getY()+1);
        addIfInside(result, position.getX()-1, position.getY());
        addIfInside(result, position.getX()+1, position.getY());

        return result;
    }

    private void addIfInside(List<Position> positions, int x, int y) {
        if(x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            positions.add(new Position(x, y));
        }
    }
}