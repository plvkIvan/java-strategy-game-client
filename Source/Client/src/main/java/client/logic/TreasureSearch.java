package client.logic;

import java.util.HashSet;
import java.util.Set;

import client.domain.Position;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;

public class TreasureSearch {
    private final Set<Position> ownHalfPositions;

    public TreasureSearch(Set<Position> ownHalfPositions) {
        this.ownHalfPositions = ownHalfPositions;
    }

    public Position chooseTarget(GameState gameState, Position currentPosition, Set<Position> visitedPositions, PathFinder pathFinder) {
        Position visibleTreasure = findTreasureIfVisible(gameState);

        if (visibleTreasure != null) {
            return visibleTreasure;
        }

        Set<Position> candidates = new HashSet<>();
        Set<Position> mountainCandidates = new HashSet<>();

        for (FullMapNode node : gameState.getMap().getMapNodes()) {
            Position position = new Position(node.getX(), node.getY());
            if (!ownHalfPositions.contains(position)) {
                continue;
            }

            if (visitedPositions.contains(position)) {
                continue;
            }

            if (node.getTerrain() == ETerrain.Water) {
                continue;
            }

            if (node.getTerrain() == ETerrain.Mountain) {
                mountainCandidates.add(position);
            }
            else {
                candidates.add(position);
            }
        }

        if(mountainCandidates.isEmpty()) {
            Position nearest = pathFinder.findNearestReachableTarget(gameState, currentPosition, mountainCandidates);
            if (nearest != null) {
                return nearest;
            }
        }

        Position nearest = pathFinder.findNearestReachableTarget(gameState, currentPosition, candidates);
        if (nearest != null) {
            return nearest;
        }

        return currentPosition;
    }

    private Position findTreasureIfVisible(GameState gameState) {
        for (FullMapNode node : gameState.getMap().getMapNodes()) {
            if (node.getTreasureState() == ETreasureState.MyTreasureIsPresent) {
                return new Position(node.getX(), node.getY());
            }
        }

        return null;
    }
}