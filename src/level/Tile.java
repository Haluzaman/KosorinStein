package level;

import graphics.Texture;

public class Tile {

    public static int FREE_TYPE = 0;
    public static int SOLID_TYPE = 1;
    public static int HORIZ_DOOR = 2;
    public static int VERT_DOOR = 3;
    //means it has some entity under for example column, so can be used only as higher wall
    public static int PARTIAL = 4;
    public static int HORIZ_WINDOW = 5;

    public int x,y;
    public int type;
    public Texture wallTexture;
    public Texture floorTexture;
    public Texture ceilingTexture;

    //height is in screensize merit
    public double height;

    public Tile(int x, int y, double height, int type, Texture wallTexture, Texture floorTexture, Texture ceilingTexture) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.height = height;
        this.wallTexture = wallTexture;
        this.floorTexture = floorTexture;
        this.ceilingTexture = ceilingTexture;
    }


}
