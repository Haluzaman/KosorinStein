package utils.io;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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

    public static File getFile(FileType fileType, String filename) {
        String finalPath;

        if(filename.contains(".")) {
            finalPath = String.format("%s/%s",fileType.getFileType(), filename);
        } else {
            finalPath = String.format("%s/%s.%s",fileType.getFileType(), filename, fileType.getFileExtension());
        }

        try {
            URL resource = FileLoader.class.getClassLoader().getResource(finalPath);
            URI uri = resource.toURI();
            return new File(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
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
