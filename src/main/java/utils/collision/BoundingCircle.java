package utils.collision;

import utils.math.Vector2d;
import utils.math.Hit;
import utils.math.MathUtils;
import utils.math.Ray;

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
