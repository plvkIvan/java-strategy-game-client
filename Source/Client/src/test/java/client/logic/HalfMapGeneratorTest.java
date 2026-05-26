package client.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import client.domain.GameHalfMap;
import client.domain.TerrainType;
import client.logic.mapgeneration.CastlePlacementTarget;
import org.junit.jupiter.api.Test;

class HalfMapGeneratorTest {

    @Test
    void generate_returnsValidHalfMapWithOneCastle() {
        HalfMapGenerator generator = new HalfMapGenerator();

        GameHalfMap map = generator.generate(CastlePlacementTarget.ANY);

        assertEquals(50, map.size());
        assertEquals(1, map.countCastle());
        assertTrue(map.countTerrainTypes(TerrainType.MOUNTAIN) >= 5);
        assertTrue(map.countTerrainTypes(TerrainType.WATER) >= 7);
        assertTrue(map.countTerrainTypes(TerrainType.GRASS) >= 24);
        assertTrue(map.countWalkableRow(0) >= 6);
        assertTrue(map.countWalkableRow(4) >= 6);
        assertTrue(map.countWalkableColumn(0) >= 3);
        assertTrue(map.countWalkableColumn(9) >= 3);
    }
}