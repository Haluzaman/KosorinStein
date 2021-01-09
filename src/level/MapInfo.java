package level;

import utils.level.CollisionHandler;

public class MapInfo {

    private Tile[] tileMap;
    private int[] collisionMap;
    private int width;
    private int height;

    public MapInfo(int width, int height, Tile[] map) {
        this.tileMap = map;
        this.width = width;
        this.height = height;
        setupCollisionMap();
    }

    private void setupCollisionMap() {
        this.collisionMap = new int[tileMap.length];

        for(int i = 0; i < this.collisionMap.length; i++) {
            if(tileMap[i].type == Tile.FREE_TYPE) {
                collisionMap[i] = CollisionHandler.FREE;
            } else {
                collisionMap[i] = CollisionHandler.BLOCKED;
            }
        }
    }

    public Tile[] getTileMap() { return this.tileMap; }
    public int[] getCollisionMap() { return this.collisionMap; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }

    public Tile getTileAt(int x, int y) {
        int index = y * width + x;
        if(index >= 0 && index < tileMap.length) return tileMap[index];
        else {
            return tileMap[0];
        }
    }

    public int getCollTileAt(int x, int y) {
        int index = y * width + x;

        if(index >= 0 && index < collisionMap.length) return collisionMap[index];
        else return '\n';
    }

    public void setCollTileAt(int x, int y, int collType) {
        int index = y * width + x;
        if(index >= 0 && index < collisionMap.length)
            collisionMap[index] = collType;
    }

}
