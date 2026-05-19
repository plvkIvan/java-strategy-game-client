package client.ui.model;

import client.domain.FullGameMap;
import client.domain.Position;

public class GameUiSnapshot {
    private final FullGameMap fullGameMap;
    private final Position myPosition;
    private final Position enemyPosition;
    private final Position treasurePosition;
    private final boolean treasureCollected;
    private final boolean gameWon;
    private final boolean gameLost;
    private final int turnCounter;

    public GameUiSnapshot(FullGameMap fullGameMap,
                          Position myPosition,
                          Position enemyPosition,
                          Position treasurePosition,
                          boolean treasureCollected,
                          boolean gameWon,
                          boolean gameLost,
                          int turnCounter) {
        this.fullGameMap = fullGameMap;
        this.myPosition = myPosition;
        this.enemyPosition = enemyPosition;
        this.treasurePosition = treasurePosition;
        this.treasureCollected = treasureCollected;
        this.gameWon = gameWon;
        this.gameLost = gameLost;
        this.turnCounter = turnCounter;
    }

    public FullGameMap getFullGameMap() {
        return fullGameMap;
    }

    public Position getMyPosition() {
        return myPosition;
    }

    public Position getEnemyPosition() {
        return enemyPosition;
    }

    public Position getTreasurePosition() {
        return treasurePosition;
    }

    public boolean isTreasureCollected() {
        return treasureCollected;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public boolean isGameLost() {
        return gameLost;
    }

    public int getTurnCounter() {
        return turnCounter;
    }
}