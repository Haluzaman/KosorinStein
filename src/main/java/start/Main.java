package  start;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        JFrame gamePanel = new JFrame("Hra");
        gamePanel.setResizable(false);
        gamePanel.setContentPane(new GamePanel());
        gamePanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel.setLocationRelativeTo(null);
        gamePanel.setVisible(true);
        gamePanel.pack();
    }
}
