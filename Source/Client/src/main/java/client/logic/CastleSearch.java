package client.logic;

import java.util.HashSet;
import java.util.Set;

import client.domain.Position;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;

public class CastleSearch {
    private final Set<Position> ownHalfPositions;

    public CastleSearch(Set<Position> ownHalfPositions) {
        this.ownHalfPositions = ownHalfPositions;
    }

    public Position chooseTarget(GameState gameState, Position currentPosition, Set<Position> visitedPositions, PathFinder pathFinder) {
        Position visibleEnemyCastle = findVisibleEnemyCastle(gameState);
        if (visibleEnemyCastle != null) {
            return visibleEnemyCastle;
        }

        Set<Position> candidates = new HashSet<>();

        for (FullMapNode node : gameState.getMap().getMapNodes()) {
            Position position = new Position(node.getX(), node.getY());
            if (ownHalfPositions.contains(position)) {
                continue;
            }

            if (visitedPositions.contains(position)) {
                continue;
            }

            if (node.getTerrain() == ETerrain.Water) {
                continue;
            }

            candidates.add(position);
        }

        Position nearest = pathFinder.findNearestReachableTarget(gameState, currentPosition, candidates);
        if (nearest != null) {
            return nearest;
        }

        return currentPosition;
    }

    private Position findVisibleEnemyCastle(GameState gameState) {
        for (FullMapNode node : gameState.getMap().getMapNodes()) {
            if (node.getFortState() == EFortState.EnemyFortPresent) {
                return new Position(node.getX(), node.getY());
            }
        }

        return null;
    }
}