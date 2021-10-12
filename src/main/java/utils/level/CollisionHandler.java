package utils.level;

import entities.Door;
import entities.GameObject;
import interfaces.ICollidable;
import level.Level;
import utils.math.*;

public class CollisionHandler {

    public static int BLOCKED = 1;
    public static int FREE = 0;

    private MapIntersectionFinder interFinder;
    private Level level;


    public CollisionHandler(Level level) {
        this.level = level;
        this.interFinder = new MapIntersectionFinder(level);
    }


    public Intersection findMapCollision(Ray ray) {
        return this.interFinder.performDDA(ray, ray.getAngle(), null);
    }

    public boolean isTileFree(double x, double y) {
        return FREE == this.level.getMapInfo().getCollTileAt((int)x, (int)y);
    }


    public Door getDoorAt(int x, int y) {
        return level.getDoor(x, y);
    }

    public void setWorldCollision(Vector2d position, int collType) {
        this.level.getMapInfo().setCollTileAt((int)position.x, (int)position.y, collType);
    }


    public ICollidable findCollision(Ray ray) {
        return findCollision(ray, 10000000);
    }

    public ICollidable findCollision(Ray ray, double maxDist) {
        var collidables = level.getCollidables();

        for(var entity: collidables) {
            Hit h = entity.intersects(ray);
            if(h != null && maxDist >= h.distFromSrc) {
                return entity;
            }
        }

        return null;
    }
}
