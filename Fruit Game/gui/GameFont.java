package gui;

import java.awt.*;
import java.io.*;

public class GameFont {

    private static final Font BASE;

    static {
        Font f;
        try {
            f = Font.createFont(Font.TRUETYPE_FONT, new File("assets/PressStart2P.ttf"));
        } catch (Exception e) {
            f = new Font("Courier New", Font.BOLD, 12);
        }
        BASE = f;
    }

    public static Font get(float size) {
        return BASE.deriveFont(Font.PLAIN, size);
    }
}
