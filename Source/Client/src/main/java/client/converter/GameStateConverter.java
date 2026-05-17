package client.converter;

import client.domain.FullGameMap;
import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.domain.TerrainType;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;

public class GameStateConverter {

    public GameSnapshot toDomain(GameState gameState) {
        FullGameMap fullGameMap = new FullGameMap();

        for (FullMapNode node : gameState.getMap().getMapNodes()) {
            fullGameMap.addMapNode(toDomainNode(node));
        }

        return new GameSnapshot(fullGameMap);
    }

    private FullMapNodeInfo toDomainNode(FullMapNode node) {
        Position position = new Position(node.getX(), node.getY());
        EPlayerPositionState playerPositionState = node.getPlayerPositionState();

        return new FullMapNodeInfo(
                position,
                toDomainTerrain(node.getTerrain()),
                playerPositionState.representsMyPlayer(),
                representsEnemyPlayer(playerPositionState),
                node.getTreasureState() == ETreasureState.MyTreasureIsPresent,
                node.getFortState() == EFortState.MyFortPresent,
                node.getFortState() == EFortState.EnemyFortPresent
        );
    }

    private boolean representsEnemyPlayer(EPlayerPositionState playerPositionState) {
        String stateName = playerPositionState.name();
        return stateName.contains("Enemy") || stateName.contains("Both");
    }

    private TerrainType toDomainTerrain(ETerrain terrain) {
        switch (terrain) {
            case Grass:
                return TerrainType.GRASS;

            case Mountain:
                return TerrainType.MOUNTAIN;

            case Water:
                return TerrainType.WATER;

            default:
                throw new IllegalArgumentException("Unsupported terrain type: " + terrain);
        }
    }
}