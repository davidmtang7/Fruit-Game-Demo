package gui;

import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private final Image bg = new ImageIcon("assets/start.png").getImage();

    public MenuPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Fruit Game", JLabel.CENTER);
        title.setFont(GameFont.get(52f));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(300, 0, 0, 0));
        title.setOpaque(false);

        boolean[] confirmed    = {false};
        boolean[] tutConfirmed = {false};

        JButton startBtn = new JButton("Start") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
                if (confirmed[0]) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setColor(Color.YELLOW);
                    g3.setStroke(new BasicStroke(4f));
                    g3.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
                    g3.dispose();
                }
            }
        };
        startBtn.setFont(GameFont.get(28f));
        startBtn.setPreferredSize(new Dimension(480, 130));
        startBtn.setMaximumSize(new Dimension(480, 130));
        startBtn.setForeground(Color.WHITE);
        startBtn.setBackground(Color.BLACK);
        startBtn.setOpaque(false);
        startBtn.setContentAreaFilled(false);
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);

        JButton tutorialBtn = new JButton("Tutorial") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
                if (tutConfirmed[0]) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setColor(Color.YELLOW);
                    g3.setStroke(new BasicStroke(4f));
                    g3.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
                    g3.dispose();
                }
            }
        };
        tutorialBtn.setFont(GameFont.get(28f));
        tutorialBtn.setPreferredSize(new Dimension(480, 130));
        tutorialBtn.setMaximumSize(new Dimension(480, 130));
        tutorialBtn.setForeground(Color.WHITE);
        tutorialBtn.setBackground(Color.BLACK);
        tutorialBtn.setOpaque(false);
        tutorialBtn.setContentAreaFilled(false);
        tutorialBtn.setFocusPainted(false);
        tutorialBtn.setBorderPainted(false);

        // Listeners added after both buttons exist so each can reference the other
        startBtn.addActionListener(e -> {
            if (!confirmed[0]) {
                confirmed[0]    = true;
                tutConfirmed[0] = false;
                startBtn.repaint();
                tutorialBtn.repaint();
            } else {
                SceneManager.getInstance().showFruitInfo();
            }
        });

        tutorialBtn.addActionListener(e -> {
            if (!tutConfirmed[0]) {
                tutConfirmed[0] = true;
                confirmed[0]    = false;
                tutorialBtn.repaint();
                startBtn.repaint();
            } else {
                SceneManager.getInstance().showTutorial();
            }
        });

        JPanel startRow    = centeredRow(startBtn);
        JPanel tutorialRow = centeredRow(tutorialBtn);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(startRow);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(tutorialRow);
        centerPanel.add(Box.createVerticalGlue());

        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private static JPanel centeredRow(JButton btn) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        row.setOpaque(false);
        row.add(btn);
        return row;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bg.getWidth(null) > 0) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
