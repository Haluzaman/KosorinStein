package utils.render;

public class MetaInfoLoader {

    private MetaInfoLoader() {
    }

    public static void loadMetaInfo() {

        final String a = "animationMeta.xml";
        final String b = "spriteSheetMeta.xml";

        TextureManager.getInstance().initSpriteSheets(SpriteSheetLoader.loadSpriteSheets(b));
        TextureManager.getInstance().initAnimations(AnimationLoader.loadAnimations(a));


    }
}
