package utils.render;

import graphics.SpriteSheet;
import graphics.Texture;
import graphics.animation.IAnimation;

import java.util.Map;

public class TextureManager {

    private static TextureManager instance = null;
    private Map<String, Texture> textureAtlas;
    private Map<String, SpriteSheet> spriteSheetAtlas;
    private Map<String, IAnimation> animationAtlas;

    private TextureManager() {
        textureAtlas = null;
    }

    //TODO: do it better not hardcoded, let it read from some xml
    public static void init() {
        if(instance == null) {
            TextureLoader.getInstance().loadTexturesFromFile("wallTexture.png");
            TextureLoader.getInstance().loadTexturesFromFile("barell.png");
            TextureLoader.getInstance().loadTexturesFromFile("door.png");
            TextureLoader.getInstance().loadTexturesFromFile("background.png");
            TextureLoader.getInstance().loadTexturesFromFile("ceiling.png");
            TextureLoader.getInstance().loadTexturesFromFile("brokenFloor.png");
            TextureLoader.getInstance().loadTexturesFromFile("floor.png");
            TextureLoader.getInstance().loadTexturesFromFile("window.png");
            instance = new TextureManager();
            instance.textureAtlas = TextureLoader.getInstance().getTextureMap();
//            instance.spriteSheetAtlas = SpriteSheetLoader.loadSpriteSheets();
        }
    }

    public void initSpriteSheets(Map<String, SpriteSheet> spriteSheetAtlas) {
        instance.spriteSheetAtlas = spriteSheetAtlas;
    }

    public void initAnimations(Map<String, IAnimation> animationAtlas) {
        instance.animationAtlas = animationAtlas;
    }

    public static TextureManager getInstance() {
        if(instance == null) {
            instance = new TextureManager();
        }
        return instance;
    }

    public Texture getTextureByName(String name) {
        return textureAtlas.get(name);
    }

    public Texture getTextureByTile(int wallType) {
        if(wallType == 1) {
            return textureAtlas.get("wallTexture");
        } else if(wallType == 2) {
            return textureAtlas.get("door");
        } else {
            System.out.println("TextureManager returning null for " + wallType);
            return textureAtlas.get("wallTexture");
        }
    }

   public static IAnimation getAnimationByName(String name) {
       IAnimation a = instance.animationAtlas.get(name);
       if(a == null) {
           System.out.println("Could not load Animation: " + name);
           System.exit(1);
       }

       return a;
   }

   public static Texture[] getTextureRow(String spriteSheetName, int spriteRow) {
       SpriteSheet spriteSheet = instance.spriteSheetAtlas.get(spriteSheetName);
       if(spriteSheet == null) {
           System.out.println("Could not load textureRow form: " + spriteSheetName + " int row: " + spriteRow);
           System.exit(1);
       }
       return spriteSheet.getTextureRow(spriteRow);
   }

    public static Texture getTextureAt(String spriteSheetName, int row, int col) {
        SpriteSheet spriteSheet = instance.spriteSheetAtlas.get(spriteSheetName);
       if(spriteSheet == null) {
           System.out.println("Could not load textureRow form: " + spriteSheetName);
           System.exit(1);
       }
       return spriteSheet.getTextureAt(row, col);
   }

    public static Texture[][] getTextureRows(String spriteSheetName) {
        SpriteSheet spriteSheet = instance.spriteSheetAtlas.get(spriteSheetName);
        if(spriteSheet == null) {
            System.out.println("Could not load textureRow form: " + spriteSheetName);
            System.exit(1);
        }
        return spriteSheet.getTextureRows();
    }

}
