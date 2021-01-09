package utils.math;

import entities.Door;
import level.Level;
import level.MapInfo;
import level.Tile;
import java.util.List;

public class MapIntersectionFinder {

    private Level level;

    public MapIntersectionFinder(Level lvl) {
        this.level = lvl;
    }

    public void findAllIntersections(Ray ray, double correctionAngle, List<Intersection> intersections) {
        boolean stop = false;
        Ray currRay = ray;
        Intersection intersection;

        while(!stop) {
            intersection = performDDA(currRay, correctionAngle,ray.getOrigin());
            if(intersection.t == null) break;
            intersections.add(intersection);

            if(intersection.wallType == Tile.HORIZ_WINDOW) {
                currRay = new Ray(intersection.position, ray.getDirection(), ray.getAngle());
            } else {
                stop = true;
            }
        }


    }

    public Intersection performDDA(Ray ray, double correctionAngle, Vector2d compDistFrom) {
        double currAngle = ray.getAngle();
        boolean facingUp = ray.isFacingUp();
        boolean facingRight = ray.isFacingRight();

        if(compDistFrom == null) compDistFrom = ray.getOrigin();

        Vector2d rayOrigin = ray.getOrigin();
        MapInfo mapInfo = level.getMapInfo();

        Intersection finalInter = new Intersection();
        Intersection horizInter = new Intersection();
        Intersection verInter = new Intersection();

        double horizYa;
        double horizXa;
        double verYa;
        double verXa;

        int mapWidth = mapInfo.getWidth();
        int mapHeight = mapInfo.getHeight();

        boolean wallHit = false;
        double slope = -Math.tan(currAngle);

        verInter.position.x = facingRight ? Math.ceil(rayOrigin.x) : Math.floor(rayOrigin.x);
        verInter.position.y = rayOrigin.y + (verInter.position.x - rayOrigin.x) * slope;
        verInter.distToWall = Double.MAX_VALUE;
        verXa = facingRight ? 1.0 : -1.0;
        verYa = verXa * slope;

        while(!wallHit) {
            int mapX = (int) (verInter.position.x + (facingRight ? 0.0f : -1.0f));
            int mapY = (int) (verInter.position.y);
            if(!MathUtils.isInBounds(mapX, mapY, mapWidth, mapHeight)) {
                wallHit = true;
            } else {
                int wallType = mapInfo.getTileAt(mapX, mapY).type;
                if(wallType == 1) {
                    wallHit = true;
//                    verInter.distToWall = (verInter.position.x - rayOrigin.x) * Math.cos(correctionAngle) + (-verInter.position.y + rayOrigin.y) * Math.sin(correctionAngle);
                    verInter.distToWall = (verInter.position.x - compDistFrom.x) * Math.cos(correctionAngle) + (-verInter.position.y + compDistFrom.y) * Math.sin(correctionAngle);
                    verInter.wallType = wallType;
                    verInter.t = mapInfo.getTileAt(mapX, mapY);
                } else {
                    nextStep(verInter, verXa, verYa);
                }
            }
        }

        slope = -1.0f / Math.tan(currAngle);
        horizInter.position.y = facingUp ? (int)(rayOrigin.y) : Math.ceil(rayOrigin.y);
        horizInter.position.x = rayOrigin.x + (horizInter.position.y - rayOrigin.y) * slope;
        horizInter.distToWall = Double.MAX_VALUE;
        horizYa = facingUp ? -1.0 : 1.0;
        horizXa = horizYa * slope;

        wallHit = false;
        while(!wallHit) {
            int mapX = (int)(horizInter.position.x);
            int mapY = (int)(horizInter.position.y + (facingUp ? -1.0f : 0.0f));
            if(!MathUtils.isInBounds(mapX, mapY, mapWidth, mapHeight)) {
                wallHit = true;
            } else {
                int wallType = mapInfo.getTileAt(mapX, mapY).type;
                if(wallType == 1) {
                    wallHit = true;
//                    horizInter.distToWall = (horizInter.position.x - rayOrigin.x) * Math.cos(correctionAngle) + (-horizInter.position.y + rayOrigin.y) * Math.sin(correctionAngle);
                    horizInter.distToWall = (horizInter.position.x - compDistFrom.x) * Math.cos(correctionAngle) + (-horizInter.position.y + compDistFrom.y) * Math.sin(correctionAngle);
                    horizInter.wallType = wallType;
                    horizInter.t = mapInfo.getTileAt(mapX, mapY);
                } else if(wallType == Tile.HORIZ_DOOR) {
                    //door
                    Door door = level.getDoor(mapX, mapY);
                    Vector2d leftPoint = door.getLeftPoint();
                    Vector2d RightPoint = door.getRightPoint();
                    Ray r = new Ray(compDistFrom, ray.getDirection(), ray.getAngle());
                    var dist = MathUtils.GetRayToLineSegmentIntersection(r, leftPoint, RightPoint);
                    wallHit = handleHit(ray, correctionAngle, rayOrigin, mapInfo, horizInter, horizYa, horizXa, mapX, mapY, wallType, dist);
                } else if(wallType == Tile.HORIZ_WINDOW) {
                    Vector2d leftPos = new Vector2d(mapX, mapY + 0.5);
                    Vector2d rightPos = new Vector2d(mapX + 1.0, mapY + 0.5);
                    Ray r = new Ray(compDistFrom, ray.getDirection(), ray.getAngle());
                    var dist = MathUtils.GetRayToLineSegmentIntersection(r, leftPos, rightPos);
                    wallHit = handleHit(ray, correctionAngle, rayOrigin, mapInfo, horizInter, horizYa, horizXa, mapX, mapY, wallType, dist);
                } else {
                    nextStep(horizInter, horizXa, horizYa);
                }
            }
        }

        if(verInter.distToWall < horizInter.distToWall) {
            fillIntersection(finalInter, verInter, true);
        }
        else {
            fillIntersection(finalInter, horizInter, false);
        }

        return finalInter;
    }

    private void nextStep(Intersection i, double xa, double ya) {
        i.position.x += xa;
        i.position.y += ya;
    }

    private void fillIntersection(Intersection to, Intersection from, boolean isVertical) {
        to.distToWall = from.distToWall;
        to.position.x = from.position.x;
        to.position.y = from.position.y;
        to.isVertical = isVertical;
        to.wallType = from.wallType;
        to.t = from.t;
    }

    private boolean handleHit(Ray ray, double correctionAngle, Vector2d rayOrigin, MapInfo mapInfo, Intersection horizInter, double ya, double xa, int mapX, int mapY, int wallType, Hit dist) {
        boolean wallHit = false;

        if (dist != null) {
            wallHit = true;
            horizInter.position.x = rayOrigin.x + dist.distFromSrc * ray.getDirection().x;
            horizInter.position.y = rayOrigin.y + dist.distFromSrc * -ray.getDirection().y;
            horizInter.distToWall = (horizInter.position.x - rayOrigin.x) * Math.cos(correctionAngle) + (-horizInter.position.y + rayOrigin.y) * Math.sin(correctionAngle);
            horizInter.wallType = wallType;
            horizInter.t = mapInfo.getTileAt(mapX, mapY);
        } else {
            nextStep(horizInter, xa, ya);
        }

        return wallHit;
    }
}
