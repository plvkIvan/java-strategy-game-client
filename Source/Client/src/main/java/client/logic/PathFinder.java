package client.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import client.domain.Position;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;

public class PathFinder {
    private static final int MAP_WIDTH = 20;
    private static final int MAP_HEIGHT = 10;

    public List<Position> findPath(GameState gameState, Position start, Position target) {
        if (start == null || target == null) {
            return List.of();
        }
        if (start.equals(target)) {
            return List.of(start);
        }

        Map<Position, FullMapNode> nodeMap = buildNodeMap(gameState);
        if (!nodeMap.containsKey(start) || !nodeMap.containsKey(target)) {
            return List.of();
        }

        DijkstraResult result = runDijkstra(nodeMap, start, target);
        return buildPath(result.previous, start, target);
    }

    public Position findNearestReachableTarget(GameState gameState, Position start, Set<Position> candidates) {
        Map<Position, FullMapNode> nodeMap = buildNodeMap(gameState);
        Position bestTarget = null;
        int bestCost = Integer.MAX_VALUE;

        for (Position candidate : candidates) {
            int cost = findPathCost(nodeMap, start, candidate);
            if (cost >= 0 && cost < bestCost) {
                bestCost = cost;
                bestTarget = candidate;
            }
        }

        return bestTarget;
    }

    private int findPathCost(Map<Position, FullMapNode> nodeMap, Position start, Position target) {
        if (start == null || target == null) {
            return -1;
        }
        if (start.equals(target)) {
            return 0;
        }
        if (!nodeMap.containsKey(start) || !nodeMap.containsKey(target)) {
            return -1;
        }

        DijkstraResult result = runDijkstra(nodeMap, start, target);
        Integer cost = result.distances.get(target);
        return cost != null ? cost : -1;
    }

    private DijkstraResult runDijkstra(Map<Position, FullMapNode> nodeMap, Position start, Position target) {
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
            if (current.equals(target)) {
                break;
            }

            FullMapNode currentMapNode = nodeMap.get(current);
            if (currentMapNode == null || !isWalkable(currentMapNode)) {
                continue;
            }

            for (Position neighbor : MapUtils.getNeighbors(current, MAP_WIDTH, MAP_HEIGHT)) {
                FullMapNode neighborMapNode = nodeMap.get(neighbor);
                if (neighborMapNode == null || !isWalkable(neighborMapNode)) {
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

    private Map<Position, FullMapNode> buildNodeMap(GameState gameState) {
        Map<Position, FullMapNode> nodeMap = new HashMap<>();
        for (FullMapNode node : gameState.getMap().getMapNodes()) {
            nodeMap.put(new Position(node.getX(), node.getY()), node);
        }
        return nodeMap;
    }

    private boolean isWalkable(FullMapNode node) {
        return node.getTerrain() != ETerrain.Water;
    }

    private int getMoveCost(FullMapNode from, FullMapNode to) {
        return getTerrainCost(from.getTerrain()) + getTerrainCost(to.getTerrain());
    }

    private int getTerrainCost(ETerrain terrain) {
        return terrain == ETerrain.Mountain ? 2 : 1;
    }

    private List<Position> buildPath(Map<Position, Position> previous, Position start, Position target) {
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

        private DijkstraResult(Map<Position, Integer> distances, Map<Position, Position> previous) {
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