package client.ui.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import client.domain.FullGameMap;
import client.ui.observer.Observer;
import org.junit.jupiter.api.Test;

class GameUiModelMockitoTest {

    @Test
    void update_notifiesObserverWithMockito() {
        GameUiModel model = new GameUiModel();
        Observer<GameUiSnapshot> observer = createObserverMock();
        GameUiSnapshot snapshot = createSnapshot();

        model.addGameStateObserver(observer);

        model.update(snapshot);

        verify(observer).update(snapshot);
    }

    @Test
    void update_doesNotNotifyRemovedObserverWithMockito() {
        GameUiModel model = new GameUiModel();
        Observer<GameUiSnapshot> observer = createObserverMock();
        GameUiSnapshot snapshot = createSnapshot();

        model.addGameStateObserver(observer);
        model.removeGameStateObserver(observer);

        model.update(snapshot);

        verify(observer, never()).update(snapshot);
    }

    @SuppressWarnings("unchecked")
    private Observer<GameUiSnapshot> createObserverMock() {
        return mock(Observer.class);
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