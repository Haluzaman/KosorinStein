package utils.math;

import level.Tile;

public class Intersection {

    public Tile t;
    public Vector2d position;
    public double distToWall;
    public boolean isVertical;
    public int wallType;

    public Intersection() {
        t = null;
        position = new Vector2d();
    }

}
