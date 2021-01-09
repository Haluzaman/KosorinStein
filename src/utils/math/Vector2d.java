package utils.math;

public class Vector2d {
    public double x;
    public double y;

    public Vector2d() {

    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d(Vector2d v) {
        this.x = v.x;
        this.y = v.y;
    }

    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setXY(Vector2d v) {
        this.x = v.x;
        this.y = v.y;
    }
}
