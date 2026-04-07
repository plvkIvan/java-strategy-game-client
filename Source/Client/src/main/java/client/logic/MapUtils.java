package client.logic;

import java.util.ArrayList;
import java.util.List;

import client.domain.Position;

public final class MapUtils {
    private MapUtils() {
    }

    public static List<Position> getNeighbors(Position position, int width, int height) {
        List<Position> neighbors = new ArrayList<>();
        addIfInside(neighbors, position.getX(), position.getY() - 1, width, height);
        addIfInside(neighbors, position.getX(), position.getY() + 1, width, height);
        addIfInside(neighbors, position.getX() - 1, position.getY(), width, height);
        addIfInside(neighbors, position.getX() + 1, position.getY(), width, height);

        return neighbors;
    }

    public static boolean isInside(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public static void addIfInside(List<Position> positions, int x, int y, int width, int height) {
        if (isInside(x, y, width, height)) {
            positions.add(new Position(x, y));
        }
    }
}