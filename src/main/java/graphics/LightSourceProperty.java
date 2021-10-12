package  graphics;

import  utils.math.Vector2d;

public class LightSourceProperty {

    public Vector2d worldPosition;
    public double red;
    public double green;
    public double blue;
    public double attentuation;


    public LightSourceProperty() {
        this.red = 0.0;
        this.green = 0.0;
        this.blue = 0.0;
        this.worldPosition = new Vector2d();
        attentuation = 10000000.0;
    }

    public LightSourceProperty(Vector2d pos, double red, double green, double blue, double a) {
        this.worldPosition = pos;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.attentuation = a;
    }

}
