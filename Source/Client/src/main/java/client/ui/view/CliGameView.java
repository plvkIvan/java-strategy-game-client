package client.ui.view;

import client.ui.model.GameUiSnapshot;
import client.ui.observer.Observer;

public class CliGameView implements Observer<GameUiSnapshot> {
    private final MapRenderer mapRenderer;
    private final LegendRenderer legendRenderer;
    private boolean legendPrinted = false;

    public CliGameView(MapRenderer mapRenderer, LegendRenderer legendRenderer) {
        this.mapRenderer = mapRenderer;
        this.legendRenderer = legendRenderer;
    }

    @Override
    public void update(GameUiSnapshot snapshot) {
        render(snapshot);
    }

    private void render(GameUiSnapshot snapshot) {
        System.out.println();
        System.out.println("========== GAME STATE ==========");
        System.out.println("Turn: " + snapshot.getTurnCounter());
        System.out.println("Treasure collected: " + renderBoolean(snapshot.isTreasureCollected()));

        if (snapshot.isGameWon()) {
            System.out.println("Result: WON");
        } else if (snapshot.isGameLost()) {
            System.out.println("Result: LOST");
        } else {
            System.out.println("Result: running");
        }

        System.out.println();
        System.out.print(mapRenderer.render(snapshot));

        if (!legendPrinted) {
            System.out.println(legendRenderer.render());
            legendPrinted = true;
        }

        System.out.println("================================");
        System.out.println();
    }

    private String renderBoolean(boolean value) {
        if (value) {
            return "yes";
        }

        return "no";
    }
}