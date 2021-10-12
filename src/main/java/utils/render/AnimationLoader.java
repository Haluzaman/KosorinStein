package utils.render;

import graphics.Texture;
import graphics.animation.AnimationRow;
import graphics.animation.IAnimation;
import graphics.animation.SimpleAnimation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.Pair;
import utils.io.FileLoader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AnimationLoader {

    public static Map<String, IAnimation> loadAnimations(String xmlName) {
        return createAnimationsFromMeta(xmlName);
    }

    private static Map<String, IAnimation> createAnimationsFromMeta(String fileName) {
        Map<String, IAnimation> animationMap = new HashMap<>();
        try {
            InputStream xmlFile = FileLoader.readFile(FileLoader.FileType.META, fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList levelNodeList = doc.getElementsByTagName("Animation");
            for(int i = 0; i < levelNodeList.getLength(); i++) {
                Node node = levelNodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Pair<String, IAnimation> spriteSheetEntry = fillAnimationFromElement((Element)levelNodeList.item(i));
                    animationMap.put(spriteSheetEntry.getKey(), spriteSheetEntry.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return animationMap;
    }

    private static Pair<String, IAnimation> fillAnimationFromElement(Element element) {
        String spriteSheetName = element.getElementsByTagName("SpriteSheetName").item(0).getTextContent().replaceAll("\\s+|,","");
        String startRowStr = element.getElementsByTagName("startRow").item(0).getTextContent().replaceAll("\\s+|,","");


        int startRow = Integer.parseInt(startRowStr);
        NodeList states = element.getElementsByTagName("State");

        AnimationRow[] animStates = loadAnimationRows(spriteSheetName, states);
        IAnimation animation = new SimpleAnimation(animStates, startRow);

        return new Pair<>(spriteSheetName, animation);
    }


    private static AnimationRow[] loadAnimationRows(String spriteSheetName, NodeList nodeList) {
        AnimationRow[] row = new AnimationRow[nodeList.getLength()];
        for(int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE) {
                row[i] = fillAnimationRowFromElement(spriteSheetName, (Element) n);
            }
        }

        return row;
    }

    private static AnimationRow fillAnimationRowFromElement(String spriteSheetName,Element element) {
        String timeToNextFrameStr = element.getElementsByTagName("timeToNextFrame").item(0).getTextContent().replaceAll("\\s+|,","");
        String rowPositionsStr = element.getElementsByTagName("rowPositions").item(0).getTextContent().replaceAll("\\s+|,","");
        String colPositionsStr = element.getElementsByTagName("colPositions").item(0).getTextContent().replaceAll("\\s+|,","");
        String numDirectionsStr = element.getElementsByTagName("numDirections").item(0).getTextContent().replaceAll("\\s+|,","");

        int timeToNextFrame = Integer.parseInt(timeToNextFrameStr);
        int numDirections = Integer.parseInt(numDirectionsStr);
        String[] rows = rowPositionsStr.split(";");
        String[] columns = colPositionsStr.split(";");
        if(rows.length != columns.length) {
            System.out.println("Wrong size of rows and columns in animation!");
        }
        Texture[] frames = new Texture[rows.length];
        for (int i = 0; i < rows.length; i++) {
            int row = Integer.parseInt(rows[i]);
            int col = Integer.parseInt(columns[i]);
            frames[i] = TextureManager.getTextureAt(spriteSheetName, row, col);
        }

        return new AnimationRow(frames, 0, timeToNextFrame, numDirections);
    }
}
