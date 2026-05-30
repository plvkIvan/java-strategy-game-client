package client.ui.view;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import client.domain.FullGameMap;
import client.domain.FullMapNodeInfo;
import client.domain.Position;
import client.domain.TerrainType;
import client.ui.model.GameUiSnapshot;
import org.junit.jupiter.api.Test;

class CliGameViewTest {

    @Test
    void update_whenGameIsRunning_printsRunningGameState() {
        CliGameView view = new CliGameView(
                new MapRenderer(),
                new LegendRenderer()
        );

        String output = captureOutput(() -> view.update(createSnapshot(false, false)));

        assertTrue(output.contains("GAME STATE"));
        assertTrue(output.contains("Turn: 1"));
        assertTrue(output.contains("Treasure collected: no"));
        assertTrue(output.contains("Result: running"));
    }

    @Test
    void update_whenGameIsWon_printsWonResult() {
        CliGameView view = new CliGameView(
                new MapRenderer(),
                new LegendRenderer()
        );

        String output = captureOutput(() -> view.update(createSnapshot(true, false)));

        assertTrue(output.contains("Result: WON"));
    }

    @Test
    void update_whenGameIsLost_printsLostResult() {
        CliGameView view = new CliGameView(
                new MapRenderer(),
                new LegendRenderer()
        );

        String output = captureOutput(() -> view.update(createSnapshot(false, true)));

        assertTrue(output.contains("Result: LOST"));
    }

    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8));
            action.run();
            return outputStream.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOut);
        }
    }

    private GameUiSnapshot createSnapshot(boolean won, boolean lost) {
        FullGameMap map = new FullGameMap();

        map.addMapNode(new FullMapNodeInfo(
                new Position(0, 0),
                TerrainType.GRASS,
                true,
                false,
                false,
                false,
                false
        ));

        return new GameUiSnapshot(
                map,
                new Position(0, 0),
                null,
                null,
                false,
                won,
                lost,
                1
        );
    }
}