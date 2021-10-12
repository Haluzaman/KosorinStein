package  gameState;

import  controller.MainMenuKeyController;
import  interfaces.IKeyController;
import  interfaces.IKeyResponsive;
import  render.MainMenuRenderer;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class MainMenuState extends GameState implements IKeyResponsive {

    private static String START_GAME_STR = "START";
    private static String EXIT_STR = "EXIT";

    private List<String> options;
    private int selectedOptionIndex;
    private MainMenuRenderer renderer;

    public MainMenuState(GameStateManager gsm, InputHandler input, String name) {
        super(gsm, input, name);
        renderer = new MainMenuRenderer(this, gsm.getScreen().getGraphics());
        options = new ArrayList<>();
        options.add(START_GAME_STR);
        options.add(EXIT_STR);
        selectedOptionIndex = 0;
    }

    @Override
    public void draw(double dt) {
        renderer.draw(dt);
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void init() {
        inputHandler.registerController(new MainMenuKeyController(this));
    }

    @Override
    public void onKeyPress(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.VK_UP) {
            selectedOptionIndex--;
            if(selectedOptionIndex < 0)
                selectedOptionIndex = options.size() - 1;
        } else if(event.getKeyCode() == KeyEvent.VK_DOWN) {
            selectedOptionIndex++;
            if(selectedOptionIndex == options.size())
                selectedOptionIndex = 0;
        } else if(event.getKeyCode() == KeyEvent.VK_ENTER) {
            if(options.get(selectedOptionIndex).equalsIgnoreCase(EXIT_STR)) {
                System.out.println("Exiting!");
                System.exit(0);
            } else if(options.get(selectedOptionIndex).equalsIgnoreCase(START_GAME_STR)) {
                System.out.println("Start Level!");
                gsm.setState(GameStateManager.LEVEL_STATE);
            }
        }
    }

    @Override
    public void onKeyReleased(KeyEvent event) {

    }


    public List<String> getOptions() {
        return options;
    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }
}
