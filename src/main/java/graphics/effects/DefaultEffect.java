package  graphics.effects;

import  graphics.LightSourceProperty;
import  interfaces.ILightSource;

public abstract class DefaultEffect implements IEffect {

    protected LightSourceProperty lightSource;

    public DefaultEffect() {

    }

    @Override
    public void setContext(LightSourceProperty source) {
        this.lightSource = source;
    }

    public abstract void update(double dt);
}
