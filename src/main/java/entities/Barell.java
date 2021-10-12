package  entities;

import  interfaces.IRenderable;
import  utils.math.Vector2d;
import  utils.render.RenderContext;

public class Barell extends GameObject implements IRenderable {


    private RenderContext renderContext;

    public Barell() {
        super();
    }

    public Barell(Vector2d position) {
        super(position);
    }

    public Barell(Vector2d position, RenderContext rendercontext) {
        this.position = position;
        this.renderContext = rendercontext;
    }

    @Override
    public RenderContext getRenderContext() { return renderContext; }

    public Vector2d getPosition() { return this.position; }
}
