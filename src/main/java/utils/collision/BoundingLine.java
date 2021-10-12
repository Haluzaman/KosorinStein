package  utils.collision;

import  utils.math.Hit;
import  utils.math.MathUtils;
import  utils.math.Ray;
import  utils.math.Vector2d;

public class BoundingLine extends BoundingShape {

    private Vector2d p2;

    public BoundingLine(Vector2d p1, Vector2d p2) {
        super(p1);
        this.p2 = p2;
    }

    public Vector2d getSecondPoint() { return p2; }

    @Override
    public Hit getIntersection(Ray ray) {
        return MathUtils.getIntersection(ray, this);
    }
}
