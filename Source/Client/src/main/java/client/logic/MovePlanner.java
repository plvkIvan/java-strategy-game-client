package client.logic;

import java.util.List;

import client.domain.GameSnapshot;
import client.domain.MoveDirection;
import client.domain.Position;
import client.exception.InvalidGameStateException;
import client.exception.NoReachablePathException;

public class MovePlanner {
    private final PathFinder pathFinder;

    public MovePlanner(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    public MoveDirection planNextMove(GameSnapshot gameSnapshot, Position current, Position target) {
        List<Position> path = pathFinder.findPath(gameSnapshot, current, target);

        if (path.isEmpty()) {
            throw new NoReachablePathException(
                    "No reachable path from " + current + " to " + target + "."
            );
        }

        if (path.size() < 2) {
            throw new InvalidGameStateException(
                    "Path from " + current + " to " + target + " does not contain a next step."
            );
        }

        Position nextPosition = path.get(1);
        return determineMove(current, nextPosition);
    }

    private MoveDirection determineMove(Position from, Position to) {
        if (to.getX() == from.getX() && to.getY() == from.getY() - 1) {
            return MoveDirection.UP;
        }

        if (to.getX() == from.getX() && to.getY() == from.getY() + 1) {
            return MoveDirection.DOWN;
        }

        if (to.getX() == from.getX() - 1 && to.getY() == from.getY()) {
            return MoveDirection.LEFT;
        }

        if (to.getX() == from.getX() + 1 && to.getY() == from.getY()) {
            return MoveDirection.RIGHT;
        }

        throw new InvalidGameStateException("Positions are not adjacent: " + from + " and " + to + ".");
    }
}