package client.ui.model;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.concurrent.atomic.AtomicReference;

import client.domain.FullGameMap;
import client.ui.observer.Observer;
import org.junit.jupiter.api.Test;

class GameUiModelTest {

    @Test
    void update_storesCurrentSnapshot() {
        GameUiModel model = new GameUiModel();
        GameUiSnapshot snapshot = createSnapshot();

        model.update(snapshot);

        assertSame(snapshot, model.getCurrentSnapshot());
    }

    @Test
    void update_notifiesRegisteredObserver() {
        GameUiModel model = new GameUiModel();
        GameUiSnapshot snapshot = createSnapshot();
        AtomicReference<GameUiSnapshot> receivedSnapshot = new AtomicReference<>();

        model.addGameStateObserver(receivedSnapshot::set);

        model.update(snapshot);

        assertSame(snapshot, receivedSnapshot.get());
    }

    @Test
    void removeGameStateObserver_removedObserverIsNotNotified() {
        GameUiModel model = new GameUiModel();
        GameUiSnapshot snapshot = createSnapshot();
        AtomicReference<GameUiSnapshot> receivedSnapshot = new AtomicReference<>();

        Observer<GameUiSnapshot> observer = receivedSnapshot::set;

        model.addGameStateObserver(observer);
        model.removeGameStateObserver(observer);

        model.update(snapshot);

        assertNull(receivedSnapshot.get());
    }

    private GameUiSnapshot createSnapshot() {
        return new GameUiSnapshot(
                new FullGameMap(),
                null,
                null,
                null,
                false,
                false,
                false,
                1
        );
    }
}