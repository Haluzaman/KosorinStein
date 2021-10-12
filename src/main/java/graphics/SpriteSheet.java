package graphics;

import java.awt.image.BufferedImage;

public class SpriteSheet {

    private int width;
    private int height;
    private BufferedImage image;

    public final  graphics.Texture[][] textureAtlas;

    public SpriteSheet(BufferedImage image,  graphics.Texture[][] textureAtlas) {
        this.textureAtlas = textureAtlas;
    }

    public  graphics.Texture getTextureAt(int row, int col) {
        return textureAtlas[row][col];
    }

    public  graphics.Texture[] getTextureRow(int row) {
        return textureAtlas[row];
    }

    public  graphics.Texture[][] getTextureRows() { return this.textureAtlas; }
}
