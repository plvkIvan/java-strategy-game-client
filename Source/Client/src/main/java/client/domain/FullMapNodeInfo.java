package client.domain;

public class FullMapNodeInfo {
    private final Position position;
    private final TerrainType terrainType;
    private final boolean myPlayerPresent;
    private final boolean enemyPlayerPresent;
    private final boolean myTreasurePresent;
    private final boolean myFortPresent;
    private final boolean enemyFortPresent;

    public FullMapNodeInfo(Position position,
                           TerrainType terrainType,
                           boolean myPlayerPresent,
                           boolean enemyPlayerPresent,
                           boolean myTreasurePresent,
                           boolean myFortPresent,
                           boolean enemyFortPresent) {
        this.position = position;
        this.terrainType = terrainType;
        this.myPlayerPresent = myPlayerPresent;
        this.enemyPlayerPresent = enemyPlayerPresent;
        this.myTreasurePresent = myTreasurePresent;
        this.myFortPresent = myFortPresent;
        this.enemyFortPresent = enemyFortPresent;
    }

    public Position getPosition() {
        return position;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public boolean isMyPlayerPresent() {
        return myPlayerPresent;
    }

    public boolean isEnemyPlayerPresent() {
        return enemyPlayerPresent;
    }

    public boolean isMyTreasurePresent() {
        return myTreasurePresent;
    }

    public boolean isMyFortPresent() {
        return myFortPresent;
    }

    public boolean isEnemyFortPresent() {
        return enemyFortPresent;
    }

    public boolean isWalkable() {
        return terrainType != TerrainType.WATER;
    }
}