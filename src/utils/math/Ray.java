package utils.math;

public class Ray {
    private Vector2d direction;
    private Vector2d origin;
    private double angle;
    private boolean isFacingUp;
    private boolean isFacingRight;

    public Ray(Vector2d origin, Vector2d direction, double angle) {
        this.direction = direction;
        this.origin = origin;
        this.angle = Math.abs(angle > 0.0f ? angle : (2.0 * Math.PI + angle ));
        this.isFacingRight = !MathUtils.isRayFacingLeft(this.angle, true);
        this.isFacingUp = MathUtils.isRayFacingUp(this.angle, true);
    }

    public Ray(double x, double y, double dx, double dy, double angle) {
        this.origin = new Vector2d(x, y);
        this.direction = new Vector2d(dx, dy);
        this.angle = Math.abs(angle > 0.0f ? angle : (2.0 * Math.PI + angle ));
        this.isFacingRight = !MathUtils.isRayFacingLeft(this.angle, true);
        this.isFacingUp = MathUtils.isRayFacingUp(this.angle, true);
    }

    public Ray(Ray ray) {
        this.origin = new Vector2d(ray.getOrigin());
        this.direction = new Vector2d(ray.getDirection());
        this.angle = ray.getAngle();
        this.isFacingRight = !MathUtils.isRayFacingLeft(this.angle, true);
        this.isFacingUp = MathUtils.isRayFacingUp(this.angle, true);
    }


    public Vector2d getDirection() {
        return direction;
    }

    public void setDirection(Vector2d direction) {
        this.direction = direction;
    }

    public Vector2d getOrigin() {
        return origin;
    }

    public void setOrigin(Vector2d origin) {
        this.origin = origin;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public boolean isFacingUp() {
        return isFacingUp;
    }

    public void setFacingUp(boolean facingUp) {
        isFacingUp = facingUp;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public void setFacingRight(boolean facingRight) {
        isFacingRight = facingRight;
    }
}
