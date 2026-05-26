package client.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class PositionTest {

    @Test
    void equalsAndHashCode_sameCoordinates_areEqual() {
        Position first = new Position(3, 4);
        Position second = new Position(3, 4);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equals_differentCoordinates_areNotEqual() {
        Position first = new Position(3, 4);
        Position second = new Position(4, 3);

        assertNotEquals(first, second);
    }

    @Test
    void toString_returnsReadableCoordinates() {
        Position position = new Position(3, 4);

        assertEquals("(3,4)", position.toString());
    }
}