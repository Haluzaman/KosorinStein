package  graphics.effects;

import  graphics.LightSourceProperty;

public class PulsatingEffect extends DefaultEffect {

    private double maxValue;
    private int delta;
    private long lastTime;
    private double speed;
    private double add;

    public PulsatingEffect(double maxValue, int delta, double speed) {
        this.maxValue = maxValue;
        this.delta = delta;
        this.speed = speed;
        this.add = 0.0;
    }

    @Override
    public void update(double dt) {
        long currentTime = System.currentTimeMillis();
        long d = currentTime - lastTime;
        if(d >= delta) {
            add += 0.01;
            //TODO: find how actualy do this shit
            lightSource.attentuation = Math.abs(Math.sin(add)) * maxValue;
            System.out.println(lightSource.attentuation);
            lastTime = System.currentTimeMillis();
        }
    }
}
