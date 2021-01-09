package gameState;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class IH implements KeyListener {

    private static int NUM_KEYS = 256;
    private boolean[] keys;


    public IH() {
        this.keys = new boolean[NUM_KEYS];
        Arrays.fill(keys, false);
    }

    public void clear() {
        Arrays.fill(keys, false);
    }

    @Override
    public synchronized void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public synchronized void keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if(keyCode >= 0 && keyCode < 256) {
            this.keys[keyCode] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if(keyCode >= 0 && keyCode < 256) {
            this.keys[keyCode] = false;
        }
    }

    public boolean isKeyPressed(int keyCode) {
        return this.keys[keyCode];
    }

}
