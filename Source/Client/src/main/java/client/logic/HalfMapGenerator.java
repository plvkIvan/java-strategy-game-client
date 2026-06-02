package client.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import client.domain.GameHalfMap;
import client.domain.GameMapConstants;
import client.domain.MapNode;
import client.domain.Position;
import client.domain.TerrainType;
import client.logic.mapgeneration.CastlePlacementTarget;
import client.logic.mapgeneration.SafeRandomCastlePlacer;
import client.logic.mapgeneration.StrategicCastlePlacer;
import client.ui.view.ValidationNotificationRenderer;
import client.validation.MapValidationRule;
import client.validation.MapValidator;
import client.validation.notification.ValidationNotificationCollector;
import client.validation.rules.BorderWalkableRule;
import client.validation.rules.ConnectedWalkableFieldsRule;
import client.validation.rules.MapSizeRule;
import client.validation.rules.OneCastleRule;
import client.validation.rules.TerrainAmountRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HalfMapGenerator {
    private static final Logger logger = LoggerFactory.getLogger(HalfMapGenerator.class);

    private final Random rand = new Random();

    private final SafeRandomCastlePlacer randomCastlePlacer = new SafeRandomCastlePlacer(rand);
    private final StrategicCastlePlacer strategicCastlePlacer = new StrategicCastlePlacer(rand);
    private final MapValidator mapValidator = createMapValidator();
    private final ValidationNotificationRenderer validationNotificationRenderer =
            new ValidationNotificationRenderer();

    public GameHalfMap generate() {
        return generate(CastlePlacementTarget.ANY);
    }

    public GameHalfMap generate(CastlePlacementTarget castlePlacementTarget) {
        int attempt = 0;

        while (true) {
            attempt++;
            GameHalfMap gameHalfMap = generateCandidate(castlePlacementTarget);

            if (gameHalfMap == null) {
                logger.debug("Half map generation attempt {} produced no castle position.", attempt);
                continue;
            }

            ValidationNotificationCollector notifications = mapValidator.validate(gameHalfMap);

            if (!notifications.hasErrors()) {
                logger.info("Valid half map generated after {} attempt(s) with castle target {}.",
                        attempt,
                        castlePlacementTarget);
                return gameHalfMap;
            }

            logger.debug("Half map generation attempt {} failed validation.", attempt);
            validationNotificationRenderer.renderErrors(notifications);
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

        for (int i = 0; i < GameMapConstants.MIN_MOUNTAIN_FIELDS; i++) {
            terrainTypes.add(TerrainType.MOUNTAIN);
        }

        for (int i = 0; i < GameMapConstants.MIN_WATER_FIELDS; i++) {
            terrainTypes.add(TerrainType.WATER);
        }

        for (int i = 0; i < GameMapConstants.MIN_GRASS_FIELDS; i++) {
            terrainTypes.add(TerrainType.GRASS);
        }

        int numberEmptyNodes = GameMapConstants.HALF_MAP_SIZE - terrainTypes.size();

        for (int i = 0; i < numberEmptyNodes; i++) {
            terrainTypes.add(TerrainType.GRASS);
        }

        return terrainTypes;
    }

    private GameHalfMap createMapFromTerrainTypes(List<TerrainType> terrainTypes) {
        GameHalfMap gameHalfMap = new GameHalfMap();
        int index = 0;

        for (int y = 0; y < GameMapConstants.HALF_MAP_HEIGHT; y++) {
            for (int x = 0; x < GameMapConstants.HALF_MAP_WIDTH; x++) {
                TerrainType terrainType = terrainTypes.get(index++);
                gameHalfMap.addMapNode(new MapNode(new Position(x, y), terrainType, false));
            }
        }

        return gameHalfMap;
    }

    private Position chooseCastlePosition(GameHalfMap gameHalfMap,
                                          CastlePlacementTarget castlePlacementTarget) {
        if (castlePlacementTarget == null || castlePlacementTarget == CastlePlacementTarget.ANY) {
            Position castlePosition = randomCastlePlacer.chooseCastlePosition(
                    gameHalfMap,
                    CastlePlacementTarget.ANY
            );

            logger.debug("Random castle position selected: {}.", castlePosition);
            return castlePosition;
        }

        Position castlePosition = strategicCastlePlacer.chooseCastlePosition(
                gameHalfMap,
                castlePlacementTarget
        );

        logger.debug("Strategic castle position selected: {} for target {}.",
                castlePosition,
                castlePlacementTarget);

        return castlePosition;
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

        validationRules.add(new MapSizeRule(GameMapConstants.HALF_MAP_SIZE));
        validationRules.add(new OneCastleRule());
        validationRules.add(new TerrainAmountRule(
                TerrainType.MOUNTAIN,
                GameMapConstants.MIN_MOUNTAIN_FIELDS
        ));
        validationRules.add(new TerrainAmountRule(
                TerrainType.WATER,
                GameMapConstants.MIN_WATER_FIELDS
        ));
        validationRules.add(new TerrainAmountRule(
                TerrainType.GRASS,
                GameMapConstants.MIN_GRASS_FIELDS
        ));
        validationRules.add(new BorderWalkableRule(
                GameMapConstants.HALF_MAP_WIDTH,
                GameMapConstants.HALF_MAP_HEIGHT,
                GameMapConstants.MIN_WALKABLE_ROW_FIELDS,
                GameMapConstants.MIN_WALKABLE_COLUMN_FIELDS
        ));
        validationRules.add(new ConnectedWalkableFieldsRule(
                GameMapConstants.HALF_MAP_WIDTH,
                GameMapConstants.HALF_MAP_HEIGHT
        ));

        return new MapValidator(validationRules);
    }
}