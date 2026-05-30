package client.ui.model;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import client.domain.FullGameMap;
import org.junit.jupiter.api.Test;

class GameUiModelTest {

    @Test
    void getCurrentSnapshot_beforeUpdate_returnsNull() {
        GameUiModel model = new GameUiModel();

        assertNull(model.getCurrentSnapshot());
    }

    @Test
    void update_storesCurrentSnapshot() {
        GameUiModel model = new GameUiModel();
        GameUiSnapshot snapshot = createSnapshot();

        model.update(snapshot);

        assertSame(snapshot, model.getCurrentSnapshot());
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