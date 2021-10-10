package utils.render;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetaInfoLoader {

    private MetaInfoLoader() {
    }

    public static void loadMetaInfo() {
        final String dirName = "meta/";

        ClassLoader classLoader = MetaInfoLoader.class.getClassLoader();
        URL resource = classLoader.getResource(dirName);
        if (resource == null) {
            System.out.println("file not found! " + dirName);
            System.exit(1);
        }

        try {
            File file = new File(resource.toURI());
            final List<String> metaName = Stream.of(Objects.requireNonNull(file.listFiles()))
                    .filter(f -> !f.isDirectory())
                    .map(File::getName)
                    .collect(Collectors.toList());

            if (metaName.size() < 1) {
                System.out.println("No metainfo found!");
                System.exit(1);
            }

            final String a = metaName.get(0);
            final String b = metaName.get(1);

            TextureManager.getInstance().initSpriteSheets(SpriteSheetLoader.loadSpriteSheets(b));
            TextureManager.getInstance().initAnimations(AnimationLoader.loadAnimations(a));

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }
}
