package  entities;

import  graphics.LightSourceProperty;
import  graphics.effects.IEffect;
import  interfaces.ILightSource;
import  interfaces.IRenderable;
import  interfaces.IUpdatable;
import  level.Level;
import  utils.math.Vector2d;
import  utils.render.RenderContext;

public class Lamp extends GameObject implements IRenderable, IUpdatable, ILightSource {

    private LightSourceProperty lightProperty;
    private RenderContext renderContext;
    private IEffect effect;

    public Lamp() {
        super();
    }

    public Lamp(Vector2d position, RenderContext renderContext, LightSourceProperty l) {
        super(position);
        this.renderContext = renderContext;
        this.lightProperty = l;
    }

    public Lamp(Vector2d position, RenderContext renderContext, LightSourceProperty l, IEffect e) {
        super(position);
        this.renderContext = renderContext;
        this.lightProperty = l;
//        this.effect = e;
//        this.effect.setContext(this.lightProperty);
    }

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public RenderContext getRenderContext() {
        return this.renderContext;
    }

    @Override
    public void update(double dt, Level level) {
        this.renderContext.getAnimation().update(dt);
        if(effect != null) effect.update(dt); //TODO: maybe nullObject? Do not have to have condition
    }


    @Override
    public LightSourceProperty getLightSourceProperty() {
        return this.lightProperty;
    }
}
