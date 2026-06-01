package client.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FullGameMap {
    private final Map<Position, FullMapNodeInfo> mapNodes = new HashMap<>();

    private int width = 0;
    private int height = 0;

    public void addMapNode(FullMapNodeInfo mapNode) {
        Position position = mapNode.getPosition();

        mapNodes.put(position, mapNode);

        width = Math.max(width, position.getX() + 1);
        height = Math.max(height, position.getY() + 1);
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
        return width;
    }

    public int getHeight() {
        return height;
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