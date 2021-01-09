package entities.weapons;

import graphics.animation.IAnimation;
import interfaces.ICollidable;
import utils.math.MathUtils;
public class MeleeWeapon extends Weapon {

    public MeleeWeapon(int id, int damage, int ammo, IAnimation c) {
        super(id, damage, ammo, c);
    }

    @Override
    public void shootCallback() {
       ICollidable c = collisionHandler.findCollision(MathUtils.constructRay(player.getCamera()), 1.0);
       if(c != null) {
           c.onAction(this);
       }
    }

}
