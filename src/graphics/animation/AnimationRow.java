package graphics.animation;

import graphics.Texture;

public class AnimationRow {

    private Texture[] frames;
    private int currentFrame;
    private int timeToNextFrame;
    private long lastTime;
    private int numDirections;
    private int modulo;
    private boolean isDone;
    private int numFrames;

    public AnimationRow(Texture[] frames, int currentFrame, int timeToNextFrame, int numDirections) {
        this.frames = frames;
        this.currentFrame = currentFrame;
        this.timeToNextFrame = timeToNextFrame;
        this.numDirections = numDirections;
        this.modulo = frames.length == 0 ? 1 : frames.length - 1;
        isDone = false;
        this.numFrames = frames.length / numDirections;
    }

    public AnimationRow(Texture[] frames, int currentFrame, int timeToNextFrame) {
        this.frames = frames;
        this.currentFrame = currentFrame;
        this.timeToNextFrame = timeToNextFrame;
    }

    public Texture getCurrentFrame() {
        return this.frames[currentFrame];
    }

    public Texture getCurrentFrame(double angle) {
        int offset = (int)Math.floor(angle / (2 * Math.PI / this.numDirections));
        int cFrame = offset * numFrames + currentFrame;
        return this.frames[cFrame];
    }

    public boolean isDone() { return isDone; }

    public void update() {
        long currTime = System.currentTimeMillis();
        long elapsed = currTime - lastTime;
        if(elapsed >= timeToNextFrame) {
            lastTime = System.currentTimeMillis();
            currentFrame++;
            if(currentFrame  >= this.numFrames) {
                this.isDone = true;
                currentFrame = 0;
            }
        }
    }


    public void init() {
        this.currentFrame = 0;
        isDone = false;
        this.lastTime = System.currentTimeMillis();
    }

    public int getCurrentFrameID() {
        return currentFrame;
    }
}
