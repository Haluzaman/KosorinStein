package  gameState;

public abstract class GameState {

    protected String name;
    protected GameStateManager gsm;
    protected InputHandler inputHandler;

    public GameState(GameStateManager gsm, InputHandler input, String name){
        this.gsm = gsm;
        this.name = name;
        this.inputHandler = input;
    }

    public String getName() {
        return this.name;
    }

    public abstract void draw(double dt);
    public abstract void update(double dt);
    public abstract void init();
}
