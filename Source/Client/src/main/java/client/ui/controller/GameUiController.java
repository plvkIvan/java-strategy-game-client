package client.ui.controller;

import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.ui.model.GameUiModel;
import client.ui.model.GameUiSnapshot;

public class GameUiController {
    private final GameUiModel gameUiModel;

    public GameUiController(GameUiModel gameUiModel) {
        this.gameUiModel = gameUiModel;
    }

    public void update(GameSnapshot gameSnapshot,
                       Position treasurePosition,
                       boolean treasureCollected,
                       boolean gameWon,
                       boolean gameLost,
                       int turnCounter) {
        GameUiSnapshot snapshot = new GameUiSnapshot(
                gameSnapshot.getFullGameMap(),
                gameSnapshot.getFullGameMap().findMyPlayerPosition(),
                findEnemyPlayerPosition(gameSnapshot),
                treasurePosition,
                treasureCollected,
                gameWon,
                gameLost,
                turnCounter
        );

        gameUiModel.update(snapshot);
    }

    private Position findEnemyPlayerPosition(GameSnapshot gameSnapshot) {
        for (FullMapNodeInfo node : gameSnapshot.getFullGameMap().getMapNodes()) {
            if (node.isEnemyPlayerPresent()) {
                return node.getPosition();
            }
        }

        return null;
    }
}