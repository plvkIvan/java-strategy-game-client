package client.logic;

import java.util.HashSet;
import java.util.Set;

import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.domain.TerrainType;

public class CastleSearch {
    private final Set<Position> ownHalfPositions;

    public CastleSearch(Set<Position> ownHalfPositions) {
        this.ownHalfPositions = ownHalfPositions;
    }

    public Position chooseTarget(GameSnapshot gameSnapshot,
                                 Position currentPosition,
                                 Set<Position> visitedPositions,
                                 PathFinder pathFinder) {
        Position visibleEnemyCastle = findVisibleEnemyCastle(gameSnapshot);

        if (visibleEnemyCastle != null) {
            return visibleEnemyCastle;
        }

        Set<Position> candidates = new HashSet<>();
        Set<Position> preferredMountainCandidates = new HashSet<>();

        for (FullMapNodeInfo node : gameSnapshot.getFullGameMap().getMapNodes()) {
            Position position = node.getPosition();

            if (ownHalfPositions.contains(position)) {
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

    private Position findVisibleEnemyCastle(GameSnapshot gameSnapshot) {
        for (FullMapNodeInfo node : gameSnapshot.getFullGameMap().getMapNodes()) {
            if (node.isEnemyFortPresent()) {
                return node.getPosition();
            }
        }

        return null;
    }
}