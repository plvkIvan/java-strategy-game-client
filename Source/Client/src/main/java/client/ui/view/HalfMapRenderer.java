package client.ui.view;

import client.domain.GameHalfMap;
import client.domain.GameMapConstants;
import client.domain.MapNode;
import client.domain.Position;
import client.domain.TerrainType;
import client.logic.mapgeneration.CastlePlacementTarget;

public class HalfMapRenderer {

    public void renderGeneratedHalfMap(GameHalfMap gameHalfMap,
                                       CastlePlacementTarget castlePlacementTarget) {
        System.out.println();
        System.out.println("========== GENERATED HALF MAP ==========");
        System.out.println("Castle placement target: " + castlePlacementTarget);
        System.out.println();
        System.out.print(render(gameHalfMap));
        System.out.println();
        System.out.println("Legend: 🏰 own castle | 🌱 grass | ⛰️ mountain | 🌊 water");
        System.out.println("========================================");
        System.out.println();
    }

    private String render(GameHalfMap gameHalfMap) {
        StringBuilder builder = new StringBuilder();

        for (int y = 0; y < GameMapConstants.HALF_MAP_HEIGHT; y++) {
            for (int x = 0; x < GameMapConstants.HALF_MAP_WIDTH; x++) {
                Position position = new Position(x, y);
                MapNode node = gameHalfMap.getMapNode(position);

                builder.append(renderNode(node));
            }

            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }

    private String renderNode(MapNode node) {
        if (node == null) {
            return "⬛";
        }

        if (node.hasCastle()) {
            return "🏰";
        }

        return renderTerrain(node.getTerrainType());
    }

    private String renderTerrain(TerrainType terrainType) {
        if (terrainType == TerrainType.WATER) {
            return "🌊";
        }

        if (terrainType == TerrainType.MOUNTAIN) {
            return "⛰️";
        }

        return "🌱";
    }
}