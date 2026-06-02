package client.domain;

public final class GameMapConstants {
    public static final int HALF_MAP_WIDTH = 10;
    public static final int HALF_MAP_HEIGHT = 5;
    public static final int HALF_MAP_SIZE = HALF_MAP_WIDTH * HALF_MAP_HEIGHT;

    public static final int NUMBER_OF_HALF_MAPS = 2;
    public static final int FULL_MAP_SIZE = HALF_MAP_SIZE * NUMBER_OF_HALF_MAPS;

    public static final int FIRST_LEFT_HALF_X = 0;
    public static final int LAST_LEFT_HALF_X = HALF_MAP_WIDTH - 1;
    public static final int FIRST_RIGHT_HALF_X = HALF_MAP_WIDTH;

    public static final int FIRST_TOP_HALF_Y = 0;
    public static final int LAST_TOP_HALF_Y = HALF_MAP_HEIGHT - 1;
    public static final int FIRST_BOTTOM_HALF_Y = HALF_MAP_HEIGHT;

    public static final int CASTLE_ZONE_WIDTH = 5;
    public static final int CASTLE_ZONE_HEIGHT = 3;

    public static final int CASTLE_LEFT_MIN_X = FIRST_LEFT_HALF_X;
    public static final int CASTLE_LEFT_MAX_X = CASTLE_ZONE_WIDTH - 1;
    public static final int CASTLE_RIGHT_MIN_X = HALF_MAP_WIDTH - CASTLE_ZONE_WIDTH;
    public static final int CASTLE_RIGHT_MAX_X = LAST_LEFT_HALF_X;

    public static final int CASTLE_TOP_MIN_Y = FIRST_TOP_HALF_Y;
    public static final int CASTLE_TOP_MAX_Y = CASTLE_ZONE_HEIGHT - 1;
    public static final int CASTLE_BOTTOM_MIN_Y = HALF_MAP_HEIGHT - CASTLE_ZONE_HEIGHT;
    public static final int CASTLE_BOTTOM_MAX_Y = LAST_TOP_HALF_Y;

    public static final int MIN_MOUNTAIN_FIELDS = 8;
    public static final int MIN_WATER_FIELDS = 7;
    public static final int MIN_GRASS_FIELDS = 24;

    public static final int MIN_WALKABLE_ROW_FIELDS = 6;
    public static final int MIN_WALKABLE_COLUMN_FIELDS = 3;

    private GameMapConstants() {
    }
}