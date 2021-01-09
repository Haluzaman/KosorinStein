package utils.render;

import graphics.SpriteSheet;
import graphics.Texture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextureLoader {

    private Map<String, Texture> textureMap;

    private static TextureLoader instance = null;

    private TextureLoader() {
        textureMap = new HashMap<>();
    }

    public void loadTexturesFromFile(String fileName) {
        try {
            fileName = "res/graphics/" + fileName;
            File img = new File(fileName);
            BufferedImage image = ImageIO.read(img);

            String[] splittedName = fileName.split("/");
            String textureName = splittedName[splittedName.length - 1].split("\\.")[0];
            System.out.println("Loaded Texture: " + textureName);
            Texture texture = new Texture(image);
            textureMap.put(textureName, texture);
//            System.out.println(image);
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
