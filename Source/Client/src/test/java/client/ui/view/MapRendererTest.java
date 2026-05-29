package client.ui.view;

import static org.junit.jupiter.api.Assertions.assertTrue;

import client.domain.FullGameMap;
import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.domain.TerrainType;
import client.ui.model.GameUiSnapshot;
import org.junit.jupiter.api.Test;

class MapRendererTest {

    @Test
    void render_displaysPlayerTreasureCastlesAndTerrain() {
        FullGameMap map = new FullGameMap();

        map.addMapNode(new FullMapNodeInfo(
                new Position(0, 0),
                TerrainType.GRASS,
                true,
                false,
                false,
                false,
                false
        ));

        map.addMapNode(new FullMapNodeInfo(
                new Position(1, 0),
                TerrainType.MOUNTAIN,
                false,
                true,
                false,
                false,
                false
        ));

        map.addMapNode(new FullMapNodeInfo(
                new Position(2, 0),
                TerrainType.WATER,
                false,
                false,
                true,
                false,
                false
        ));

        map.addMapNode(new FullMapNodeInfo(
                new Position(0, 1),
                TerrainType.GRASS,
                false,
                false,
                false,
                true,
                false
        ));

        map.addMapNode(new FullMapNodeInfo(
                new Position(1, 1),
                TerrainType.GRASS,
                false,
                false,
                false,
                false,
                true
        ));

        map.addMapNode(new FullMapNodeInfo(
                new Position(2, 1),
                TerrainType.GRASS,
                false,
                false,
                false,
                false,
                false
        ));

        GameUiSnapshot snapshot = new GameUiSnapshot(
                map,
                new Position(0, 0),
                new Position(1, 0),
                null,
                false,
                false,
                false,
                1
        );

        MapRenderer renderer = new MapRenderer();

        String renderedMap = renderer.render(snapshot);

        assertTrue(renderedMap.contains("\uD83E\uDD16")); // my player
        assertTrue(renderedMap.contains("\uD83D\uDC7E")); // enemy player
        assertTrue(renderedMap.contains("\uD83D\uDCB0")); // treasure
        assertTrue(renderedMap.contains("\uD83C\uDFF0")); // my castle
        assertTrue(renderedMap.contains("\uD83C\uDFEF")); // enemy castle
        assertTrue(renderedMap.contains("\uD83C\uDF31")); // grass
    }

    @Test
    void render_whenBothPlayersOnSamePosition_displaysSwords() {
        FullGameMap map = new FullGameMap();

        map.addMapNode(new FullMapNodeInfo(
                new Position(0, 0),
                TerrainType.GRASS,
                true,
                true,
                false,
                false,
                false
        ));

        GameUiSnapshot snapshot = new GameUiSnapshot(
                map,
                new Position(0, 0),
                new Position(0, 0),
                null,
                false,
                false,
                false,
                1
        );

        MapRenderer renderer = new MapRenderer();

        String renderedMap = renderer.render(snapshot);

        assertTrue(renderedMap.contains("\u2694")); // crossed swords
    }

    @Test
    void render_whenTreasureCollected_displaysCollectedTreasureSymbol() {
        FullGameMap map = new FullGameMap();

        map.addMapNode(new FullMapNodeInfo(
                new Position(0, 0),
                TerrainType.GRASS,
                false,
                false,
                false,
                false,
                false
        ));

        GameUiSnapshot snapshot = new GameUiSnapshot(
                map,
                null,
                null,
                new Position(0, 0),
                true,
                false,
                false,
                1
        );

        MapRenderer renderer = new MapRenderer();

        String renderedMap = renderer.render(snapshot);

        assertTrue(renderedMap.contains("\u2705")); // collected treasure
    }
}