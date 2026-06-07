package client.ui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import client.ui.observer.Observer;
import org.junit.jupiter.api.Test;

class GameUiModelMockitoTest {

    @Test
    void update_notifiesObserverWithMockito() {
        GameUiModel model = new GameUiModel();
        Observer<GameUiSnapshot> observer = createObserverMock();
        GameUiSnapshot snapshot = createSnapshotMockWithTurnCounter(1);

        model.addGameStateObserver(observer);

        model.update(snapshot);

        verify(observer).update(snapshot);
        assertEquals(1, model.getCurrentSnapshot().getTurnCounter());
    }

    @Test
    void update_doesNotNotifyRemovedObserverWithMockito() {
        GameUiModel model = new GameUiModel();
        Observer<GameUiSnapshot> observer = createObserverMock();
        GameUiSnapshot snapshot = createSnapshotMockWithTurnCounter(2);

        model.addGameStateObserver(observer);
        model.removeGameStateObserver(observer);

        model.update(snapshot);

        verify(observer, never()).update(snapshot);
        assertEquals(2, model.getCurrentSnapshot().getTurnCounter());
    }

    @SuppressWarnings("unchecked")
    private Observer<GameUiSnapshot> createObserverMock() {
        return mock(Observer.class);
    }

    private GameUiSnapshot createSnapshotMockWithTurnCounter(int turnCounter) {
        GameUiSnapshot snapshot = mock(GameUiSnapshot.class);
        when(snapshot.getTurnCounter()).thenReturn(turnCounter);
        return snapshot;
    }
}