package graphics.animation;

import graphics.Texture;

public interface IAnimation {

    Texture getCurrentTexture();
    Texture getCurrentTexture(double angle);

    void update(double delta);

    void setAnimation(int state);
    int getState();

    boolean isDone();

    int getAnimFrameID();
}
