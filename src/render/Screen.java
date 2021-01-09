package render;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Screen {

    private int[] pixels;
    private BufferedImage image;
    private Graphics2D graphics;
    private int width;
    private int height;

    public Screen(int w, int h) {
        this.width = w;
        this.height = h;
        image = new BufferedImage(this.width,this.height,BufferedImage.TYPE_INT_ARGB);
//        image = new BufferedImage(this.width,this.height,BufferedImage.TYPE_INT_RGB);
        graphics = (Graphics2D) image.getGraphics();
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
    }

    public void clear(Color c) {
        Arrays.fill(pixels, c.getRGB());
    }


    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int[] getPixels() { return this.pixels; }
    public int getPixelAt(int index) { return this.pixels[index]; }
    public void setPixelAt(int index, int val) { this.pixels[index] = val; }
    public BufferedImage getImage() { return this.image; }
    public Graphics2D getGraphics() { return this.graphics; }

}
