package render;

import gameState.MainMenuState;

import java.awt.*;
import java.util.List;

public class MainMenuRenderer {

    MainMenuState menuState;
    Graphics2D graphics;
    Font selectedFont, normalFont;

    public MainMenuRenderer(MainMenuState state, Graphics2D g2d) {
        menuState = state;
        graphics = g2d;
        normalFont = graphics.getFont();
        selectedFont = normalFont.deriveFont(normalFont.getSize() * 1.4f);
    }

    public void draw(double dt) {
        List<String> opts = menuState.getOptions();

        for(int i = 0; i < opts.size(); i++) {

            if(menuState.getSelectedOptionIndex() == i) {
                graphics.setFont(selectedFont);
                graphics.setColor(Color.YELLOW);
            } else {
                graphics.setFont(normalFont);
                graphics.setColor(Color.WHITE);
            }

           graphics.drawString(opts.get(i), 50, 100 + i * 30);
        }
    }
}
