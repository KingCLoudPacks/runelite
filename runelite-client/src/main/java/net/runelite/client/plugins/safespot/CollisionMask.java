package net.runelite.client.plugins.safespot;

/**
 * @author LoudPacks
 * <p>
 * A class that holds bit mask data for various collision flags affecting tile movement
 */
public final class CollisionMask {

    /**
     * Directional movement blocking flags.
     */
    public static final int BLOCK_MOVEMENT_NORTH_WEST = 0x1;
    public static final int BLOCK_MOVEMENT_NORTH = 0x2;
    public static final int BLOCK_MOVEMENT_NORTH_EAST = 0x4;
    public static final int BLOCK_MOVEMENT_EAST = 0x8;
    public static final int BLOCK_MOVEMENT_SOUTH_EAST = 0x10;
    public static final int BLOCK_MOVEMENT_SOUTH = 0x20;
    public static final int BLOCK_MOVEMENT_SOUTH_WEST = 0x40;
    public static final int BLOCK_MOVEMENT_WEST = 0x80;

    /**
     * Movement blocking type flags.
     */
    public static final int BLOCK_MOVEMENT_OBJECT = 0x100;
    public static final int BLOCK_MOVEMENT_FLOOR_DECORATION = 0x40000;
    public static final int BLOCK_MOVEMENT_FLOOR = 0x200000; // Eg. water
    public static final int BLOCK_MOVEMENT_CLOSED = 0xFFFFFF; //Eg. closed door
    public static final int BLOCK_MOVEMENT_FULL =
            BLOCK_MOVEMENT_OBJECT |
                    BLOCK_MOVEMENT_FLOOR_DECORATION |
                    BLOCK_MOVEMENT_FLOOR;

    /**
     * Directional line of sight blocking flags. Ex. walls
     */
    public static final int BLOCK_LINE_OF_SIGHT_NORTH = BLOCK_MOVEMENT_NORTH << 9; // 0x400
    public static final int BLOCK_LINE_OF_SIGHT_EAST = BLOCK_MOVEMENT_EAST << 9; // 0x1000
    public static final int BLOCK_LINE_OF_SIGHT_SOUTH = BLOCK_MOVEMENT_SOUTH << 9; // 0x4000
    public static final int BLOCK_LINE_OF_SIGHT_WEST = BLOCK_MOVEMENT_WEST << 9; // 0x10000
    public static final int BLOCK_LINE_OF_SIGHT_FULL = 0x20000;

    /**
     * @param collisionData collision data to check for mask
     * @param mask          mask to check against collision data
     * @return true if the collision data has the specified bit mask
     */
    public static boolean checkMask(int collisionData, int mask) {
        return (collisionData & mask) == mask;
    }

}
