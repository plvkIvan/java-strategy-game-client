package client.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FullGameMap {
    private final Map<Position, FullMapNodeInfo> mapNodes = new HashMap<>();

    public void addMapNode(FullMapNodeInfo mapNode) {
        mapNodes.put(mapNode.getPosition(), mapNode);
    }

    public FullMapNodeInfo getMapNode(Position position) {
        return mapNodes.get(position);
    }

    public Collection<FullMapNodeInfo> getMapNodes() {
        return mapNodes.values();
    }

    public boolean contains(Position position) {
        return mapNodes.containsKey(position);
    }

    public int getWidth() {
        int maxX = 0;

        for (FullMapNodeInfo node : mapNodes.values()) {
            if (node.getPosition().getX() > maxX) {
                maxX = node.getPosition().getX();
            }
        }

        return maxX + 1;
    }

    public int getHeight() {
        int maxY = 0;

        for (FullMapNodeInfo node : mapNodes.values()) {
            if (node.getPosition().getY() > maxY) {
                maxY = node.getPosition().getY();
            }
        }

        return maxY + 1;
    }

    public Position findMyPlayerPosition() {
        for (FullMapNodeInfo node : mapNodes.values()) {
            if (node.isMyPlayerPresent()) {
                return node.getPosition();
            }
        }

        return null;
    }
}