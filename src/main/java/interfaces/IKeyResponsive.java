package  interfaces;

import  gameState.InputHandler;

import java.awt.event.KeyEvent;


public interface IKeyResponsive {
    void onKeyPress(KeyEvent e);
    void onKeyReleased(KeyEvent e);
}
