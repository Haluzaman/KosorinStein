package  entities;

import  graphics.Texture;
import  interfaces.IUpdatable;
import  level.Level;
import  utils.level.CollisionHandler;
import  utils.math.Vector2d;

public class Door extends GameObject implements IUpdatable {

    public enum DOOR_STATE {
        closed,
        opening,
        closing,
        opened
    }

    private static int UPDATE_TIME = 50;
    private static int MAX_OPENED_TIME = 10000;

    private Vector2d leftPoint;
    private Vector2d rightPoint;
    private double offset;
    private Texture texture;
    private DOOR_STATE state;

    private CollisionHandler collisionHandler;
    private double totalTime;
    private long lastTime;

    public Door(Vector2d position, Vector2d leftPoint, Vector2d rightPoint, Texture texture) {
        this.state = DOOR_STATE.closed;
        this.offset = 0.0;
        this.totalTime = 0.0;

        this.position = position;
        this.leftPoint = leftPoint;
        this.rightPoint = rightPoint;
        this.texture = texture;
        this.lastTime = System.currentTimeMillis();
    }

    @Override
    public void update(double dt, Level level) {
        double speed = 0.05;
        long currTime = System.currentTimeMillis();
        long elapsed = currTime - lastTime;

        switch (state) {
            case closed:
                break;
            case opening:
                if(elapsed >= UPDATE_TIME) {
                    offset += speed * dt / 2;
                    if(offset >= 1.0) {
                        offset = 1.0;
                        state = DOOR_STATE.opened;
                        collisionHandler.setWorldCollision(position, CollisionHandler.FREE);
                    } else {
                        collisionHandler.setWorldCollision(position, CollisionHandler.BLOCKED);
                    }
                    lastTime = currTime;
                }
                break;
            case closing:
                if(elapsed >= UPDATE_TIME) {
                    offset -= speed * dt / 2;
                    if(offset <= 0.0) {
                        offset = 0.0;
                        state = DOOR_STATE.closed;
                        collisionHandler.setWorldCollision(position, CollisionHandler.BLOCKED);
                    } else {
                        collisionHandler.setWorldCollision(position, CollisionHandler.BLOCKED);
                    }
                    lastTime = currTime;
                }
                break;
            case opened:
                if(elapsed >= MAX_OPENED_TIME) {
                    state = DOOR_STATE.closing;
                    lastTime = currTime;
                }
                break;
        }
    }

    public Vector2d getLeftPoint() { return this.leftPoint; }

    public Vector2d getRightPoint() {
        double off = offset > 0 ? -offset : offset;
        return new Vector2d(this.rightPoint.x + off, this.rightPoint.y);
    }

    public double getOffset() { return offset; }

    public DOOR_STATE getState() {
        return this.state;
    }

    public void setCollisionHandler(CollisionHandler h) { this.collisionHandler = h; }

    public void open() {
        if(this.state == DOOR_STATE.closed) {
            this.state = DOOR_STATE.opening;
        }
    }
}
