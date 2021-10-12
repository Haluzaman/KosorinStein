package utils.level;

import entities.*;
import entities.weapons.Knife;
import entities.weapons.Pistol;
import entities.weapons.SubmachineGun;
import entities.weapons.Weapon;
import graphics.LightSourceProperty;
import graphics.animation.IAnimation;
import graphics.effects.IEffect;
import graphics.effects.PulsatingEffect;
import level.Level;
import level.MapInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import render.Camera;
import utils.io.FileLoader;
import utils.math.Vector2d;
import utils.render.RenderContext;
import utils.render.TextureManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

public class LevelLoader {

    private LevelLoader() { }

    public static List<Level> loadLevels(String lvlName) {
        List<Level> levels = new ArrayList<>();
        try {
//            String fileName = "main/res/levels/" + lvlName;
//            File xmlFile = new File(fileName);
            InputStream xmlFile = FileLoader.readFile(FileLoader.FileType.LEVEL, lvlName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList levelNodeList = doc.getElementsByTagName("Level");
            for(int i = 0; i < levelNodeList.getLength(); i++) {
                Node node = levelNodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Level level = fillLevelFromXmlNode(levelNodeList.item(0));
                    levels.add(level);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        LevelDeserializer d = new LevelDeserializer("level1", 20,20);
        d.parseMap();
        return levels;
    }


    private static Level fillLevelFromXmlNode(Node levelNode) {
        Element levelElement = (Element) levelNode;

        String widthStr = levelElement.getElementsByTagName("width").item(0).getTextContent().trim();
        String heightStr = levelElement.getElementsByTagName("height").item(0).getTextContent().trim();
        String levelStr = levelElement.getElementsByTagName("map").item(0).getTextContent().replaceAll("\\s+|,","");
        String levelName = levelElement.getElementsByTagName("name").item(0).getTextContent().trim();

        char[] chMap = levelStr.toCharArray();
        int[] map = new int[chMap.length];
        Map<String, Door> doors = new HashMap<>();

        for(int i = 0; i < chMap.length; i++) {
            map[i] = chMap[i] - '0';
        }

        int width = Integer.parseInt(widthStr);
        int height = Integer.parseInt(heightStr);

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                int pos = j + i * width;
                if(map[pos] == 2) {
                    Vector2d doorPos = new Vector2d(j, i);
                    Vector2d leftPos = new Vector2d(j, i + 0.5);
                    Vector2d rightPos = new Vector2d(j + 1.0, i + 0.5);
                    Door door = new Door(doorPos, leftPos, rightPos, TextureManager.getInstance().getTextureByName("door"));
                    String hash = String.valueOf(j) + i;
                    doors.put(hash, door);
                }
            }
        }

        Camera camera = parseCameraFromLevelNode(levelElement.getElementsByTagName("camera").item(0));
        Player player = new Player(camera.position, camera.direction, camera, new LightSourceProperty(camera.position, 0.3, 0.3, 0.3, 2));


        MapInfo mapInfo = new LevelDeserializer(levelName, width, height).parseMap();

        Level level = new Level(mapInfo, levelName, player, doors);

        Node nodeEntities = levelElement.getElementsByTagName("entities").item(0);
        if(nodeEntities != null) {
            if( nodeEntities.getNodeType() == Node.ELEMENT_NODE) {
                NodeList el = ((Element) nodeEntities).getElementsByTagName("entity");
                parseEntitiesFromLevelNodeList(el).forEach(level::addEntity);
           }
        }

        Node weaponEntities = levelElement.getElementsByTagName("weapons").item(0);
        if(weaponEntities != null) {
            if( weaponEntities.getNodeType() == Node.ELEMENT_NODE) {
                NodeList el = ((Element) weaponEntities).getElementsByTagName("weapon");
                parseWeaponsFromLevelNodeList(el).forEach(player::addWeapon);
            }
        }

        level.addEntity(player);
        return level;
    }


    private static Camera parseCameraFromLevelNode(Node cameraNode) {
        Element cameraElement = (Element) cameraNode;

        double cameraPosX = Double.parseDouble(cameraElement.getElementsByTagName("x").item(0).getTextContent());
        double cameraPosY = Double.parseDouble(cameraElement.getElementsByTagName("y").item(0).getTextContent());
        double directionX = Double.parseDouble(cameraElement.getElementsByTagName("directionX").item(0).getTextContent());
        double directionY = Double.parseDouble(cameraElement.getElementsByTagName("directionY").item(0).getTextContent());
        double planeX = Double.parseDouble(cameraElement.getElementsByTagName("planeX").item(0).getTextContent());
        double planeY = Double.parseDouble(cameraElement.getElementsByTagName("planeY").item(0).getTextContent());

        return new Camera(cameraPosX, cameraPosY, directionX, directionY, planeX, planeY);
    }

    private static LightSourceProperty parseLightSrcPropertyFromNode(Node lightPropNode) {
        Element lightElement = (Element) lightPropNode;

        if(lightElement == null) return null;

        double posX = Double.parseDouble(lightElement.getElementsByTagName("x").item(0).getTextContent());
        double posY = Double.parseDouble(lightElement.getElementsByTagName("y").item(0).getTextContent());
        double red = Double.parseDouble(lightElement.getElementsByTagName("red").item(0).getTextContent());
        double green = Double.parseDouble(lightElement.getElementsByTagName("green").item(0).getTextContent());
        double blue = Double.parseDouble(lightElement.getElementsByTagName("blue").item(0).getTextContent());
        double attentuation = Double.parseDouble(lightElement.getElementsByTagName("attentuation").item(0).getTextContent());

        return new LightSourceProperty(new Vector2d(posX, posY), red, green, blue, attentuation);
    }

    private static List<Object> parseEntitiesFromLevelNodeList(NodeList entitiesNodeList) {
        List<Object> entities = new LinkedList<>();

        for(int i = 0; i < entitiesNodeList.getLength(); i++) {
            Node node = entitiesNodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                entities.add(fillEntityFromEntityNode(node));
            }
        }

        return entities;
    }

