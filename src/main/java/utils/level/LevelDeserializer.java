package utils.level;

import graphics.Texture;
import level.MapInfo;
import level.Tile;
import utils.io.FileLoader;
import utils.render.TextureManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelDeserializer {

    private String fileName;
    private int width;
    private int height;

    public LevelDeserializer(String fileName, int w, int h) {
        this.fileName = fileName;
        this.width = w;
        this.height = h;
    }

    public MapInfo parseMap() {
        //        String fullPath = "main/res/levels/" + this.fileName + ".Tinfo";
        try (BufferedReader r = FileLoader.getFileAsBufferedReader(FileLoader.FileType.LEVEL_TILE_INFO, this.fileName)) {
            Tile[] tiles = getTileMap(r);
            return new MapInfo(width, height, tiles);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Tile[] getTileMap(BufferedReader r) throws IOException {
        List<Tile> tileMap = new ArrayList<>(width * height);
        String regex = "-";
        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                String xStr = r.readLine().trim().split(regex)[1];
                int x = Integer.parseInt(xStr);


                String yStr = r.readLine().trim().split(regex)[1];
                int y = Integer.parseInt(yStr);


                String typeStr = r.readLine().trim().split(regex)[1];
                int type = Integer.parseInt(typeStr);

                String ceilingTex = r.readLine().trim().split(regex)[1];
                Texture ceilingTexture = TextureManager.getInstance().getTextureByName(ceilingTex);


                String wallTex = r.readLine().trim().split(regex)[1];
                Texture wallTexture = TextureManager.getInstance().getTextureByName(wallTex);


                String floorTex = r.readLine().trim().split(regex)[1];
                Texture floorTexture = TextureManager.getInstance().getTextureByName(floorTex);

                String heightStr = r.readLine().trim().split(regex)[1];
                double height = Double.parseDouble(heightStr);

                Tile t = new Tile(x, y, height, type, wallTexture, floorTexture, ceilingTexture);
                tileMap.add(t);

                r.readLine();
            }
        }

        return tileMap.toArray(new Tile[0]);
    }


}
