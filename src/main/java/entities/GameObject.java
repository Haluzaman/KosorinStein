package  entities;

import  utils.math.Vector2d;

public abstract class GameObject {

    protected Vector2d position;

    public GameObject() {}

    public GameObject(Vector2d position) {
        this.position = position;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public Vector2d getPosition() {
        return this.position;
    }
}
