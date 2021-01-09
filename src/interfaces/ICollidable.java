package interfaces;

import entities.GameObject;
import entities.weapons.Weapon;
import utils.collision.BoundingShape;
import utils.math.Hit;
import utils.math.MathUtils;
import utils.math.Ray;
import utils.math.Vector2d;

public interface ICollidable {

    void onAction(GameObject src);
    void onAction(Weapon W);
    void onAction();
    BoundingShape getBoundingShape();

    default Hit intersects(Ray ray) {
        return getBoundingShape().getIntersection(ray);
    }
}
