package  entities.weapons;

import  graphics.animation.IAnimation;

public class Knife extends MeleeWeapon {

    public Knife(int id, int damage, int ammo, IAnimation c) {
        super(id, damage, 1, c);
    }
}
