package client.domain;

public class MapNode {
    private final Position position;
    private final TerrainType terrainType;
    private final boolean hasCastle;

    public Tile(Position position, TerrainType terrainType, boolean hasCastle) {
        this.position = position;
        this.terrainType = terrainType;
        this.hasCastle = hasCastle;
    }

    public Position getPosition() {
        return position;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public boolean hasCastle() {
        return hasCastle;
    }

    public boolean isWalkable() {
        if(terrain!=TerrainType.WATER) {
            return true;
        }
        return false;
    }
}