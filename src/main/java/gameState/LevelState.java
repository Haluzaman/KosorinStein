package  gameState;

import  controller.LevelKeyController;
import  level.Level;
import  utils.level.LevelLoader;
import  utils.render.MetaInfoLoader;
import  utils.render.TextureManager;

import java.util.List;

public class LevelState extends GameState {

    private List<Level> levelList;
    private Level currentLevel;

    public LevelState(GameStateManager gsm, InputHandler input, String name) {
        super(gsm, input, name);
        TextureManager.init();
        MetaInfoLoader.loadMetaInfo();
        levelList = LevelLoader.loadLevels("levels.xml");
        levelList.forEach(x -> x.setupRenderer(this, gsm.getScreen(), gsm.getScreen().getGraphics()));
        currentLevel = levelList.get(0);
    }

    @Override
    public void draw(double dt) {
        currentLevel.render(dt);
    }

    @Override
    public void update(double dt) {
        currentLevel.update(dt);
    }

    @Override
    public void init() {
        this.inputHandler.registerController(new LevelKeyController(currentLevel.getKeyResponsives()));
    }


}
