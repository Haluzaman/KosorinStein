package utils.math;

import  level.Tile;

public class Intersection {

    public static final int UP_SIDE = 0;
    public static final int RIGHT_SIDE = 1;
    public static final int DOWN_SIDE = 2;
    public static final int LEFT_SIDE = 3;

    public Tile t;
    public Vector2d position;
    public double distToWall;
    public boolean isVertical;
    public int wallType;
    public int wallCollisionSide;

    public Intersection() {
        t = null;
        position = new Vector2d();
    }

}
