package client.domain;

import java.util.Objects;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Position position = (Position) object;
        return x == position.x && y == position.y;
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), x, y);
    }
}