package graphics.effects;

import interfaces.ILightSource;

public class WaveEffect extends DefaultEffect {

    private int startVal;
    private int maxVal;
    private int duration;

    public WaveEffect(int startVal, int duration, int maxVal) {
        this.startVal = startVal;
        this.maxVal = maxVal;
        this.duration = duration;
    }


    @Override
    public void update(double dt) {

    }
}
