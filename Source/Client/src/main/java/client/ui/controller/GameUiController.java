package client.ui.controller;

import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.ui.model.GameUiModel;
import client.ui.model.GameUiSnapshot;
import client.ui.view.CliGameView;

public class GameUiController {
    private final GameUiModel gameUiModel;
    private final CliGameView cliGameView;

    public GameUiController(GameUiModel gameUiModel, CliGameView cliGameView) {
        this.gameUiModel = gameUiModel;
        this.cliGameView = cliGameView;
    }

    public void update(GameSnapshot gameSnapshot,
                       boolean treasureCollected,
                       boolean gameWon,
                       boolean gameLost,
                       int turnCounter) {
        GameUiSnapshot snapshot = new GameUiSnapshot(
                gameSnapshot.getFullGameMap(),
                gameSnapshot.getFullGameMap().findMyPlayerPosition(),
                findEnemyPlayerPosition(gameSnapshot),
                treasureCollected,
                gameWon,
                gameLost,
                turnCounter
        );

        gameUiModel.update(snapshot);
        cliGameView.render(gameUiModel.getCurrentSnapshot());
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