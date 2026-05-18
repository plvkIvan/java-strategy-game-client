package client.ui.view;

import client.domain.FullGameMap;
import client.domain.FullMapNodeInfo;
import client.domain.Position;
import client.domain.TerrainType;
import client.ui.model.GameUiSnapshot;

public class MapRenderer {

    public String render(GameUiSnapshot snapshot) {
        StringBuilder builder = new StringBuilder();

        FullGameMap fullGameMap = snapshot.getFullGameMap();

        int width = fullGameMap.getWidth();
        int height = fullGameMap.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position position = new Position(x, y);
                FullMapNodeInfo node = fullGameMap.getMapNode(position);

                builder.append(renderNode(node, position, snapshot));
            }

            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }

    private String renderNode(FullMapNodeInfo node,
                              Position position,
                              GameUiSnapshot snapshot) {
        if (node == null) {
            return "⬛";
        }

        if (position.equals(snapshot.getMyPosition())
                && snapshot.getEnemyPosition() != null
                && position.equals(snapshot.getEnemyPosition())) {
            return "⚔️";
        }

        if (position.equals(snapshot.getMyPosition())) {
            return "🤖";
        }

        if (snapshot.getEnemyPosition() != null && position.equals(snapshot.getEnemyPosition())) {
            return "👾";
        }

        if (node.isMyTreasurePresent()) {
            return "💰";
        }

        if (node.isMyFortPresent()) {
            return "🏰";
        }

        if (node.isEnemyFortPresent()) {
            return "🏯";
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