package gui;

// GamePanel.java
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel {
    private Timer gameLoop;

    public GamePanel() {
        setBackground(Color.DARK_GRAY);
    }

    public void startLoop() {
        gameLoop = new Timer(16, e -> {
            update();
            repaint();
        });
        gameLoop.start();
    }

    private void update() {
        // game logic goes here
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // drawing goes here
    }
}