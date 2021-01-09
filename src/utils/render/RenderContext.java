package utils.render;

import graphics.Texture;
import graphics.animation.IAnimation;
import utils.math.Vector2d;

public class RenderContext {

    private Vector2d position;
    private IAnimation animation;
    private boolean applyLight;

    public RenderContext(Vector2d pos, IAnimation animation) {
        this.position = pos;
        this.animation = animation;
    }

    public RenderContext(Vector2d pos, IAnimation animation, boolean applyLight) {
        this.position = pos;
        this.animation = animation;
        this.applyLight = applyLight;
    }


    public Texture getTexture() {
        return this.animation.getCurrentTexture();
    }

    public Texture getTexture(double angle) {
        return this.animation.getCurrentTexture(angle);
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public IAnimation getAnimation() {
        return animation;
    }

    public boolean isLightApplicable() {
        return applyLight;
    }
}
