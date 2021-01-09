package utils.render;

import graphics.SpriteSheet;
import graphics.Texture;
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpriteSheetLoader {

    private SpriteSheetLoader() {

    }

    public static Map<String, SpriteSheet> loadSpriteSheets(String fileName) {
        return createSpriteSheetsFromMeta(fileName);
    }


    private static Map<String, SpriteSheet> createSpriteSheetsFromMeta(String fileName) {
        Map<String, SpriteSheet> spriteSheetMap = new HashMap<>();
        try {
            File xmlFile = new File("res/meta/" + fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList levelNodeList = doc.getElementsByTagName("SpriteSheet");
            for(int i = 0; i < levelNodeList.getLength(); i++) {
                Node node = levelNodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Pair<String, SpriteSheet> spriteSheetEntry = fillNormalSpriteSheetFromElement((Element)node);
                    spriteSheetMap.put(spriteSheetEntry.getKey(), spriteSheetEntry.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return spriteSheetMap;
    }

//    private static Pair<String, SpriteSheet> fillSpriteSheetFromNode(Node node) {
//        Element element = (Element) node;
//
//        String type = element.getElementsByTagName("type").item(0).getTextContent().replaceAll("\\s+|,","");
//
//    }


    private static String getStrElementByTagName(Element e, String name, int at) {
        return e.getElementsByTagName(name).item(at).getTextContent().replaceAll("\\s+|","");
    }

    private static String getStrElementByTagName(Element e, String name) {
        return e.getElementsByTagName(name).item(0).getTextContent().replaceAll("\\s+|","");
    }

    private static int getIntElementByTagName(Element e, String name) {
       String elStr = e.getElementsByTagName(name).item(0).getTextContent().replaceAll("\\s+|","");
       int elVal = Integer.parseInt(elStr);
       return elVal;
    }

    private static Pair<String, SpriteSheet> fillNormalSpriteSheetFromElement(Element element) {
        String name = element.getElementsByTagName("name").item(0).getTextContent().replaceAll("\\s+|,","");
        String pathToSpriteSheet = element.getElementsByTagName("path").item(0).getTextContent().replaceAll("\\s+|,","");
        String numRowsStr = element.getElementsByTagName("numRows").item(0).getTextContent().replaceAll("\\s+|,","");
        String numColsStr = element.getElementsByTagName("numCols").item(0).getTextContent().replaceAll("\\s+|,","");
        String spriteWidthStr = element.getElementsByTagName("spriteWidth").item(0).getTextContent().replaceAll("\\s+|,","");
        String spriteHeightStr = element.getElementsByTagName("spriteHeight").item(0).getTextContent().replaceAll("\\s+|,","");



        int spriteWidth = Integer.parseInt(spriteWidthStr);
        int spriteHeight = Integer.parseInt(spriteHeightStr);
        int numRows = Integer.parseInt(numRowsStr);
        String[] numColsStrArr = numColsStr.split(";");
        int[] numCols = new int[numColsStrArr.length];

        for(int i = 0; i < numColsStrArr.length; i++) {
            numCols[i] = Integer.parseInt(numColsStrArr[i]);
        }

        SpriteSheet spriteSheet = loadSpriteSheet(pathToSpriteSheet, numCols, numRows, spriteWidth, spriteHeight);
        if(spriteSheet == null) {
            System.out.println("Could not find spriteSheet: " + name);
            System.exit(1);
        } else {
            System.out.println("Loaded spriteSheet: " + name);
        }
        return new Pair<>(name, spriteSheet);
    }

    private static SpriteSheet loadSpriteSheet(String path, int[] numCols, int numRows, int spriteWidth, int spriteHeight) {
        SpriteSheet spriteSheet;

        try{
            File img = new File(path);
            BufferedImage image = ImageIO.read(img);
            Texture[][] atlas = parseSpriteSheet(image, numCols, numRows, spriteWidth, spriteHeight);
            spriteSheet = new SpriteSheet(image, atlas);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }

        return spriteSheet;
    }

    private static Texture[][] parseSpriteSheet(BufferedImage image, int[] numCols, int numRows, int spriteWidth, int spriteHeight) {
        Texture[][] textures = new Texture[numRows][];

        for(int row = 0; row < numRows; row++) {
            textures[row] = new Texture[numCols[row]];
            for(int col = 0; col < numCols[row]; col++) {
                textures[row][col] = new Texture(image.getSubimage(col * spriteWidth, row * spriteHeight, spriteWidth, spriteHeight));
            }
        }

        return textures;
    }
}
