package utils.math;

import render.Camera;
import utils.collision.BoundingCircle;
import utils.collision.BoundingLine;

import java.awt.*;

public class MathUtils {

    private MathUtils() { }

    public static boolean isRayFacingUp(double angle, boolean inRad) {
        if(!inRad) {
            angle =  (angle * Math.PI / 180.0f);
        }

        return angle >= 0.0 && angle < Math.PI;
    }

    public static boolean isRayFacingLeft(double angle, boolean inRad) {
        if(!inRad) {
            angle =  (angle * Math.PI / 180.0f);
        }

        return angle >= (Math.PI / 2) && angle < (Math.PI * 3 / 2);
    }

    public static boolean isInBounds(int x, int y, int w, int h) {
        return !(x < 0 || x >= w || y < 0 || y >= h);
    }

    public static Hit GetRayToLineSegmentIntersection(Ray ray, Vector2d point1, Vector2d point2) {
        Vector2d rayOrigin = ray.getOrigin();
        Vector2d rayDirection = ray.getDirection();
        Vector2d v1 = new Vector2d(rayOrigin.x - point1.x, rayOrigin.y - point1.y );
        Vector2d v2 = new Vector2d(point2.x - point1.x, point2.y - point1.y );
        Vector2d v3 = new Vector2d(rayDirection.y, rayDirection.x);

        double dot = dotProduct2D(v2, v3);
        if (Math.abs(dot) < 0.000001)
        return null;

        double t1 = crossProduct2D(v2, v1) / dot;
        double t2 = dotProduct2D(v1,v3) / dot;

        if (t1 >= 0.0 && (t2 >= 0.0 && t2 <= 1.0)) {
            double x = rayOrigin.x + rayDirection.x * t1;
            double y = rayOrigin.y + rayDirection.y * t1;
            return new Hit(x, y, t1);
        }

        return null;
    }

    public static Hit getIntersection(Ray ray, BoundingLine l) {
        return GetRayToLineSegmentIntersection(ray, l.getPosition(), l.getSecondPoint());
    }

    public static Hit getIntersection(Ray ray, BoundingCircle c) {
        return getRayCircleIntersection(ray, c.getPosition(), c.getRadius());
    }

    public static Hit getRayCircleIntersection(Ray ray, Vector2d center, double radius) {
        Vector2d oC = getVector(ray.getOrigin(), center);
        double d = dotProduct2D(ray.getDirection(), oC);

        if(d < 0.0) return null;

        double x = ray.getOrigin().x  + ray.getDirection().x * d;
        double y = ray.getOrigin().y  + ray.getDirection().y * d;
        Vector2d newPoint = new Vector2d(x,y);

        double t = getDistance(center, newPoint);
        if(t <= radius) {
            var h = new Hit();
            h.distFromSrc = getDistance(ray.getOrigin(), center);
            return h;
        }

        return null;
    }

    public static Vector2d getVector(Vector2d p1, Vector2d p2) {
        return new Vector2d(p2.x - p1.x, p2.y - p1.y);
    }

    public static double dotProduct2D(Vector2d p1, Vector2d p2) {
        return p1.x * p2.x + p1.y * p2.y;
    }

    public static double crossProduct2D(Vector2d p1, Vector2d p2) {
        return p1.x * p2.y - p1.y * p2.x;
    }

    //without square root
    public static double getSimpleDistance(Vector2d pos1, Vector2d pos2) {
        return Math.pow(pos2.x - pos1.x, 2) + Math.pow(pos2.y - pos1.y, 2);
    }

    //with square root
    public static double getDistance(Vector2d pos1, Vector2d pos2) {
        return Math.sqrt(Math.pow(pos2.x - pos1.x, 2) + Math.pow(pos2.y - pos1.y, 2));
    }


    public static Ray constructRay(Camera camera, double cameraX) {
        double rayDirX = camera.direction.x + camera.plane.x * cameraX;
        double rayDirY = camera.direction.y + camera.plane.y * cameraX;
        double angle = Math.atan2(rayDirY, rayDirX);
        Vector2d direction = new Vector2d(rayDirX, rayDirY);
        Vector2d origin = new Vector2d(camera.position.x, camera.position.y);

        return new Ray(origin, direction, angle);
    }

    /**
     *  Constructs ray with -y direction
     *  for entity collision solutions
     * */
    public static Ray constructRay(Camera camera) {
        return new Ray(camera.position.x, camera.position.y ,
            camera.direction.x, -camera.direction.y,
            camera.getAngle());
    }

    public static void rotateVector(Vector2d vec, double angle) {
        double newX = vec.x * Math.cos(angle) - vec.y * Math.sin(angle);
        double newY = vec.x * Math.sin(angle) + vec.y * Math.cos(angle);
        vec.x = newX;
        vec.y = newY;
    }

    public static int fadeToBlack(int color, double current, double max) {
        if (current < 0) {
            return color;
        }
        if (current >= max) {
            return Color.BLACK.getRGB();
        }

        Color c = new Color(color);
        double amount = (max - current) / max;
        int r = (int) (c.getRed() * amount);
        int g = (int) (c.getGreen() * amount);
        int b = (int) (c.getBlue() * amount);
        return new Color(r, g, b).getRGB();
    }

    public static int clamp(int value, int max, int min) {
        if(value > max) return max;
        else return Math.max(value, min);
    }

    public static double clamp(double value, double max, double min) {
        if(value > max) return max;
        else return Math.max(value, min);
    }

    public static Vector2d getRelativePos(Vector2d v1, Vector2d v2) {
        return new Vector2d(v1.x - v2.x, v2.y - v1.y);
    }

    public static int blendPixel(int frontPixel, int backPixel) {
        int currAlpha = ((frontPixel >> 24) & 0xff);
        int currR = ((frontPixel & 0x00ff0000) >> 16);
        int currG = ((frontPixel & 0x0000ff00) >> 8);
        int currB = (frontPixel & 0x000000ff);

        int oldR = (backPixel & 0x00ff0000) >> 16;
        int oldG = (backPixel & 0x0000ff00) >> 8;
        int oldB = (backPixel & 0x000000ff);

        double alpha = currAlpha / 255.0;
        double op = 1 - alpha;

        int newRed = (int)((alpha * currR) + (op * oldR));
        int newGreen = (int)((alpha * currG) + (op * oldG));
        int newBlue = (int)((alpha * currB) + (op * oldB));

        return (0xff000000) | ((newRed << 16) | (newGreen << 8) | newBlue);
    }

    public static double getAngle(Vector2d p1, Vector2d p2) {
        Vector2d p = getVector(p1, p2);
        double angle = Math.atan2(p.y, p.x);
        angle = (angle > 0.0 ? angle : (2.0f * Math.PI + angle - 0.001));
        return angle;
    }

    public static double getAngle(Vector2d p) {
        double angle = Math.atan2(p.y, p.x);
        angle = (angle > 0.0 ? angle : (2.0f * Math.PI + angle - 0.001));
        return angle;
    }

    public static double degToRad(double angle) {
        return Math.PI / 180.0 * angle;
    }

    public static double radToDeg(double angle) {
        return 180.0 / Math.PI * angle;
    }
}
