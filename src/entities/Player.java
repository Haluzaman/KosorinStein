package entities;

import entities.weapons.Weapon;
import graphics.LightSourceProperty;
import graphics.Texture;
import interfaces.IKeyResponsive;
import interfaces.ILightSource;
import interfaces.IUpdatable;
import level.Level;
import render.Camera;
import utils.level.CollisionHandler;
import utils.level.LevelUtils;
import utils.math.Intersection;
import utils.math.MathUtils;
import utils.math.Ray;
import utils.math.Vector2d;

import java.awt.event.KeyEvent;

public class Player extends GameObject implements IKeyResponsive, IUpdatable, ILightSource {

    private Camera camera;
    private Vector2d direction;
    private CollisionHandler collisionHandler;
    //player has lightProperty to not wander in complete dark
    private LightSourceProperty lightProperty;

    private Weapon[] weapons;
    private int currentWeapon;

    public Player(Vector2d position, Vector2d direction, Camera camera, LightSourceProperty lp) {
        super(position);
        this.camera = camera;
        this.direction = direction;
        this.weapons = new Weapon[3];
        this.currentWeapon = 0;
        this.lightProperty = lp;
    }

    @Override
    public void onKeyPress(KeyEvent event) {
        double rotSpeed = 0.12;
        double moveSpeed = 0.12;

        switch(event.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                MathUtils.rotateVector(this.direction, rotSpeed);
                MathUtils.rotateVector(this.camera.plane, rotSpeed);
                break;
            case KeyEvent.VK_RIGHT:
                MathUtils.rotateVector(this.direction,-rotSpeed);
                MathUtils.rotateVector(this.camera.plane,-rotSpeed);
                break;
            case KeyEvent.VK_W:
                double newX = position.x + direction.x * moveSpeed;
                double newY = position.y - direction.y * moveSpeed;
                if(collisionHandler.isTileFree(newX, newY)) {
                    position.x = newX;
                    position.y = newY;
                }
                break;
            case KeyEvent.VK_S:
                newX = position.x - direction.x * moveSpeed;
                newY = position.y + direction.y * moveSpeed;
                if(collisionHandler.isTileFree(newX, newY)) {
                    position.x = newX;
                    position.y = newY;
                }
                break;
            case KeyEvent.VK_SPACE:
                Intersection i = collisionHandler.findMapCollision(new Ray(this.position, this.direction, this.camera.getAngle()));
                if(i.wallType == LevelUtils.DOOR && i.distToWall <= 0.85) {
                    Door d = collisionHandler.getDoorAt((int)i.position.x, (int)i.position.y);
                    d.open();
                }
                break;
            case KeyEvent.VK_1:
                if(weapons[currentWeapon].canSwitch()) {
                    currentWeapon = 0;
                }
                break;
            case KeyEvent.VK_2:
                if(weapons[currentWeapon].canSwitch()) {
                    currentWeapon = 1;
                }
                break;
            case KeyEvent.VK_3:
                if(weapons[currentWeapon].canSwitch()) {
                    currentWeapon = 2;
                }
                break;
        }
        if(event.isControlDown()) {
            weapons[currentWeapon].shoot();
        }

    }


    @Override
    public void onKeyReleased(KeyEvent event) {

    }

    @Override
    public void update(double dt, Level level) {
        Weapon w = weapons[currentWeapon];
        w.update();
        //TODO: move lightSrc to weapon and better calculations then this shit :D
        if(w.isShooting() && w.getId() != 0) {
            lightProperty.attentuation = 0.1;
        } else {
            lightProperty.attentuation = 0.6;
        }
    }

    public void setCollisionHandler(CollisionHandler h) {
        this.collisionHandler = h;
    }

    public Camera getCamera() {return this.camera; }

    public void addWeapon(Weapon w) {
        if(weapons[w.getId()] == null) {
            w.setCollisionHandler(this.collisionHandler);
            w.setPlayer(this);
            weapons[w.getId()] = w;
        }
    }

    public Weapon getWeapon() {
        return weapons[currentWeapon];
    }

    public Texture getWeaponTexture() {
        return weapons[this.currentWeapon].getTexture();
    }

    @Override
    public LightSourceProperty getLightSourceProperty() {
        return lightProperty;
    }

}
