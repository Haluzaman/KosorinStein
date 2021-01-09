package utils.collision;

import utils.math.Hit;
import utils.math.MathUtils;
import utils.math.Ray;
import utils.math.Vector2d;

public class BoundingCircle extends BoundingShape {

    private double radius;

    public BoundingCircle(Vector2d position, double radius) {
        super(position);
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public Hit getIntersection(Ray ray) {
        return MathUtils.getIntersection(ray, this);
    }
}
