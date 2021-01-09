package controller;

import interfaces.IKeyController;
import interfaces.IKeyResponsive;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

public class MainMenuKeyController implements IKeyController {

    public List<IKeyResponsive> subjects;

    public MainMenuKeyController(IKeyResponsive subject) {
        subjects = new LinkedList<>();
        subjects.add(subject);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        subjects.forEach((s) -> s.onKeyPress(e));
    }

    @Override
    public void onKeyReleased(KeyEvent e) {
        subjects.forEach((s) -> s.onKeyReleased(e));
    }
}