    private static Object fillEntityFromEntityNode(Node node) {
        Element el = (Element) node;
        Object entity = null;

        String entityName = el.getElementsByTagName("name").item(0).getTextContent().trim();
        String textureName = el.getElementsByTagName("textureName").item(0).getTextContent().trim();
        String strPositionX = el.getElementsByTagName("positionX").item(0).getTextContent().trim();
        String strPositionY = el.getElementsByTagName("positionY").item(0).getTextContent().trim();

        Node lightPropertiesNode = el.getElementsByTagName("lightProperties").item(0);
        LightSourceProperty lightProp = parseLightSrcPropertyFromNode(lightPropertiesNode);

        if(entityName.equalsIgnoreCase("barell")) {
            IAnimation animation = TextureManager.getAnimationByName(textureName);
            Vector2d position = new Vector2d(Double.parseDouble(strPositionX), Double.parseDouble(strPositionY));
            RenderContext context = new RenderContext(position, animation, true);

            entity = new Barell(position, context);
        } else if(entityName.equalsIgnoreCase("lamp")) {
            IAnimation anim = TextureManager.getAnimationByName(textureName);
            Vector2d position = new Vector2d(Double.parseDouble(strPositionX), Double.parseDouble(strPositionY));
            RenderContext renderContext = new RenderContext(position, anim, false);
            IEffect e = new PulsatingEffect(0.5, 10, 0.02);
            entity = new Lamp(position, renderContext, lightProp, e);
        } else if(entityName.equalsIgnoreCase("guard")) {
            IAnimation anim = TextureManager.getAnimationByName(textureName);
            Vector2d position = new Vector2d(Double.parseDouble(strPositionX), Double.parseDouble(strPositionY));
            RenderContext renderContext = new RenderContext(position, anim, true);
            entity = new Guard(position, renderContext);
        }

        return entity;
    }

    private static List<Weapon> parseWeaponsFromLevelNodeList(NodeList weaponsNodeList) {
        List<Weapon> entities = new LinkedList<>();

        for(int i = 0; i < weaponsNodeList.getLength(); i++) {
            Node node = weaponsNodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                entities.add(fillWeaponFromEntityNode(node));
            }
        }

        return entities;
    }


    private static Weapon fillWeaponFromEntityNode(Node node) {
        Element el = (Element) node;
        Weapon weapon = null;

        String weaponName = el.getElementsByTagName("name").item(0).getTextContent().trim();
        String idStr = el.getElementsByTagName("id").item(0).getTextContent().trim();
        String damageStr = el.getElementsByTagName("damage").item(0).getTextContent().trim();
        String ammoStr = el.getElementsByTagName("ammo").item(0).getTextContent().trim();

        int id = Integer.parseInt(idStr);
        int damage = Integer.parseInt(damageStr);
        int ammo = Integer.parseInt(ammoStr);
        IAnimation anim = TextureManager.getAnimationByName(weaponName);

        if(weaponName.equalsIgnoreCase("pistol")) {
            weapon = new Pistol(id, damage, ammo, anim);
        } else if(weaponName.equalsIgnoreCase("knife")) {
            weapon = new Knife(id, damage, ammo, anim);
        } else if(weaponName.equalsIgnoreCase("submachinegun")) {
            weapon = new SubmachineGun(id, damage, ammo, anim);
        } else {
            System.out.println("Do not know weapon of name: " + weaponName);
            System.exit(1);
        }

        return weapon;
    }

}
