package client.logic;

import java.util.List;

import client.domain.GameSnapshot;
import client.domain.Position;
import messagesbase.messagesfromclient.EMove;

public class MovePlanner {
    private final PathFinder pathFinder;

    public MovePlanner(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    public EMove planNextMove(GameSnapshot gameSnapshot, Position current, Position target) {
        List<Position> path = pathFinder.findPath(gameSnapshot, current, target);

        if (path.size() < 2) {
            return null;
        }

        Position nextPosition = path.get(1);
        return determineMove(current, nextPosition);
    }

    private EMove determineMove(Position from, Position to) {
        if (to.getX() == from.getX() && to.getY() == from.getY() - 1) {
            return EMove.Up;
        }

        if (to.getX() == from.getX() && to.getY() == from.getY() + 1) {
            return EMove.Down;
        }

        if (to.getX() == from.getX() - 1 && to.getY() == from.getY()) {
            return EMove.Left;
        }

        if (to.getX() == from.getX() + 1 && to.getY() == from.getY()) {
            return EMove.Right;
        }

        throw new IllegalStateException("Positions are not adjacent");
    }
}