package client.logic;

import java.util.HashSet;
import java.util.Set;

import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.domain.TerrainType;

public class TreasureSearch {
    private final Set<Position> ownHalfPositions;

    public TreasureSearch(Set<Position> ownHalfPositions) {
        this.ownHalfPositions = ownHalfPositions;
    }

    public Position chooseTarget(GameSnapshot gameSnapshot,
                                 Position currentPosition,
                                 Set<Position> visitedPositions,
                                 PathFinder pathFinder) {
        Position visibleTreasure = findTreasureIfVisible(gameSnapshot);

        if (visibleTreasure != null) {
            return visibleTreasure;
        }

        Set<Position> candidates = new HashSet<>();
        Set<Position> preferredMountainCandidates = new HashSet<>();

        for (FullMapNodeInfo node : gameSnapshot.getFullGameMap().getMapNodes()) {
            Position position = node.getPosition();

            if (!ownHalfPositions.contains(position)) {
                continue;
            }

            if (visitedPositions.contains(position)) {
                continue;
            }

            if (!node.isWalkable()) {
                continue;
            }

            candidates.add(position);

            if (node.getTerrainType() == TerrainType.MOUNTAIN) {
                preferredMountainCandidates.add(position);
            }
        }

        Position bestTarget = pathFinder.findBestReachableTarget(
                gameSnapshot,
                currentPosition,
                candidates,
                preferredMountainCandidates
        );

        if (bestTarget != null) {
            return bestTarget;
        }

        return currentPosition;
    }

    private Position findTreasureIfVisible(GameSnapshot gameSnapshot) {
        for (FullMapNodeInfo node : gameSnapshot.getFullGameMap().getMapNodes()) {
            if (node.isMyTreasurePresent()) {
                return node.getPosition();
            }
        }

        return null;
    }
}