package utils.io;

import java.io.InputStream;

public class FileReader {

    public enum FileType {
        GRAPHICS ("graphics","png"),
        LEVEL ("level","xml"),
        LEVEL_TILE_INFO ("level","Tinfo"),
        META ("meta","xml");

        private final String type;
        private final String extension;

        FileType(String type, String extension) {
            this.type = type;
            this.extension = extension;
        }

        public String getFileType() {
            return this.type;
        }

        public String getFileExtension() {
            return this.extension;
        }
    }

    private static String getPathForFileType(FileType fileType, String fileName) {
        String finalPath;
        if(!fileName.contains(".")) {
            finalPath = String.format("/%s/%s.%s",fileType.getFileType(),fileName,fileType.getFileExtension());
        } else {
            finalPath = String.format("/%s/%s",fileType.getFileType(),fileName);
        }

        System.out.println("Reading file: " + finalPath);
        return finalPath;
    }

    public static InputStream readFile(FileType fileType, String fileName) {
        String path = getPathForFileType(fileType, fileName);
        InputStream in = FileReader.class.getResourceAsStream(path);

        if (in == null) {
            System.out.println("InputStream is null for: " + fileName);
            return null;
        }

        return in;
    }

}
