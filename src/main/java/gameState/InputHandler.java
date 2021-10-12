package  gameState;

import  interfaces.IKeyController;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

    IKeyController controller;

    public InputHandler() { }

    public InputHandler(IKeyController c) {
        this.controller = c;
    }

    public void registerController(IKeyController controller) {
        this.controller = controller;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        controller.onKeyPressed(keyEvent);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        controller.onKeyReleased(keyEvent);
    }

}
