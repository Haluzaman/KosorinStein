package entities;

import entities.weapons.Weapon;
import graphics.animation.IAnimation;
import interfaces.ICollidable;
import interfaces.IRenderable;
import interfaces.IUpdatable;
import level.Level;
import utils.collision.BoundingCircle;
import utils.collision.BoundingLine;
import utils.collision.BoundingShape;
import utils.math.MathUtils;
import utils.math.Ray;
import utils.math.Vector2d;
import utils.render.RenderContext;

public class Guard extends GameObject implements IUpdatable, IRenderable, ICollidable {

    private static int IDLE = 0;
    private static int HURT = 1;


    private RenderContext renderContext;
    private BoundingShape boundingShape;

    public Guard(Vector2d position, RenderContext c) {
        super(position);
        this.renderContext = c;
        this.boundingShape = new BoundingCircle(position, 0.5);
    }

    @Override
    public RenderContext getRenderContext() {
        return renderContext;
    }

    @Override
    public void update(double dt, Level level) {
        IAnimation a = renderContext.getAnimation();
        a.update(dt);
        if(a.getState() == HURT && a.isDone()) {
            a.setAnimation(IDLE);
        }
    }

    @Override
    public void onAction(GameObject src) {
    }


    @Override
    public void onAction(Weapon w) {
        this.renderContext.getAnimation().setAnimation(1);
    }


    @Override
    public void onAction() {
    }

    @Override
    public BoundingShape getBoundingShape() {
        return boundingShape;
    }


}
