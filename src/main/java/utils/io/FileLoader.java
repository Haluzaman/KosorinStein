package utils.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileLoader {

    public enum FileType {
        GRAPHICS ("graphics","png"),
        LEVEL ("levels","xml"),
        LEVEL_TILE_INFO ("levels","Tinfo"),
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

    public static String getPathForFileType(FileType fileType, String fileName) {
        String finalPath;
        if(!fileName.contains(".")) {
            finalPath = String.format("/%s/%s.%s",fileType.getFileType(),fileName,fileType.getFileExtension());
        } else {
            finalPath = String.format("/%s/%s",fileType.getFileType(),fileName);
        }

        System.out.println("Reading file: " + finalPath);
        return finalPath;
    }

    public static BufferedReader getFileAsBufferedReader(FileType fileType, String fileName) throws FileNotFoundException {
        InputStream is = readFile(fileType, fileName);
        if(is == null) {
            System.out.println("Could not read file: " + fileName);
            return null;
        }
        InputStreamReader reader = new InputStreamReader(is);

        return new BufferedReader(reader);
    }

    public static InputStream readFile(FileType fileType, String fileName) {
        String path = getPathForFileType(fileType, fileName);
        InputStream in = FileLoader.class.getResourceAsStream(path);

        if (in == null) {
            System.out.println("InputStream is null for: " + fileName);
            return null;
        }

        return in;
    }

}
