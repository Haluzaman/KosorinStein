package graphics;

import java.awt.image.BufferedImage;

public class SpriteSheet {

    private int width;
    private int height;
    private BufferedImage image;

    public final Texture[][] textureAtlas;

    public SpriteSheet(BufferedImage image, Texture[][] textureAtlas) {
        this.textureAtlas = textureAtlas;
    }

    public Texture getTextureAt(int row, int col) {
        return textureAtlas[row][col];
    }

    public Texture[] getTextureRow(int row) {
        return textureAtlas[row];
    }

    public Texture[][] getTextureRows() { return this.textureAtlas; }
}
