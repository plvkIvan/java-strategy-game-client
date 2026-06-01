package client.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import client.domain.FullGameMap;
import client.domain.FullMapNodeInfo;
import client.domain.GameSnapshot;
import client.domain.Position;
import client.domain.TerrainType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathFinder {
    private static final Logger logger = LoggerFactory.getLogger(PathFinder.class);

    private static final int PREFERRED_TARGET_MAX_EXTRA_COST = 6;
    private static final int DEFAULT_TERRAIN_COST = 1;
    private static final int MOUNTAIN_TERRAIN_COST = 2;

    public List<Position> findPath(GameSnapshot gameSnapshot, Position start, Position target) {
        if (start == null || target == null) {
            logger.warn("Cannot find path because start or target is null. Start: {}, target: {}.",
                    start,
                    target);
            return List.of();
        }

        if (start.equals(target)) {
            return List.of(start);
        }

        FullGameMap fullGameMap = gameSnapshot.getFullGameMap();

        if (!fullGameMap.contains(start) || !fullGameMap.contains(target)) {
            logger.warn("Cannot find path because start or target is not part of the map. Start: {}, target: {}.",
                    start,
                    target);
            return List.of();
        }

        DijkstraResult result = runDijkstra(fullGameMap, start, target);
        List<Position> path = buildPath(result.previous, start, target);

        if (path.isEmpty()) {
            logger.warn("No path found from {} to {}.", start, target);
        } else {
            logger.debug("Path from {} to {} found with {} positions.",
                    start,
                    target,
                    path.size());
        }

        return path;
    }

    public Position findNearestReachableTarget(GameSnapshot gameSnapshot,
                                               Position start,
                                               Set<Position> candidates) {
        if (start == null || candidates == null || candidates.isEmpty()) {
            logger.debug("No nearest target search possible. Start: {}, candidates available: {}.",
                    start,
                    candidates == null ? 0 : candidates.size());
            return null;
        }

        FullGameMap fullGameMap = gameSnapshot.getFullGameMap();

        if (!fullGameMap.contains(start)) {
            logger.warn("Start position {} is not part of the map.", start);
            return null;
        }

        DijkstraResult result = runDijkstra(fullGameMap, start, null);

        return findLowestCostTarget(result, candidates);
    }

    public Position findBestReachableTarget(GameSnapshot gameSnapshot,
                                            Position start,
                                            Set<Position> candidates,
                                            Set<Position> preferredCandidates) {
        if (start == null || candidates == null || candidates.isEmpty()) {
            logger.debug("No best target search possible. Start: {}, candidates available: {}.",
                    start,
                    candidates == null ? 0 : candidates.size());
            return null;
        }

        FullGameMap fullGameMap = gameSnapshot.getFullGameMap();

        if (!fullGameMap.contains(start)) {
            logger.warn("Start position {} is not part of the map.", start);
            return null;
        }

        DijkstraResult result = runDijkstra(fullGameMap, start, null);

        Position nearestTarget = findLowestCostTarget(result, candidates);
        Position preferredTarget = findLowestCostTarget(result, preferredCandidates);

        if (nearestTarget == null) {
            logger.debug("No normal target reachable. Preferred target selected: {}.", preferredTarget);
            return preferredTarget;
        }

        if (preferredTarget == null) {
            logger.debug("No preferred target reachable. Nearest target selected: {}.", nearestTarget);
            return nearestTarget;
        }

        int nearestCost = result.distances.getOrDefault(nearestTarget, Integer.MAX_VALUE);
        int preferredCost = result.distances.getOrDefault(preferredTarget, Integer.MAX_VALUE);

        if (preferredCost <= nearestCost + PREFERRED_TARGET_MAX_EXTRA_COST) {
            logger.debug("Preferred target {} selected with cost {}. Nearest target {} has cost {}.",
                    preferredTarget,
                    preferredCost,
                    nearestTarget,
                    nearestCost);
            return preferredTarget;
        }

        logger.debug("Nearest target {} selected with cost {}. Preferred target {} was too expensive with cost {}.",
                nearestTarget,
                nearestCost,
                preferredTarget,
                preferredCost);

        return nearestTarget;
    }

    private Position findLowestCostTarget(DijkstraResult result, Set<Position> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        Position bestTarget = null;
        int bestCost = Integer.MAX_VALUE;

        for (Position candidate : candidates) {
            Integer cost = result.distances.get(candidate);

            if (cost != null && cost < bestCost) {
                bestCost = cost;
                bestTarget = candidate;
            }
        }

        return bestTarget;
    }

    private DijkstraResult runDijkstra(FullGameMap fullGameMap,
                                       Position start,
                                       Position target) {
        Map<Position, Integer> distances = new HashMap<>();
        Map<Position, Position> previous = new HashMap<>();
        PriorityQueue<PathNode> queue = new PriorityQueue<>();

        distances.put(start, 0);
        queue.add(new PathNode(start, 0));

        while (!queue.isEmpty()) {
            PathNode currentNode = queue.poll();
            Position current = currentNode.position;

            if (currentNode.distance > distances.getOrDefault(current, Integer.MAX_VALUE)) {
                continue;
            }

            if (target != null && current.equals(target)) {
                break;
            }

            FullMapNodeInfo currentMapNode = fullGameMap.getMapNode(current);

            if (currentMapNode == null || !currentMapNode.isWalkable()) {
                continue;
            }

            for (Position neighbor : MapUtils.getNeighbors(
                    current,
                    fullGameMap.getWidth(),
                    fullGameMap.getHeight()
            )) {
                FullMapNodeInfo neighborMapNode = fullGameMap.getMapNode(neighbor);

                if (neighborMapNode == null || !neighborMapNode.isWalkable()) {
                    continue;
                }

                int edgeCost = getMoveCost(currentMapNode, neighborMapNode);
                int newDistance = distances.get(current) + edgeCost;

                if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    queue.add(new PathNode(neighbor, newDistance));
                }
            }
        }

        return new DijkstraResult(distances, previous);
    }

    private int getMoveCost(FullMapNodeInfo from, FullMapNodeInfo to) {
        return getTerrainCost(from.getTerrainType()) + getTerrainCost(to.getTerrainType());
    }

    private int getTerrainCost(TerrainType terrainType) {
        if (terrainType == TerrainType.MOUNTAIN) {
            return MOUNTAIN_TERRAIN_COST;
        }

        return DEFAULT_TERRAIN_COST;
    }

    private List<Position> buildPath(Map<Position, Position> previous,
                                     Position start,
                                     Position target) {
        List<Position> path = new ArrayList<>();
        Position current = target;

        while (current != null) {
            path.add(0, current);

            if (current.equals(start)) {
                break;
            }

            current = previous.get(current);
        }

        if (path.isEmpty() || !path.get(0).equals(start)) {
            return List.of();
        }

        return path;
    }

    private static class DijkstraResult {
        private final Map<Position, Integer> distances;
        private final Map<Position, Position> previous;

        private DijkstraResult(Map<Position, Integer> distances,
                               Map<Position, Position> previous) {
            this.distances = distances;
            this.previous = previous;
        }
    }

    private static class PathNode implements Comparable<PathNode> {
        private final Position position;
        private final int distance;

        private PathNode(Position position, int distance) {
            this.position = position;
            this.distance = distance;
        }

        @Override
        public int compareTo(PathNode other) {
            return Integer.compare(this.distance, other.distance);
        }
    }
}