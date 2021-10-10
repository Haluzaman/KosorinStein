package utils.render;

import graphics.Texture;
import utils.io.FileReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TextureLoader {

    private final Map<String, Texture> textureMap;

    private static TextureLoader instance = null;

    private TextureLoader() {
        textureMap = new HashMap<>();
    }

    public void loadTexturesFromFile(String fileName) {
        try {
            InputStream in = FileReader.readFile(FileReader.FileType.GRAPHICS, fileName);
            if(in == null) return;

            BufferedImage image = ImageIO.read(in);
            String[] splittedName = fileName.split("/");
            String textureName = splittedName[splittedName.length - 1].split("\\.")[0];
            System.out.println("Loaded Texture: " + textureName);
            Texture texture = new Texture(image);
            textureMap.put(textureName, texture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TextureLoader getInstance() {
        if(instance == null)
            instance = new TextureLoader();

        return instance;
    }

    public Map<String, Texture> getTextureMap(){
        return textureMap;
    }
}
