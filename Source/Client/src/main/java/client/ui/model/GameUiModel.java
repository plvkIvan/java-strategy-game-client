package client.ui.model;

public class GameUiModel {
    private GameUiSnapshot currentSnapshot;

    public void update(GameUiSnapshot snapshot) {
        this.currentSnapshot = snapshot;
    }

    public GameUiSnapshot getCurrentSnapshot() {
        return currentSnapshot;
    }
}