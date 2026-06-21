package gui;

import entities.Enemy;
import entities.Player;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class BattlePanel extends JPanel {

    private enum State { TEXT, MOVE_SELECT, GAME_OVER }

    private final Player player;
    private final Enemy appleMan;

    // Player stat UI
    private JLabel pDisgustLbl, pFullnessLbl, pEnergyLbl;
    private JProgressBar pDisgustBar, pFullnessBar, pEnergyBar;

    // Enemy stat UI
    private JLabel aDisgustLbl, aFullnessLbl, aEnergyLbl;
    private JProgressBar aDisgustBar, aFullnessBar, aEnergyBar;

    // Damage indicators
    private DmgIndicator enemyDmgLabel, playerDmgLabel;

    // Bottom panel
    private CardLayout bottomCards;
    private JPanel bottomPanel;
    private JTextArea textLeft;
    private JTextArea textRight;
    private JPanel movesContent;
    private JLabel promptLbl;

    private State state = State.TEXT;
    private final Queue<String[]> msgQueue = new LinkedList<>();
    private boolean pendingGameOver = false;
    private boolean playerWon = false;

    private final Image bgImage = new ImageIcon("assets/battlebackground.png").getImage();

    public BattlePanel(Player player, Enemy enemy) {
        this.player = player;
        this.appleMan = enemy;
        setLayout(new BorderLayout());
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        add(buildField(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && state == State.TEXT) {
                    hideDamageIndicators();
                    showNext();
                }
            }
        });

        msgQueue.add(new String[]{"Apple Man wants to battle!"});
        msgQueue.add(new String[]{"What will " + player.getName() + " do?"});
        showNext();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage.getWidth(null) > 0) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // ── Battle Field ────────────────────────────────────────────────────────

    private JPanel buildField() {
        JPanel field = new JPanel(new BorderLayout());
        field.setOpaque(false);

        enemyDmgLabel = new DmgIndicator();
        JPanel enemyImgCol = new JPanel(new BorderLayout());
        enemyImgCol.setOpaque(false);
        enemyImgCol.add(loadImage("assets/appleMan.png", 200, 200), BorderLayout.CENTER);
        enemyImgCol.add(enemyDmgLabel, BorderLayout.SOUTH);

        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 15));
        northPanel.setOpaque(false);
        northPanel.add(buildStatBox(false));
        northPanel.add(enemyImgCol);

        playerDmgLabel = new DmgIndicator();
        JPanel playerImgCol = new JPanel(new BorderLayout());
        playerImgCol.setOpaque(false);
        playerImgCol.add(loadImage("assets/player.png", 200, 200), BorderLayout.CENTER);
        playerImgCol.add(playerDmgLabel, BorderLayout.SOUTH);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 15));
        southPanel.setOpaque(false);
        southPanel.add(playerImgCol);
        southPanel.add(buildStatBox(true));

        field.add(northPanel, BorderLayout.NORTH);
        field.add(southPanel, BorderLayout.SOUTH);
        return field;
    }

    private JPanel buildStatBox(boolean isPlayer) {
        JLabel dLbl = new JLabel("Disgust: 100");
        JLabel fLbl = new JLabel("Fullness: 100");
        JLabel eLbl = new JLabel("Energy:   100");
        JProgressBar dBar = makeBar(new Color(0, 120, 0));    // dark green
        JProgressBar fBar = makeBar(new Color(255, 215, 0));  // yellow
        JProgressBar eBar = makeBar(new Color(50, 120, 255)); // blue

        Font statFont = GameFont.get(10f);
        dLbl.setFont(statFont);
        fLbl.setFont(statFont);
        eLbl.setFont(statFont);
        dLbl.setForeground(Color.WHITE);
        fLbl.setForeground(Color.WHITE);
        eLbl.setForeground(Color.WHITE);

        if (isPlayer) {
            pDisgustLbl = dLbl; pDisgustBar = dBar;
            pFullnessLbl = fLbl; pFullnessBar = fBar;
            pEnergyLbl = eLbl; pEnergyBar = eBar;
        } else {
            aDisgustLbl = dLbl; aDisgustBar = dBar;
            aFullnessLbl = fLbl; aFullnessBar = fBar;
            aEnergyLbl = eLbl; aEnergyBar = eBar;
        }

        String name = isPlayer ? player.getName() : "Apple Man";
        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(GameFont.get(12f));
        nameLbl.setForeground(Color.WHITE);

        JPanel box = new JPanel(new GridLayout(7, 1, 0, 4));
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        box.setPreferredSize(new Dimension(280, 185));

        box.add(nameLbl);
        box.add(dLbl); box.add(dBar);
        box.add(fLbl); box.add(fBar);
        box.add(eLbl); box.add(eBar);
        return box;
    }

    private JProgressBar makeBar(Color color) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(100);
        bar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            private static final int BLOCK = 14;
            private static final int GAP   = 3;

            @Override
            public void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_OFF);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_SPEED);

                JProgressBar pb = (JProgressBar) c;
                int w = pb.getWidth();
                int h = pb.getHeight();
                int totalBlocks  = (w + GAP) / (BLOCK + GAP);
                int filledBlocks = (int) Math.round(totalBlocks * pb.getPercentComplete());

                Color fg       = pb.getForeground();
                Color hi       = fg.brighter();
                Color shadow   = fg.darker().darker();
                Color emptyBg  = new Color(40, 40, 40);
                Color emptyHi  = new Color(70, 70, 70);

                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, w, h);

                for (int i = 0; i < totalBlocks; i++) {
                    int x = i * (BLOCK + GAP);
                    if (i < filledBlocks) {
                        g2.setColor(fg);
                        g2.fillRect(x, 0, BLOCK, h);
                        g2.setColor(hi);
                        g2.fillRect(x, 0, BLOCK, 2);
                        g2.fillRect(x, 0, 2, h);
                        g2.setColor(shadow);
                        g2.fillRect(x, h - 2, BLOCK, 2);
                        g2.fillRect(x + BLOCK - 2, 0, 2, h);
                    } else {
                        g2.setColor(emptyBg);
                        g2.fillRect(x, 0, BLOCK, h);
                        g2.setColor(emptyHi);
                        g2.fillRect(x, 0, BLOCK, 1);
                        g2.fillRect(x, 0, 1, h);
                    }
                }
            }

            @Override
            public void paintIndeterminate(Graphics g, JComponent c) { paintDeterminate(g, c); }

            @Override
            public Dimension getPreferredSize(JComponent c) { return new Dimension(220, 14); }
        });
        bar.setForeground(color);
        bar.setBackground(Color.BLACK);
        bar.setPreferredSize(new Dimension(220, 14));
        return bar;
    }

    private JLabel loadImage(String path, int w, int h) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() > 0) {
            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaled));
        }
        JLabel fallback = new JLabel("[" + path + "]", SwingConstants.CENTER);
        fallback.setPreferredSize(new Dimension(w, h));
        fallback.setForeground(Color.DARK_GRAY);
        return fallback;
    }

    private static JLabel outlineLbl(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    private void showDamageIndicators(int eDgst, int eFull, int pDgst, int pFull) {
        if (eDgst > 0 || eFull > 0) {
            enemyDmgLabel.setValues(eDgst, eFull);
            enemyDmgLabel.setVisible(true);
        }
        if (pDgst > 0 || pFull > 0) {
            playerDmgLabel.setValues(pDgst, pFull);
            playerDmgLabel.setVisible(true);
        }
    }

    private void hideDamageIndicators() {
        enemyDmgLabel.setVisible(false);
        playerDmgLabel.setVisible(false);
    }

    // ── Bottom Panel ────────────────────────────────────────────────────────

    private JPanel buildBottomPanel() {
        bottomCards = new CardLayout();
        bottomPanel = new JPanel(bottomCards);
        bottomPanel.setPreferredSize(new Dimension(1200, 200));
        bottomPanel.setOpaque(false);
        bottomPanel.add(buildTextCard(), "text");
        bottomPanel.add(buildMovesCard(), "moves");
        return bottomPanel;
    }

    private static Border makeBoxBorder() {
        Color dark = new Color(20, 20, 20);
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(dark, 4),
            BorderFactory.createLineBorder(dark, 1)
        );
    }

    private JPanel buildTextCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(makeBoxBorder());

        textLeft = new JTextArea(" ");
        textLeft.setFont(GameFont.get(16f));
        textLeft.setForeground(new Color(20, 20, 20));
        textLeft.setBorder(BorderFactory.createEmptyBorder(25, 30, 10, 15));
        textLeft.setLineWrap(true);
        textLeft.setWrapStyleWord(true);
        textLeft.setEditable(false);
        textLeft.setFocusable(false);
        textLeft.setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(textLeft, BorderLayout.NORTH);

        textRight = new JTextArea(" ");
        textRight.setFont(GameFont.get(16f));
        textRight.setForeground(new Color(20, 60, 20));
        textRight.setBorder(BorderFactory.createEmptyBorder(25, 15, 10, 30));
        textRight.setLineWrap(true);
        textRight.setWrapStyleWord(true);
        textRight.setEditable(false);
        textRight.setFocusable(false);
        textRight.setOpaque(false);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(textRight, BorderLayout.NORTH);

        JPanel textRow = new JPanel(new GridLayout(1, 2, 2, 0));
        textRow.setOpaque(false);
        textRow.add(leftPanel);
        textRow.add(rightPanel);

        JLabel continueLbl = new JLabel("▼  Press Enter to continue");
        continueLbl.setFont(GameFont.get(10f));
        continueLbl.setForeground(new Color(0, 80, 0));
        continueLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        continueLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 30));

        card.add(textRow, BorderLayout.CENTER);
        card.add(continueLbl, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildMovesCard() {
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(makeBoxBorder());

        promptLbl = new JLabel("<html>What will<br>" + player.getName() + " do?</html>");
        promptLbl.setFont(GameFont.get(16f));
        promptLbl.setForeground(new Color(20, 20, 20));

        movesContent = new JPanel(new GridLayout(3, 2, 8, 8));
        movesContent.setOpaque(false);
        movesContent.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        card.add(movesContent, BorderLayout.CENTER);
        return card;
    }

    // ── Message Queue ───────────────────────────────────────────────────────

    private void showNext() {
        if (msgQueue.isEmpty()) {
            if (pendingGameOver) showGameOverUI();
            else showMoveSelect();
            return;
        }
        String[] pair = msgQueue.poll();
        if (playerWon && pendingGameOver && msgQueue.isEmpty()) AudioManager.playVictory();
        textLeft.setText(pair[0]);
        textRight.setText(pair.length > 1 ? pair[1] : " ");
        bottomCards.show(bottomPanel, "text");
        state = State.TEXT;
        requestFocusInWindow();
    }

    private void showMoveSelect() {
        state = State.MOVE_SELECT;
        hideDamageIndicators();
        promptLbl.setText("<html>What will<br>" + player.getName() + " do?</html>");

        movesContent.removeAll();
        movesContent.setLayout(new GridLayout(3, 2, 8, 8));

        String[] moves = player.getCurrentFruit().getMoveSet();
        for (int i = 0; i < moves.length; i++) {
            final int choice = i + 1;
            movesContent.add(makeMoveBtn(moves[i], choice, player.getCurrentFruit().getEnergyCost(choice), player.getCurrentFruit().getMoveDamage(choice)));
        }
        for (int i = 0; i < player.getDefense().size(); i++) {
            final int choice = moves.length + i + 1;
            movesContent.add(makeMoveBtn(player.getDefense().get(i).getName(), choice, player.getDefense().get(i).getEnergyCost(), null));
        }
        int filled = moves.length + player.getDefense().size();
        while (filled < 6) {
            JPanel blank = new JPanel();
            blank.setOpaque(false);
            movesContent.add(blank);
            filled++;
        }

        movesContent.revalidate();
        movesContent.repaint();
        bottomCards.show(bottomPanel, "moves");
        requestFocusInWindow();
    }

    private JPanel makeMoveBtn(String label, int choice, int energyCost, int[] damage) {
        Color normal = new Color(230, 230, 230);
        Color hover  = new Color(200, 225, 200);

        JPanel btn = new JPanel(new BorderLayout());
        btn.setBackground(normal);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 3),
            BorderFactory.createEmptyBorder(4, 14, 4, 6)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusable(false);

        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(GameFont.get(13f));
        nameLbl.setForeground(new Color(30, 30, 50));

        Font statFont = GameFont.get(10f);
        JLabel costLbl = outlineLbl(energyCost == 0 ? "FREE" : energyCost + " Energy",
                                     statFont, new Color(100, 200, 255));
        costLbl.setFocusable(false);

        // Right side: damage (if any) + energy cost, all on one line, vertically centred
        JPanel eastPanel = new JPanel(new java.awt.GridBagLayout());
        eastPanel.setOpaque(true);
        eastPanel.setBackground(normal);
        eastPanel.setFocusable(false);

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridy  = 0;
        gbc.anchor = java.awt.GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(0, 8, 0, 0);

        if (damage != null && (damage[0] > 0 || damage[1] > 0)) {
            JLabel fullLbl = outlineLbl("-" + damage[1] + " Fullness", statFont, Color.BLACK);
            fullLbl.setFocusable(false);
            gbc.gridx = 0;
            eastPanel.add(fullLbl, gbc);

            JLabel dgstLbl = outlineLbl("-" + damage[0] + " Disgust", statFont, Color.BLACK);
            dgstLbl.setFocusable(false);
            gbc.gridx = 1;
            eastPanel.add(dgstLbl, gbc);

            gbc.gridx = 2;
        } else {
            gbc.gridx = 0;
        }
        eastPanel.add(costLbl, gbc);

        btn.add(nameLbl, BorderLayout.CENTER);
        btn.add(eastPanel, BorderLayout.EAST);

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { executePlayerTurn(choice); }
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
                eastPanel.setBackground(hover);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(normal);
                eastPanel.setBackground(normal);
            }
        };
        btn.addMouseListener(ma);
        nameLbl.addMouseListener(ma);
        costLbl.addMouseListener(ma);
        eastPanel.addMouseListener(ma);
        return btn;
    }

    // ── Battle Logic ────────────────────────────────────────────────────────

    private void executePlayerTurn(int choice) {
        bottomCards.show(bottomPanel, "text");
        state = State.TEXT;

        List<String> playerLines = captureLines(() -> player.getCurrentFruit().executeMove(choice));

        if (!player.isEnoughEnergy()) {
            for (String line : playerLines) msgQueue.add(new String[]{line, " "});
            msgQueue.add(new String[]{"What will " + player.getName() + " do?"});
            showNext();
            return;
        }

        List<String> enemyLines = captureLines(() -> appleMan.chooseRandomMoveApple());
        List<String> defLines   = captureLines(() -> {
            player.getCurrentFruit().applyPassiveEffects();
            player.executeDefense(choice);
        });

        List<String> allPlayer = new ArrayList<>(playerLines);
        allPlayer.addAll(defLines);

        int maxLen = Math.max(allPlayer.size(), enemyLines.size());
        for (int i = 0; i < maxLen; i++) {
            String left  = i < allPlayer.size()   ? allPlayer.get(i)   : " ";
            String right = i < enemyLines.size()  ? enemyLines.get(i)  : " ";
            msgQueue.add(new String[]{left, right});
        }

        int eDgst = appleMan.getDisgustTaken();
        int eFull = appleMan.getFullnessTaken();
        int pDgst = Math.max(0, player.getDisgustTaken() - player.getReducedDisgust());
        int pFull = Math.max(0, player.getFullnessTaken() - player.getReducedFullness());

        boolean wasVulnerable = appleMan.getVulnerable();

        player.endRound();
        appleMan.endRound();
        updateStats();
        showDamageIndicators(eDgst, eFull, pDgst, pFull);

        if (wasVulnerable && !appleMan.getVulnerable()) {
            msgQueue.add(new String[]{player.getName() + "'s Banana unripened!", " "});
        }

        if (!player.isAlive() || !appleMan.isAlive()) {
            if (player.isAlive()) {
                playerWon = true;
            } else {
                AudioManager.stop();
            }
            msgQueue.add(new String[]{player.isAlive() ? "You win!" : "Apple Man wins."});
            pendingGameOver = true;
        } else {
            msgQueue.add(new String[]{"What will " + player.getName() + " do?"});
        }

        showNext();
    }

    private List<String> captureLines(Runnable action) {
        StringBuilder sb = new StringBuilder();
        PrintStream old = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) { sb.append((char) b); }
        }));
        try {
            action.run();
        } finally {
            System.setOut(old);
        }
        List<String> lines = new ArrayList<>();
        for (String line : sb.toString().split("[\\r\\n]+")) {
            if (!line.isBlank()) lines.add(line);
        }
        return lines;
    }

    private void updateStats() {
        int pd = Math.max(player.getDisgust(), 0);
        int pf = Math.max(player.getFullness(), 0);
        int pe = Math.max(player.getEnergy(), 0);
        int ad = Math.max(appleMan.getDisgust(), 0);
        int af = Math.max(appleMan.getFullness(), 0);
        int ae = Math.max(appleMan.getEnergy(), 0);

        pDisgustLbl.setText("Disgust: " + pd);  pDisgustBar.setValue(Math.min(pd, 100));
        pFullnessLbl.setText("Fullness: " + pf); pFullnessBar.setValue(Math.min(pf, 100));
        pEnergyLbl.setText("Energy:   " + pe);   pEnergyBar.setValue(Math.min(pe, 100));

        aDisgustLbl.setText("Disgust: " + ad);  aDisgustBar.setValue(Math.min(ad, 100));
        aFullnessLbl.setText("Fullness: " + af); aFullnessBar.setValue(Math.min(af, 100));
        aEnergyLbl.setText("Energy:   " + ae);   aEnergyBar.setValue(Math.min(ae, 100));
    }

    private void showGameOverUI() {
        state = State.GAME_OVER;
        movesContent.removeAll();
        movesContent.setLayout(new BorderLayout());
        movesContent.setOpaque(false);

        JLabel gameOverLbl = new JLabel("Game Over", SwingConstants.CENTER);
        gameOverLbl.setFont(GameFont.get(24f));
        gameOverLbl.setForeground(new Color(30, 30, 50));
        movesContent.add(gameOverLbl, BorderLayout.CENTER);

        JButton menuBtn = new JButton("Return to Menu");
        menuBtn.setFont(GameFont.get(13f));
        menuBtn.setPreferredSize(new Dimension(240, 60));
        menuBtn.setBackground(new Color(230, 230, 230));
        menuBtn.setForeground(new Color(30, 30, 50));
        menuBtn.setFocusPainted(false);
        menuBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        menuBtn.addActionListener(e -> SceneManager.getInstance().showMenuSilent());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        btnPanel.setOpaque(false);
        btnPanel.add(menuBtn);
        movesContent.add(btnPanel, BorderLayout.SOUTH);

        movesContent.revalidate();
        movesContent.repaint();
        bottomCards.show(bottomPanel, "moves");
    }

    // ── Damage Indicator ────────────────────────────────────────────────────

    private static final class DmgIndicator extends JPanel {
        private static final Font FONT = GameFont.get(14f);
        private int disgust, fullness;

        DmgIndicator() {
            setOpaque(false);
            setPreferredSize(new Dimension(200, 64));
            setVisible(false);
        }

        void setValues(int d, int f) {
            disgust = d;
            fullness = f;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(FONT);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            FontMetrics fm = g2.getFontMetrics();
            int lineH = fm.getAscent() + fm.getDescent() + fm.getLeading();
            int totalH = lineH * 2 + 4;
            int startY = (getHeight() - totalH) / 2 + fm.getAscent();
            if (disgust > 0) {
                String s = "-" + disgust + " disgust";
                drawOutlined(g2, s, (getWidth() - fm.stringWidth(s)) / 2, startY,
                             new Color(0, 100, 0));
            }
            if (fullness > 0) {
                String s = "-" + fullness + " fullness";
                drawOutlined(g2, s, (getWidth() - fm.stringWidth(s)) / 2, startY + lineH + 4,
                             new Color(255, 215, 0));
            }
            g2.dispose();
        }

        private static void drawOutlined(Graphics2D g2, String text, int x, int y, Color fill) {
            g2.setColor(Color.BLACK);
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = -1; dy <= 1; dy++)
                    if (dx != 0 || dy != 0) g2.drawString(text, x + dx, y + dy);
            g2.setColor(fill);
            g2.drawString(text, x, y);
        }
    }
}
