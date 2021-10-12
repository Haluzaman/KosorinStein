package  graphics;

import java.awt.image.BufferedImage;

public class Texture {

    protected BufferedImage image;
    protected int[] pixels;
    protected int width;
    protected int height;

    public Texture(){}

    public Texture(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = new int[this.width * this.height];
        this.image.getRGB(0,0,width,height,pixels,0,width);
    }

    public int getPixelAt(int x, int y) {
        x &= (width - 1);
        y &= (height - 1);
        return pixels[y * width + x];
    }

    public int samplePixelAt(double x, double y) {
        //autowrap texture if out of bounds
        x = x - Math.floor(x);
        y = y - Math.floor(y);

        int xx = (int) (x * width);
        int yy = (int) (y * height);

        return pixels[yy * width + xx];
    }

    public BufferedImage getImage() { return image; }

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
}
