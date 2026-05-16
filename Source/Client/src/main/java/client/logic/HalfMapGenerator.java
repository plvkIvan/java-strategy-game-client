package client.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import client.domain.GameHalfMap;
import client.domain.MapNode;
import client.domain.Position;
import client.domain.TerrainType;
import client.logic.mapgeneration.CastlePlacementTarget;
import client.logic.mapgeneration.SafeRandomCastlePlacer;
import client.logic.mapgeneration.StrategicCastlePlacer;
import client.validation.MapValidationRule;
import client.validation.MapValidator;
import client.validation.notification.ValidationNotificationCollector;
import client.validation.rules.BorderWalkableRule;
import client.validation.rules.ConnectedWalkableFieldsRule;
import client.validation.rules.MapSizeRule;
import client.validation.rules.OneCastleRule;
import client.validation.rules.TerrainAmountRule;

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
    private final MapValidator mapValidator = createMapValidator();

    public GameHalfMap generate() {
        return generate(CastlePlacementTarget.ANY);
    }

    public GameHalfMap generate(CastlePlacementTarget castlePlacementTarget) {
        while (true) {
            GameHalfMap gameHalfMap = generateCandidate(castlePlacementTarget);

            if (gameHalfMap == null) {
                continue;
            }

            ValidationNotificationCollector notifications = mapValidator.validate(gameHalfMap);

            if (!notifications.hasErrors()) {
                return gameHalfMap;
            }

            notifications.printErrorsToSystemErr();
        }
    }

    private GameHalfMap generateCandidate(CastlePlacementTarget castlePlacementTarget) {
        List<TerrainType> terrainTypes = createTerrainTypes();

        Collections.shuffle(terrainTypes, rand);

        GameHalfMap gameHalfMap = createMapFromTerrainTypes(terrainTypes);

        Position castlePosition = chooseCastlePosition(gameHalfMap, castlePlacementTarget);

        if (castlePosition == null) {
            return null;
        }

        placeCastle(gameHalfMap, castlePosition);

        return gameHalfMap;
    }

    private List<TerrainType> createTerrainTypes() {
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

        return terrainTypes;
    }

    private GameHalfMap createMapFromTerrainTypes(List<TerrainType> terrainTypes) {
        GameHalfMap gameHalfMap = new GameHalfMap();
        int index = 0;

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                TerrainType terrainType = terrainTypes.get(index++);
                gameHalfMap.addMapNode(new MapNode(new Position(x, y), terrainType, false));
            }
        }

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

    private void placeCastle(GameHalfMap gameHalfMap, Position castlePosition) {
        MapNode oldCastleNode = gameHalfMap.getMapNode(castlePosition);
        gameHalfMap.addMapNode(new MapNode(
                castlePosition,
                oldCastleNode.getTerrainType(),
                true
        ));
    }

    private MapValidator createMapValidator() {
        List<MapValidationRule> validationRules = new ArrayList<>();

        validationRules.add(new MapSizeRule(WIDTH * HEIGHT));
        validationRules.add(new OneCastleRule());
        validationRules.add(new TerrainAmountRule(TerrainType.MOUNTAIN, MIN_MOUNTAIN));
        validationRules.add(new TerrainAmountRule(TerrainType.WATER, MIN_WATER));
        validationRules.add(new TerrainAmountRule(TerrainType.GRASS, MIN_GRASS));
        validationRules.add(new BorderWalkableRule(
                WIDTH,
                HEIGHT,
                MIN_WALKABLE_ROW,
                MIN_WALKABLE_COLUMN
        ));
        validationRules.add(new ConnectedWalkableFieldsRule(WIDTH, HEIGHT));

        return new MapValidator(validationRules);
    }
}