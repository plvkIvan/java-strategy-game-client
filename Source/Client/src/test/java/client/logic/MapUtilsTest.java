package client.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import client.domain.Position;
import org.junit.jupiter.api.Test;

class MapUtilsTest {

    @Test
    void getNeighbors_middlePosition_returnsFourNeighbors() {
        Position position = new Position(5, 2);

        List<Position> neighbors = MapUtils.getNeighbors(position, 10, 5);

        assertEquals(4, neighbors.size());
        assertTrue(neighbors.contains(new Position(5, 1)));
        assertTrue(neighbors.contains(new Position(5, 3)));
        assertTrue(neighbors.contains(new Position(4, 2)));
        assertTrue(neighbors.contains(new Position(6, 2)));
    }

    @Test
    void getNeighbors_cornerPosition_returnsTwoNeighbors() {
        Position position = new Position(0, 0);

        List<Position> neighbors = MapUtils.getNeighbors(position, 10, 5);

        assertEquals(2, neighbors.size());
        assertTrue(neighbors.contains(new Position(0, 1)));
        assertTrue(neighbors.contains(new Position(1, 0)));
    }
}