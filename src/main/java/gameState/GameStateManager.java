package  gameState;

import  render.Screen;

import java.util.ArrayList;
import java.util.List;

public class GameStateManager {

    public static int MAIN_MENU_STATE = 0;
    public static int LEVEL_STATE = 1;

    private List<GameState> gameStates;
    private GameState currentState;
    private int currentStateNum;
    private Screen screen;

    private InputHandler inputHandler;

    public GameStateManager(Screen screen, InputHandler inHandler) {
        this.inputHandler = inHandler;
        this.screen = screen;
        initGameStates();
    }

    public void addState(GameState state) {
        gameStates.add(state);
    }

    public void setState(int stateNum) {
        currentStateNum = stateNum;
        currentState = gameStates.get(currentStateNum);
        currentState.init();
    }

    public void draw(double dt) {
        currentState.draw(dt);
    }

    public void update(double dt) {
        currentState.update(dt);
    }

    public Screen getScreen() {
        return this.screen;
    }

    private void initGameStates() {
        this.gameStates = new ArrayList<>();
        this.gameStates.add(new MainMenuState(this, inputHandler, "Main Menu"));
        this.gameStates.add(new LevelState(this, inputHandler, "Level State"));
        this.currentStateNum = 0;
        this.currentState = gameStates.get(currentStateNum);
        this.currentState.init();
    }


}
