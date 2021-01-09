package utils.render;

import graphics.animation.IAnimation;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetaInfoLoader {

    private MetaInfoLoader() {}

    public static void loadMetaInfo() {
        String dirName = "res/meta/";

        List<String> metaName = Stream.of(Objects.requireNonNull(new File(dirName).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toList());

        if(metaName.size() < 1) {
            System.out.println("No metainfo found!");
            System.exit(1);
        }

        String a = metaName.get(0);
        String b = metaName.get(1);

        TextureManager.getInstance().initSpriteSheets(SpriteSheetLoader.loadSpriteSheets(b));
        TextureManager.getInstance().initAnimations(AnimationLoader.loadAnimations(a));
    }
}
