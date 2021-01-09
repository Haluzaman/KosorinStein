package level;

import entities.Door;
import entities.Player;
import gameState.LevelState;
import interfaces.*;
import render.Camera;
import render.LevelRenderer;
import render.Screen;
import utils.level.CollisionHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Level {

    private String name;

    private MapInfo mapInfo;

    private List<ICollidable> collidables;
    private List<IUpdatable> updatables;
    private List<IRenderable> renderables;
    private List<IKeyResponsive> keyResponsives;
    private List<ILightSource> lightSources;

    private Camera camera;
    private LevelRenderer renderer;

    private Map<String, Door> doors;
    private CollisionHandler collisionHandler;
    private Player player;


    public Level(MapInfo mapInfo, String name, Player player, Map<String, Door> doors) {
        this.mapInfo = mapInfo;
        this.name = name;

        this.updatables = new ArrayList<>();
        this.renderables = new ArrayList<>();
        this.keyResponsives = new ArrayList<>();
        this.lightSources = new LinkedList<>();
        this.collidables = new LinkedList<>();

        this.collisionHandler = new CollisionHandler(this);

        this.player = player;
        this.player.setCollisionHandler(this.collisionHandler);
        this.camera = player.getCamera();
        this.updatables.addAll(doors.values());

        this.doors = doors;
        for (Door d : doors.values()) {
            d.setCollisionHandler(this.collisionHandler);
        }
    }

    public void addEntity(Object entity) {
        if(entity instanceof IUpdatable) {
            updatables.add((IUpdatable) entity);
        }

        if(entity instanceof IRenderable) {
            renderables.add((IRenderable) entity);
        }

        if(entity instanceof IKeyResponsive) {
            keyResponsives.add((IKeyResponsive)entity);
        }

        if(entity instanceof ILightSource) {
            lightSources.add((ILightSource) entity);
        }

        if(entity instanceof ICollidable) {
            collidables.add((ICollidable) entity);
        }
    }

    public void removeEntity(Object entity) {
        if(entity instanceof IUpdatable) {
            updatables.remove(entity);
        }

        if(entity instanceof IRenderable) {
            renderables.remove(entity);
        }

        if(entity instanceof IKeyResponsive) {
            keyResponsives.remove(entity);
        }

        if(entity instanceof ILightSource) {
            lightSources.remove(entity);
        }

        if(entity instanceof ICollidable) {
            collidables.remove(entity);
        }
    }

    public void update(double dt) {
        updatables.forEach(x -> x.update(dt, this));
    }

    public void render(double dt) {
        renderer.renderLevel(dt);
    }

    public void setMapInfo(MapInfo map) { this.mapInfo = map; }
    public MapInfo getMapInfo() { return this.mapInfo; }
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
    public Camera getCamera() { return this.camera; }

    public void setupRenderer(LevelState state, Screen screen, Graphics2D g2d) {
        this.renderer = new LevelRenderer(this, screen, g2d, camera);
    }

    public List<IRenderable> getRenderables() { return this.renderables; }

    public List<IKeyResponsive> getKeyResponsives() { return this.keyResponsives; }

    public List<ILightSource> getLightSources() { return this.lightSources; }

    public List<ICollidable> getCollidables() { return this.collidables; }

    public CollisionHandler getCollisionHandler() { return this.collisionHandler; }

    public Door getDoor(int x, int y) {
        return doors.get(String.valueOf(x) + y);
    }

    public Player getPlayer() { return this.player; }

}
