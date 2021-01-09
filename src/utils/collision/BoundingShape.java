package utils.collision;

import utils.math.Hit;
import utils.math.Ray;
import utils.math.Vector2d;

public abstract class BoundingShape {

    private Vector2d position;

    public BoundingShape(Vector2d position) {
        this.position = position;
    }

    public Vector2d getPosition() { return position; }

    public abstract Hit getIntersection(Ray ray);
}
