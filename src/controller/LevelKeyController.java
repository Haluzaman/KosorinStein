package controller;

import interfaces.IKeyController;
import interfaces.IKeyResponsive;

import java.awt.event.KeyEvent;
import java.util.List;

public class LevelKeyController implements IKeyController {

    private List<IKeyResponsive> subjects;

    public LevelKeyController(List<IKeyResponsive> responsives) {
        this.subjects = responsives;
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        this.subjects.forEach((x) -> x.onKeyPress(e));
    }

    @Override
    public void onKeyReleased(KeyEvent e) {
        this.subjects.forEach((x) -> x.onKeyReleased(e));
    }
}
