package entities.weapons;

import graphics.animation.IAnimation;
import interfaces.ICollidable;
import utils.math.MathUtils;

public class HitscanWeapon extends Weapon {

    public HitscanWeapon(int id, int damage, int ammo, IAnimation c) {
        super(id, damage, ammo, c);
    }

    @Override
    protected void shootCallback() {
        this.ammo--;
        ICollidable c = collisionHandler.findCollision(MathUtils.constructRay(player.getCamera()), 10.0);
        if(c != null) {
            c.onAction(this);
        }
    }
}
