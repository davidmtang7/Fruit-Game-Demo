package gui;

import java.io.IOException;

public class AudioManager {

    private static volatile Thread audioThread;
    private static volatile Process process;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(AudioManager::stop));
    }

    private static void play(String path) {
        stop();
        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Process p = new ProcessBuilder("afplay", path).start();
                    process = p;
                    p.waitFor();
                } catch (IOException | InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        t.setDaemon(true);
        audioThread = t;
        t.start();
    }

    public static void playMenuMusic()    { play("assets/menu.mp3"); }
    public static void playBattleMusic() { play("assets/grass.mp3"); }
    public static void playVictory()     { play("assets/victory.mp3"); }

    public static void playPageTurn() {
        Thread t = new Thread(() -> {
            try {
                new ProcessBuilder("afplay", "assets/pageturn.mp3").start().waitFor();
            } catch (IOException | InterruptedException ignored) {}
        });
        t.setDaemon(true);
        t.start();
    }

    public static void playChomp() {
        Thread t = new Thread(() -> {
            try {
                new ProcessBuilder("afplay", "assets/chomp.mp3").start().waitFor();
            } catch (IOException | InterruptedException ignored) {}
        });
        t.setDaemon(true);
        t.start();
    }

    public static void stop() {
        Thread t = audioThread;
        audioThread = null;
        if (t != null) t.interrupt();
        Process p = process;
        process = null;
        if (p != null) p.destroyForcibly();
    }
}
