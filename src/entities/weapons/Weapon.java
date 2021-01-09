package entities.weapons;

import entities.Player;
import graphics.Texture;
import graphics.animation.IAnimation;
import utils.level.CollisionHandler;


public abstract class Weapon {

    protected static int FIRING = 0;
    protected static int IDLE = 1;

    protected CollisionHandler collisionHandler;
    protected int id;
    protected int damage;
    protected int ammo;
    protected boolean isShooting;
    protected IAnimation animation;
    protected Player player;
//    protected LightSourceProperty lightProperty;

//    public Weapon(int id, int damage, int ammo, IAnimation c, LightSourceProperty lightProperty) {
//        TODO: change to 1 after implementation of hand attack
//        this.id = id;
//        this.animation = c;
//        this.damage = damage;
//        this.ammo = ammo;
//        this.isShooting = false;
//        this.lightProperty = Objects.requireNonNullElseGet(lightProperty, LightSourceProperty::new);
//    }

    public Weapon(int id, int damage, int ammo, IAnimation c) {
        //TODO: change to 1 after implementation of hand attack
        this.id = id;
        this.animation = c;
        this.damage = damage;
        this.ammo = ammo;
        this.isShooting = false;
    }

    public int getId() { return this.id;}

    public void update() {
        animation.update(0.0);
        if(animation.getState() == FIRING && animation.isDone()) {
            animation.setAnimation(IDLE);
            this.isShooting = false;
        }
    }

    public void shoot() {
        if(this.animation.getState() != FIRING && this.ammo > 0) {
            this.animation.setAnimation(FIRING);
            this.isShooting = true;
            shootCallback();
        }
    };

    protected abstract void shootCallback();

    public boolean canSwitch() {
        return this.animation.isDone();
    }

    public Texture getTexture() {
        return animation.getCurrentTexture();
    }

    public boolean isShooting() {
        return this.isShooting;
    }

    public void setPlayer(Player p) {
        this.player = p;
    }

    public void setCollisionHandler(CollisionHandler cHandler) {
        this.collisionHandler = cHandler;
    }

    public int getAmmo() { return this.ammo; }
//    public abstract LightSourceProperty getLightSourceProperty();
}
