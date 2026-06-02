package client.logic.search;

import java.util.List;
import java.util.Set;

import client.domain.FullGameMap;
import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.domain.TerrainType;
import client.logic.MapUtils;
import client.logic.PathFinder;

public class TargetSearchService {
    private static final int INFORMATION_GAIN_BONUS = 4;
    private static final int MOUNTAIN_TARGET_BONUS = 2;

    private static final int DEFAULT_TERRAIN_COST = 1;
    private static final int MOUNTAIN_TERRAIN_COST = 2;

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

        Position bestUnvisitedTarget = chooseBestTarget(
                gameSnapshot,
                currentPosition,
                visitedPositions,
                pathFinder,
                false
        );

        if (bestUnvisitedTarget != null) {
            return bestUnvisitedTarget;
        }

        Position bestFallbackTarget = chooseBestTarget(
                gameSnapshot,
                currentPosition,
                visitedPositions,
                pathFinder,
                true
        );

        if (bestFallbackTarget != null) {
            return bestFallbackTarget;
        }

        return currentPosition;
    }

    private Position chooseBestTarget(GameSnapshot gameSnapshot,
                                      Position currentPosition,
                                      Set<Position> visitedPositions,
                                      PathFinder pathFinder,
                                      boolean allowVisitedTargets) {
        FullGameMap fullGameMap = gameSnapshot.getFullGameMap();

        Position bestTarget = null;
        int bestScore = Integer.MAX_VALUE;
        int bestPathCost = Integer.MAX_VALUE;

        for (FullMapNodeInfo node : fullGameMap.getMapNodes()) {
            Position candidate = node.getPosition();

            if (!isCandidateRelevant(node, visitedPositions, allowVisitedTargets)) {
                continue;
            }

            List<Position> path = pathFinder.findPath(
                    gameSnapshot,
                    currentPosition,
                    candidate
            );

            if (path.isEmpty()) {
                continue;
            }

            int pathCost = calculatePathCost(fullGameMap, path);
            int informationGain = calculateInformationGain(
                    gameSnapshot,
                    node,
                    visitedPositions
            );

            int score = pathCost - informationGain * INFORMATION_GAIN_BONUS;

            if (node.getTerrainType() == TerrainType.MOUNTAIN) {
                score -= MOUNTAIN_TARGET_BONUS;
            }

            if (score < bestScore || score == bestScore && pathCost < bestPathCost) {
                bestScore = score;
                bestPathCost = pathCost;
                bestTarget = candidate;
            }
        }

        return bestTarget;
    }

    private boolean isCandidateRelevant(FullMapNodeInfo node,
                                        Set<Position> visitedPositions,
                                        boolean allowVisitedTargets) {
        Position position = node.getPosition();

        if (!targetSearchStrategy.isInSearchArea(position)) {
            return false;
        }

        if (!node.isWalkable()) {
            return false;
        }

        if (!allowVisitedTargets && isVisited(visitedPositions, position)) {
            return false;
        }

        return node.getTerrainType() == TerrainType.GRASS
                || node.getTerrainType() == TerrainType.MOUNTAIN;
    }

    private int calculateInformationGain(GameSnapshot gameSnapshot,
                                         FullMapNodeInfo targetNode,
                                         Set<Position> visitedPositions) {
        if (targetNode.getTerrainType() != TerrainType.MOUNTAIN) {
            if (isVisited(visitedPositions, targetNode.getPosition())) {
                return 0;
            }

            return 1;
        }

        int informationGain = 0;
        FullGameMap fullGameMap = gameSnapshot.getFullGameMap();
        Position mountainPosition = targetNode.getPosition();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int x = mountainPosition.getX() + dx;
                int y = mountainPosition.getY() + dy;

                if (!MapUtils.isInside(x, y, fullGameMap.getWidth(), fullGameMap.getHeight())) {
                    continue;
                }

                Position visiblePosition = new Position(x, y);
                FullMapNodeInfo visibleNode = fullGameMap.getMapNode(visiblePosition);

                if (visibleNode == null) {
                    continue;
                }

                if (!targetSearchStrategy.isInSearchArea(visiblePosition)) {
                    continue;
                }

                if (isVisited(visitedPositions, visiblePosition)) {
                    continue;
                }

                if (visibleNode.getTerrainType() == TerrainType.GRASS) {
                    informationGain++;
                }
            }
        }

        return informationGain;
    }

    private int calculatePathCost(FullGameMap fullGameMap, List<Position> path) {
        int pathCost = 0;

        for (int i = 1; i < path.size(); i++) {
            FullMapNodeInfo previousNode = fullGameMap.getMapNode(path.get(i - 1));
            FullMapNodeInfo currentNode = fullGameMap.getMapNode(path.get(i));

            pathCost += calculateTerrainCost(previousNode.getTerrainType());
            pathCost += calculateTerrainCost(currentNode.getTerrainType());
        }

        return pathCost;
    }

    private int calculateTerrainCost(TerrainType terrainType) {
        if (terrainType == TerrainType.MOUNTAIN) {
            return MOUNTAIN_TERRAIN_COST;
        }

        return DEFAULT_TERRAIN_COST;
    }

    private boolean isVisited(Set<Position> visitedPositions, Position position) {
        return visitedPositions != null && visitedPositions.contains(position);
    }
}