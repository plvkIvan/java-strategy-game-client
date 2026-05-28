package client.ui.view;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LegendRendererTest {

    @Test
    void render_containsAllImportantLegendEntries() {
        LegendRenderer renderer = new LegendRenderer();

        String legend = renderer.render();

        assertTrue(legend.contains("You"));
        assertTrue(legend.contains("Enemy"));
        assertTrue(legend.contains("Both players"));
        assertTrue(legend.contains("Treasure"));
        assertTrue(legend.contains("Treasure collected"));
        assertTrue(legend.contains("Your castle"));
        assertTrue(legend.contains("Enemy castle"));
        assertTrue(legend.contains("Grass"));
        assertTrue(legend.contains("Mountain"));
        assertTrue(legend.contains("Water"));
    }

    @Test
    void render_containsUtf8Emojis() {
        LegendRenderer renderer = new LegendRenderer();

        String legend = renderer.render();

        assertTrue(legend.contains("🤖"));
        assertTrue(legend.contains("👾"));
        assertTrue(legend.contains("⚔️"));
        assertTrue(legend.contains("💰"));
        assertTrue(legend.contains("✅"));
        assertTrue(legend.contains("🏰"));
        assertTrue(legend.contains("🏯"));
        assertTrue(legend.contains("🌱"));
        assertTrue(legend.contains("⛰️"));
        assertTrue(legend.contains("🌊"));
    }
}