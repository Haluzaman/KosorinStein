package  graphics.animation;

import  graphics.Texture;

public class SimpleAnimation implements IAnimation {

    protected AnimationRow[] states;
    protected int currentState;

    public SimpleAnimation(AnimationRow[] states, int currentState) {
        this.states = states;
        this.currentState = currentState;
    }

    public SimpleAnimation(AnimationRow[] states) {
        this.states = states;
        this.currentState = 0;
    }

    @Override
    public Texture getCurrentTexture() {
        return this.states[currentState].getCurrentFrame();
    }

    public void update(double delta) {
        states[currentState].update();
    }

    @Override
    public void setAnimation(int state) {
        this.currentState = state;
        states[currentState].init();
    }

    @Override
    public int getState() {
        return this.currentState;
    }

    @Override
    public boolean isDone() {
        return states[currentState].isDone();
    }

    /**
     * Returns texture based on angle we lookin at texture
     * **/
    @Override
    public Texture getCurrentTexture(double angle) {
        return states[currentState].getCurrentFrame(angle);
    }

    public int getAnimFrameID() {
        return states[currentState].getCurrentFrameID();
    }
}
