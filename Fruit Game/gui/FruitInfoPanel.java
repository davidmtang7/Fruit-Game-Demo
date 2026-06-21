package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import javax.swing.*;
import javax.swing.border.*;

public class FruitInfoPanel extends JPanel {
    private JPanel selectedCard = null;
    private OutlinedLabel statusLabel;
    private JButton nextBtn;
    private String selectedFruitName = null;
    private final Image bgImage = new ImageIcon("assets/fruitselection.png").getImage();

    private static final Border DEFAULT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(180, 180, 180, 120), 2),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    );
    private static final Border SELECTED_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(50, 200, 50), 3),
        BorderFactory.createEmptyBorder(4, 4, 4, 4)
    );

    public FruitInfoPanel() {
        setLayout(new BorderLayout());

        OutlinedLabel title = new OutlinedLabel("Select your fruit", JLabel.CENTER);
        title.setFont(GameFont.get(24f));
        title.setForeground(Color.WHITE);
        title.setOpaque(false);
        title.setBorder(BorderFactory.createEmptyBorder(90, 0, 10, 0));

        JPanel fruitsPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        fruitsPanel.setOpaque(false);
        fruitsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        fruitsPanel.add(makeFruitCard("assets/apple.png", "Apple", true));
        fruitsPanel.add(makeFruitCard("assets/banana.png", "Banana", true));
        fruitsPanel.add(makeFruitCard("assets/watermelon.png", "Watermelon", true));

        statusLabel = new OutlinedLabel(" ", JLabel.CENTER);
        statusLabel.setFont(GameFont.get(10f));
        statusLabel.setForeground(new Color(255, 80, 80));
        statusLabel.setOpaque(false);
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);

        OutlinedLabel nameLabel = new OutlinedLabel("Your Name:");
        nameLabel.setFont(GameFont.get(16f));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setOpaque(false);

        JTextField nameField = new JTextField(12);
        nameField.setFont(GameFont.get(16f));
        nameField.setPreferredSize(new Dimension(300, 50));

        nextBtn = new JButton("Next ▶");
        nextBtn.setFont(GameFont.get(16f));
        nextBtn.setPreferredSize(new Dimension(280, 70));
        nextBtn.setEnabled(false);
        nextBtn.addActionListener(e -> SceneManager.getInstance().showBattle(nameField.getText().trim(), selectedFruitName));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        nameRow.setOpaque(false);
        nameRow.add(nameLabel);
        nameRow.add(nameField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setOpaque(false);
        btnRow.add(nextBtn);

        JPanel nameSection = new JPanel();
        nameSection.setLayout(new BoxLayout(nameSection, BoxLayout.Y_AXIS));
        nameSection.setOpaque(false);
        nameSection.add(statusLabel);
        nameSection.add(Box.createVerticalStrut(10));
        nameSection.add(nameRow);
        nameSection.add(Box.createVerticalStrut(18));
        nameSection.add(btnRow);

        JPanel fruitsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        fruitsRow.setOpaque(false);
        fruitsRow.add(fruitsPanel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        centerPanel.add(fruitsRow);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(nameSection);
        centerPanel.add(Box.createVerticalGlue());

        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private JPanel makeFruitCard(String imagePath, String fruitName, boolean available) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(DEFAULT_BORDER);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ImageIcon icon = new ImageIcon(imagePath);
        Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaled));
        imageLabel.setAlignmentX(CENTER_ALIGNMENT);

        OutlinedLabel nameLabel = new OutlinedLabel(fruitName, JLabel.CENTER);
        nameLabel.setFont(GameFont.get(10f));
        nameLabel.setForeground(available ? Color.WHITE : new Color(200, 200, 200));
        nameLabel.setOpaque(false);
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);

        card.add(imageLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(nameLabel);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (available) {
                    if (selectedCard != null) selectedCard.setBorder(DEFAULT_BORDER);
                    card.setBorder(SELECTED_BORDER);
                    selectedCard = card;
                    selectedFruitName = fruitName;
                    statusLabel.setText(" ");
                    nextBtn.setEnabled(true);
                } else {
                    statusLabel.setText(fruitName + " isn't available yet!");
                }
            }
        });

        return card;
    }

    private static class OutlinedLabel extends JLabel {
        OutlinedLabel(String text, int alignment) { super(text, alignment); }
        OutlinedLabel(String text) { super(text); }

        @Override
        protected void paintComponent(Graphics g) {
            String text = getText();
            if (text == null || text.isBlank()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(getFont());

            FontMetrics fm = g2.getFontMetrics();
            Insets ins = getInsets();
            int w = getWidth() - ins.left - ins.right;
            int h = getHeight() - ins.top - ins.bottom;
            int textW = fm.stringWidth(text);

            int x;
            switch (getHorizontalAlignment()) {
                case CENTER: x = ins.left + (w - textW) / 2; break;
                case RIGHT:  x = ins.left + w - textW; break;
                default:     x = ins.left; break;
            }
            int y = ins.top + (h - fm.getHeight()) / 2 + fm.getAscent();

            var frc = g2.getFontRenderContext();
            var gv = getFont().createGlyphVector(frc, text);
            Shape shape = gv.getOutline(x, y);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(shape);
            g2.setColor(getForeground());
            g2.fill(shape);
            g2.dispose();
        }
    }
}
