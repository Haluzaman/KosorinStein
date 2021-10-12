package utils.render;

public class MetaInfoLoader {

    private MetaInfoLoader() {
    }

    public static void loadMetaInfo() {
//        final String dirName = "meta/";
//
//        ClassLoader classLoader = MetaInfoLoader.class.getClassLoader();
//        URL resource = classLoader.getResource(dirName);
//        if (resource == null) {
//            System.out.println("file not found! " + dirName);
//            System.exit(1);
//        }
//
////        try {
//        System.out.println(resource.toExternalForm());
//            File file = new File(".");
//        final List<String> metaName = Stream.of(Objects.requireNonNull(file.listFiles()))
//                                    .filter(f -> !f.isDirectory())
//                                    .map(File::getName)
//                                    .collect(Collectors.toList());
//
//            if (metaName.size() < 1) {
//                System.out.println("No metainfo found!");
//                System.exit(1);
//            }

            final String a = "animationMeta.xml";
            final String b = "spriteSheetMeta.xml";

            TextureManager.getInstance().initSpriteSheets(SpriteSheetLoader.loadSpriteSheets(b));
            TextureManager.getInstance().initAnimations(AnimationLoader.loadAnimations(a));
//
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }


    }
}
