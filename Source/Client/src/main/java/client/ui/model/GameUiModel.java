package client.ui.model;

import client.ui.observer.EventPublisher;
import client.ui.observer.Observer;

public class GameUiModel {
    private final EventPublisher<GameUiSnapshot> gameStateEvents = new EventPublisher<>();
    private GameUiSnapshot currentSnapshot;

    public void addGameStateObserver(Observer<GameUiSnapshot> observer) {
        gameStateEvents.addObserver(observer);
    }

    public void removeGameStateObserver(Observer<GameUiSnapshot> observer) {
        gameStateEvents.removeObserver(observer);
    }

    public void update(GameUiSnapshot snapshot) {
        this.currentSnapshot = snapshot;
        gameStateEvents.notifyObservers(snapshot);
    }

    public GameUiSnapshot getCurrentSnapshot() {
        return currentSnapshot;
    }
}