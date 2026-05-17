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

public class PathFinder {
    private static final int PREFERRED_TARGET_MAX_EXTRA_COST = 6;

    public List<Position> findPath(GameSnapshot gameSnapshot, Position start, Position target) {
        if (start == null || target == null) {
            return List.of();
        }

        if (start.equals(target)) {
            return List.of(start);
        }

        FullGameMap fullGameMap = gameSnapshot.getFullGameMap();

        if (!fullGameMap.contains(start) || !fullGameMap.contains(target)) {
            return List.of();
        }

        DijkstraResult result = runDijkstra(fullGameMap, start, target);

        return buildPath(result.previous, start, target);
    }

    public Position findNearestReachableTarget(GameSnapshot gameSnapshot,
                                               Position start,
                                               Set<Position> candidates) {
        if (start == null || candidates == null || candidates.isEmpty()) {
            return null;
        }

        FullGameMap fullGameMap = gameSnapshot.getFullGameMap();

        if (!fullGameMap.contains(start)) {
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
            return null;
        }

        FullGameMap fullGameMap = gameSnapshot.getFullGameMap();

        if (!fullGameMap.contains(start)) {
            return null;
        }

        DijkstraResult result = runDijkstra(fullGameMap, start, null);

        Position nearestTarget = findLowestCostTarget(result, candidates);
        Position preferredTarget = findLowestCostTarget(result, preferredCandidates);

        if (nearestTarget == null) {
            return preferredTarget;
        }

        if (preferredTarget == null) {
            return nearestTarget;
        }

        int nearestCost = result.distances.getOrDefault(nearestTarget, Integer.MAX_VALUE);
        int preferredCost = result.distances.getOrDefault(preferredTarget, Integer.MAX_VALUE);

        if (preferredCost <= nearestCost + PREFERRED_TARGET_MAX_EXTRA_COST) {
            return preferredTarget;
        }

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
            return 2;
        }

        return 1;
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