package client.logic.search;

import java.util.HashSet;
import java.util.Set;

import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.domain.TerrainType;
import client.logic.PathFinder;

public class TargetSearchService {
    private final TargetSearchStrategy targetSearchStrategy;

    public TargetSearchService(TargetSearchStrategy targetSearchStrategy) {
        this.targetSearchStrategy = targetSearchStrategy;
    }

    public Position chooseTarget(GameSnapshot gameSnapshot,
                                 Position currentPosition,
                                 Set<Position> visitedPositions,
                                 PathFinder pathFinder) {
        Position visibleTarget = targetSearchStrategy.findVisibleTarget(gameSnapshot);

        if (visibleTarget != null) {
            return visibleTarget;
        }

        Set<Position> candidates = new HashSet<>();
        Set<Position> preferredMountainCandidates = new HashSet<>();

        for (FullMapNodeInfo node : gameSnapshot.getFullGameMap().getMapNodes()) {
            Position position = node.getPosition();

            if (!targetSearchStrategy.isInSearchArea(position)) {
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
}