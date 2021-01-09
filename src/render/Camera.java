package render;

import entities.Door;
import entities.GameObject;
import interfaces.IKeyResponsive;
import utils.level.CollisionHandler;
import utils.level.LevelUtils;
import utils.math.Intersection;
import utils.math.Ray;
import utils.math.Vector2d;

import java.awt.event.KeyEvent;

public class Camera {
    public Vector2d direction;
    public Vector2d plane;
    public Vector2d position;
    public double distFromPlane;
    //TODO: create player and this go into player
    private CollisionHandler collisionHandler;

    public Camera(Vector2d dir, Vector2d plane, Vector2d position) {
        this.direction = dir;
        this.plane = plane;
        this.position = position;
    }

    public Camera() {
        this.direction = new Vector2d(0,0);
        this.plane = new Vector2d(0,0);
        this.position = new Vector2d(0,0);
    }

    public Camera(double x, double y, double dirX, double dirY, double planeX, double planeY) {
        this.direction = new Vector2d(dirX,dirY);
        this.plane = new Vector2d(planeX,planeY);
        this.position = new Vector2d(x,y);
        this.distFromPlane = 1.0;
    }

    public double getAngle() {
        double angle = Math.atan2(direction.y, direction.x);
        angle = (angle > 0.0 ? angle : (2.0f * Math.PI + angle));
        return angle;
    }

}
