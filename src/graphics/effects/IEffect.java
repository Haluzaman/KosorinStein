package graphics.effects;

import graphics.LightSourceProperty;
import interfaces.ILightSource;

public interface IEffect {

    public void setContext(LightSourceProperty source);
    public void update(double dt);
}
