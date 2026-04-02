package client.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import client.domain.GameHalfMap;
import client.domain.Position;
import client.domain.TerrainType;
import client.domain.MapNode;

public class HalfMapGenerator {
    private static final int HEIGHT = 5;
    private static final int WIDTH = 10;

    private static final int MIN_MOUNTAIN  = 5;
    private static final int MIN_WATER  = 7;
    private static final int MIN_GRASS  = 24;

    private final Random rand = new Random();


    public GameHalfMap generate() {
        while (true) {
            GameHalfMap gameHalfMap = generateCandidate();
            if (isValid(gameHalfMap)) {
                return gameHalfMap;
            }
        }
    }

    private GameHalfMap generateCandidate() {
        List<TerrainType> terrainTypes = new ArrayList<>();

        for(int i = 0; i < MIN_MOUNTAIN; i++) {
            terrainTypes.add(TerrainType.MOUNTAIN);
        }

        for(int i = 0; i < MIN_WATER + 1; i++) {
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
        int castleX = rand.nextInt(WIDTH);
        int castleY = rand.nextInt(HEIGHT);
        int index = 0;

        for(int i = 0; i < HEIGHT; i++) {
            for(int j = 0; j < WIDTH; j++) {
                TerrainType terrainType = terrainTypes.get(index++);
                boolean hasCastle = (i == castleY && j == castleX) ;
                if (hasCastle && terrainType == TerrainType.WATER) {
                    terrainType = TerrainType.GRASS;
                }
                gameHalfMap.addMapNode(new MapNode(new Position(j, i), terrainType, hasCastle));

            }
        }
        return gameHalfMap;
    }
}