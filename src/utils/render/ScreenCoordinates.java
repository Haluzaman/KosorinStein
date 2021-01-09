package utils.render;

public class ScreenCoordinates {

    public int screenX;
    public int screenY;
    public int drawEndX;
    public int drawEndY;
    public double depth;

    public ScreenCoordinates() {

    }

    public ScreenCoordinates(int sX, int sY, int dX, int dY, double depth){
        this.screenX = sX;
        this.screenY = sY;
        this.drawEndX = dX;
        this.drawEndY = dY;
        this.depth = depth;
    }
}
