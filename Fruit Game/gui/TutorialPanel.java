package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

public class TutorialPanel extends JPanel {

    private static final Color BG          = new Color(245, 232, 190);
    private static final int   TOTAL_PAGES = 7;

    private int    page = 0;
    private JPanel currentCenter;
    private JTextPane textPane;
    private JLabel pageIndicator;
    private JLabel backspaceLbl;

    public TutorialPanel() {
        setBackground(BG);
        setLayout(new BorderLayout());
        setFocusable(true);

        currentCenter = buildPage0Content();
        add(currentCenter, BorderLayout.CENTER);

        pageIndicator = new JLabel("1 / " + TOTAL_PAGES, SwingConstants.CENTER);
        pageIndicator.setFont(GameFont.get(14f));
        pageIndicator.setForeground(new Color(20, 20, 20));
        pageIndicator.setBorder(BorderFactory.createEmptyBorder(5, 0, 8, 0));

        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setOpaque(false);
        southWrapper.add(buildTextBox(), BorderLayout.CENTER);
        southWrapper.add(pageIndicator, BorderLayout.SOUTH);
        add(southWrapper, BorderLayout.SOUTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)     advance();
                else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) goBack();
            }
        });

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void advance() {
        AudioManager.playPageTurn();
        remove(currentCenter);
        page++;
        if (page == 1) {
            currentCenter = buildPage1Content(); setPage1Text();
        } else if (page == 2) {
            currentCenter = buildPage2Content(); setPage2Text();
        } else if (page == 3) {
            currentCenter = buildPage3Content(); setPage3Text();
        } else if (page == 4) {
            currentCenter = buildPage4Content(); setPage4Text();
        } else if (page == 5) {
            currentCenter = buildPage5Content(); setPage5Text();
        } else if (page == 6) {
            currentCenter = buildPage6Content(); setPage6Text();
        } else {
            SceneManager.getInstance().showMenuKeepMusic();
            return;
        }
        add(currentCenter, BorderLayout.CENTER);
        updatePageUI();
        revalidate();
        repaint();
    }

    private void goBack() {
        if (page == 0) return;
        AudioManager.playPageTurn();
        remove(currentCenter);
        page--;
        if (page == 0) {
            currentCenter = buildPage0Content(); setPage0Text();
        } else if (page == 1) {
            currentCenter = buildPage1Content(); setPage1Text();
        } else if (page == 2) {
            currentCenter = buildPage2Content(); setPage2Text();
        } else if (page == 3) {
            currentCenter = buildPage3Content(); setPage3Text();
        } else if (page == 4) {
            currentCenter = buildPage4Content(); setPage4Text();
        }
        add(currentCenter, BorderLayout.CENTER);
        updatePageUI();
        revalidate();
        repaint();
    }

    private void updatePageUI() {
        pageIndicator.setText((page + 1) + " / " + TOTAL_PAGES);
        backspaceLbl.setVisible(page > 0);
        applySpacing(textPane.getStyledDocument());
    }

    // Page 0: player HP vs apple HP
    private JPanel buildPage0Content() {
        JLabel playerImg = loadImage("assets/playertutorialhp.png", 500, 600);
        playerImg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6));
        JLabel appleImg  = loadImage("assets/appletutorialhp.png",  500, 600);
        appleImg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6));
        return twoImageRow(playerImg, appleImg, 180);
    }

    // Page 1: disgust vs fullness meter reaching 0
    private JPanel buildPage1Content() {
        JLabel disgustImg  = loadImage("assets/tutorial0disgust.png",  500, 600);
        disgustImg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6));
        JLabel fullnessImg = loadImage("assets/tutorial0fullness.png", 500, 600);
        fullnessImg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6));
        return twoImageRow(disgustImg, fullnessImg, 180);
    }

    // Page 2: fruit icons above their moveset images
    private JPanel buildPage2Content() {
        JPanel col1 = fruitMovesetColumn("assets/apple.png",      "assets/applemoveset.png");
        JPanel col2 = fruitMovesetColumn("assets/banana.png",     "assets/bananamoveset.png");
        JPanel col3 = fruitMovesetColumn("assets/watermelon.png", "assets/watermelonmoveset.png");

        JPanel row = new JPanel(new GridLayout(1, 3, 30, 0));
        row.setOpaque(false);
        row.add(col1);
        row.add(col2);
        row.add(col3);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(row);
        content.add(Box.createVerticalGlue());
        return content;
    }

    private JPanel fruitMovesetColumn(String fruitPath, String movesetPath) {
        JLabel fruitImg   = loadImage(fruitPath,   150, 150);
        JLabel movesetImg = loadImageTallStretched(movesetPath, 340, 2);
        movesetImg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6));

        JPanel fruitHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        fruitHolder.setOpaque(false);
        fruitHolder.add(fruitImg);

        JPanel movesetHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        movesetHolder.setOpaque(false);
        movesetHolder.add(movesetImg);

        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.add(fruitHolder);
        col.add(movesetHolder);
        return col;
    }

    private static JPanel twoImageRow(JLabel left, JLabel right, int topStrut) {
        JPanel leftHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 0));
        leftHolder.setOpaque(false);
        leftHolder.add(left);

        JPanel rightHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 0));
        rightHolder.setOpaque(false);
        rightHolder.add(right);

        JPanel imgRow = new JPanel(new BorderLayout());
        imgRow.setOpaque(false);
        imgRow.add(leftHolder, BorderLayout.WEST);
        imgRow.add(rightHolder, BorderLayout.EAST);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(Box.createVerticalStrut(topStrut));
        content.add(imgRow);
        content.add(Box.createVerticalGlue());
        return content;
    }

    private JPanel buildTextBox() {
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
        card.setPreferredSize(new Dimension(1200, 230));
        card.setBorder(makeBoxBorder());

        Font f = GameFont.get(20f);

        SimpleAttributeSet base = new SimpleAttributeSet();
        StyleConstants.setFontFamily(base, f.getFamily());
        StyleConstants.setFontSize(base, f.getSize());
        StyleConstants.setForeground(base, new Color(20, 20, 20));

        SimpleAttributeSet fullAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(fullAttr, new Color(255, 215, 0));

        SimpleAttributeSet disgAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(disgAttr, new Color(0, 100, 0));

        SimpleAttributeSet enerAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(enerAttr, new Color(100, 200, 255));

        textPane = new JTextPane();
        textPane.setFont(f);
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.setFocusable(false);
        textPane.setCaretColor(new Color(0, 0, 0, 0));
        textPane.setBorder(BorderFactory.createEmptyBorder(25, 30, 10, 30));
        setPage0Text();
        applySpacing(textPane.getStyledDocument());

        backspaceLbl = new JLabel("▲  Press Backspace to go back");
        backspaceLbl.setFont(GameFont.get(10f));
        backspaceLbl.setForeground(Color.RED);
        backspaceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        backspaceLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 30));
        backspaceLbl.setVisible(false);

        JLabel continueLbl = new JLabel("▼  Press Enter to continue");
        continueLbl.setFont(GameFont.get(10f));
        continueLbl.setForeground(new Color(0, 80, 0));
        continueLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        continueLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 30));

        JPanel bottomLabels = new JPanel(new BorderLayout());
        bottomLabels.setOpaque(false);
        bottomLabels.add(backspaceLbl, BorderLayout.NORTH);
        bottomLabels.add(continueLbl, BorderLayout.SOUTH);

        card.add(textPane, BorderLayout.CENTER);
        card.add(bottomLabels, BorderLayout.SOUTH);
        return card;
    }

    private void setPage0Text() {
        Font f = GameFont.get(20f);

        SimpleAttributeSet base = new SimpleAttributeSet();
        StyleConstants.setFontFamily(base, f.getFamily());
        StyleConstants.setFontSize(base, f.getSize());
        StyleConstants.setForeground(base, new Color(20, 20, 20));

        SimpleAttributeSet fullAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(fullAttr, new Color(255, 215, 0));

        SimpleAttributeSet disgAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(disgAttr, new Color(0, 100, 0));

        SimpleAttributeSet enerAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(enerAttr, new Color(100, 200, 255));

        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(doc.getLength(), "Both players will have 3 bars:\na ", base);
            doc.insertString(doc.getLength(), "fullness", fullAttr);
            doc.insertString(doc.getLength(), " bar, a ", base);
            doc.insertString(doc.getLength(), "disgust", disgAttr);
            doc.insertString(doc.getLength(), " bar, and an ", base);
            doc.insertString(doc.getLength(), "energy", enerAttr);
            doc.insertString(doc.getLength(), " bar.", base);
        } catch (BadLocationException ignored) {}
    }

    private void setPage1Text() {
        Font f = GameFont.get(20f);

        SimpleAttributeSet base = new SimpleAttributeSet();
        StyleConstants.setFontFamily(base, f.getFamily());
        StyleConstants.setFontSize(base, f.getSize());
        StyleConstants.setForeground(base, new Color(20, 20, 20));

        SimpleAttributeSet fullAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(fullAttr, new Color(255, 215, 0));

        SimpleAttributeSet disgAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(disgAttr, new Color(0, 100, 0));

        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(doc.getLength(), "In order to win, your opponents\n", base);
            doc.insertString(doc.getLength(), "fullness", fullAttr);
            doc.insertString(doc.getLength(), " OR ", base);
            doc.insertString(doc.getLength(), "disgust", disgAttr);
            doc.insertString(doc.getLength(), " meter must be at 0.", base);
        } catch (BadLocationException ignored) {}
    }

    private void setPage2Text() {
        Font f = GameFont.get(20f);

        SimpleAttributeSet base = new SimpleAttributeSet();
        StyleConstants.setFontFamily(base, f.getFamily());
        StyleConstants.setFontSize(base, f.getSize());
        StyleConstants.setForeground(base, new Color(20, 20, 20));

        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(doc.getLength(),
                "You have a fruit with a unique moveset,\n" +
                "use these moves to do damage to your opponent's meters.", base);
        } catch (BadLocationException ignored) {}
    }

    private JPanel buildPage3Content() {
        JLabel biteImg = loadImageTallStretched("assets/bite.png", 700, 2);
        biteImg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6));

        JPanel holder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        holder.setOpaque(false);
        holder.add(biteImg);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(Box.createVerticalGlue());
        content.add(holder);
        content.add(Box.createVerticalGlue());
        return content;
    }

    private void setPage3Text() {
        Font f = GameFont.get(20f);

        SimpleAttributeSet base = new SimpleAttributeSet();
        StyleConstants.setFontFamily(base, f.getFamily());
        StyleConstants.setFontSize(base, f.getSize());
        StyleConstants.setForeground(base, new Color(20, 20, 20));

        SimpleAttributeSet fullAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(fullAttr, new Color(255, 215, 0));

        SimpleAttributeSet disgAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(disgAttr, new Color(0, 100, 0));

        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(doc.getLength(), "Bite will be a move in all fruits, in its box\nit will include how much ", base);
            doc.insertString(doc.getLength(), "fullness", fullAttr);
            doc.insertString(doc.getLength(), "/", base);
            doc.insertString(doc.getLength(), "disgust", disgAttr);
            doc.insertString(doc.getLength(), " damage you are doing to your opponent's meter.", base);
        } catch (BadLocationException ignored) {}
    }

    private JPanel buildPage4Content() {
        JLabel bite1 = loadImageTallStretched("assets/bite.png",  360, 1.5);
        JLabel bite2 = loadImageTallStretched("assets/bite2.png", 360, 1.5);
        JLabel bite3 = loadImageTallStretched("assets/bite3.png", 360, 1.5);
        bite1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6));
        bite2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6));
        bite3.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6));

        JPanel h1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        h1.setOpaque(false); h1.add(bite1);
        JPanel h2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        h2.setOpaque(false); h2.add(bite2);
        JPanel h3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        h3.setOpaque(false); h3.add(bite3);

        JPanel row = new JPanel(new GridLayout(1, 3, 30, 0));
        row.setOpaque(false);
        row.add(h1); row.add(h2); row.add(h3);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(Box.createVerticalGlue());
        content.add(row);
        content.add(Box.createVerticalGlue());
        return content;
    }

    private void setPage4Text() {
        Font f = GameFont.get(20f);

        SimpleAttributeSet base = new SimpleAttributeSet();
        StyleConstants.setFontFamily(base, f.getFamily());
        StyleConstants.setFontSize(base, f.getSize());
        StyleConstants.setForeground(base, new Color(20, 20, 20));

        SimpleAttributeSet fullAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(fullAttr, new Color(255, 215, 0));

        SimpleAttributeSet disgAttr = new SimpleAttributeSet(base);
        StyleConstants.setForeground(disgAttr, new Color(0, 100, 0));

        SimpleAttributeSet ripenAttr = new SimpleAttributeSet(base);
        StyleConstants.setBold(ripenAttr, true);

        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(doc.getLength(), "Each fruit will have their own default stats for Bite. Bite's effectiveness on the opponent's meters can change depending on effects made by your fruit. For example, a banana's ", base);
            doc.insertString(doc.getLength(), "ripen", ripenAttr);
            doc.insertString(doc.getLength(), " move would double both ", base);
            doc.insertString(doc.getLength(), "fullness", fullAttr);
            doc.insertString(doc.getLength(), " and ", base);
            doc.insertString(doc.getLength(), "disgust", disgAttr);
            doc.insertString(doc.getLength(), " damage done.", base);
        } catch (BadLocationException ignored) {}
    }

    private JPanel buildPage5Content() {
        JLabel smallBiteImg = loadImageTallStretched("assets/smallbite.png", 700, 2);
        smallBiteImg.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6));

        JPanel holder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        holder.setOpaque(false);
        holder.add(smallBiteImg);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(Box.createVerticalGlue());
        content.add(holder);
        content.add(Box.createVerticalGlue());
        return content;
    }

    private void setPage5Text() {
        Font f = GameFont.get(20f);

        SimpleAttributeSet base = new SimpleAttributeSet();
        StyleConstants.setFontFamily(base, f.getFamily());
        StyleConstants.setFontSize(base, f.getSize());
        StyleConstants.setForeground(base, new Color(20, 20, 20));

        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(doc.getLength(),
                "Players will also have equippable defense cards which cause them to be more resistant to opponent's attacks. For example, small bite will cause for the user to take half the damage if their opponent attacks.",
                base);
        } catch (BadLocationException ignored) {}
    }

    private JPanel buildPage6Content() {
        JLabel face = loadImageTallStretched("assets/happyface.jpg", 600, 0.75);
        face.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6));

        JPanel holder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        holder.setOpaque(false);
        holder.add(face);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(Box.createVerticalGlue());
        content.add(holder);
        content.add(Box.createVerticalGlue());
        return content;
    }

    private void setPage6Text() {
        Font f = GameFont.get(20f);

        SimpleAttributeSet base = new SimpleAttributeSet();
        StyleConstants.setFontFamily(base, f.getFamily());
        StyleConstants.setFontSize(base, f.getSize());
        StyleConstants.setForeground(base, new Color(20, 20, 20));

        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(doc.getLength(), "Tutorial Complete! Happy playing.", base);
        } catch (BadLocationException ignored) {}
    }

    private static void applySpacing(StyledDocument doc) {
        SimpleAttributeSet para = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(para, 0.6f);
        StyleConstants.setSpaceAbove(para, 6f);
        doc.setParagraphAttributes(0, doc.getLength(), para, false);
    }

    private static Border makeBoxBorder() {
        Color dark = new Color(20, 20, 20);
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(dark, 4),
            BorderFactory.createLineBorder(dark, 1)
        );
    }

    private static JLabel loadImageTallStretched(String path, int maxW, double heightMult) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() > 0) {
            double scale = (double) maxW / icon.getIconWidth();
            int w = maxW;
            int h = (int) (icon.getIconHeight() * scale * heightMult);
            Image stretched = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(stretched));
        }
        JLabel fallback = new JLabel("[" + path + "]", SwingConstants.CENTER);
        fallback.setPreferredSize(new Dimension(maxW, (int)(maxW * heightMult)));
        fallback.setForeground(Color.DARK_GRAY);
        return fallback;
    }

    private static JLabel loadImage(String path, int maxW, int maxH) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() > 0) {
            double scale = Math.min((double) maxW / icon.getIconWidth(),
                                    (double) maxH / icon.getIconHeight());
            int w = (int) (icon.getIconWidth()  * scale);
            int h = (int) (icon.getIconHeight() * scale);
            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaled));
        }
        JLabel fallback = new JLabel("[" + path + "]", SwingConstants.CENTER);
        fallback.setPreferredSize(new Dimension(maxW, maxH));
        fallback.setForeground(Color.DARK_GRAY);
        return fallback;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(BG);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
