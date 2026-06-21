package gui;

import entities.Player;
import entities.Enemy;
import cards.*;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.JFrame;

public class SceneManager {
    private static final Map<String, Supplier<FruitCard>>   FRUITS  = Map.of(
        "Apple",      AppleFruit::new,
        "Banana",     BananaFruit::new,
        "Watermelon", WatermelonFruit::new
    );
    private static final Map<String, Supplier<DefenseCard>> DEFENSES = Map.of(
        "Apple",      SmallBite::new,
        "Banana",     SmallBite::new,
        "Watermelon", SmallBite::new
    );

    private static SceneManager instance;
    private final JFrame frame;

    private SceneManager(JFrame frame) {
        this.frame = frame;
    }

    public static void init(JFrame frame) {
        instance = new SceneManager(frame);
    }

    public static SceneManager getInstance() {
        return instance;
    }

    public void showMenu() {
        AudioManager.playMenuMusic();
        frame.setContentPane(new MenuPanel());
        frame.revalidate();
        frame.repaint();
    }

    public void showMenuSilent() {
        AudioManager.stop();
        frame.setContentPane(new MenuPanel());
        frame.revalidate();
        frame.repaint();
    }

    public void showMenuKeepMusic() {
        frame.setContentPane(new MenuPanel());
        frame.revalidate();
        frame.repaint();
    }

    public void showTutorial() {
        frame.setContentPane(new TutorialPanel());
        frame.revalidate();
        frame.repaint();
    }

    public void showFruitInfo() {
        frame.setContentPane(new FruitInfoPanel());
        frame.revalidate();
        frame.repaint();
    }

    public void showBattle(String playerName, String fruitName) {
        AppleFruit enemyApple = new AppleFruit();
        FruitCard playerFruit = FRUITS.getOrDefault(fruitName, AppleFruit::new).get();
        DefenseCard defense   = DEFENSES.getOrDefault(fruitName, SmallBite::new).get();
        Enemy appleMan = new Enemy();
        appleMan.setName("Apple Man");
        appleMan.addFruit(enemyApple);
        Player player = new Player();
        player.setName(playerName.isEmpty() ? "Player" : playerName);
        player.addFruit(playerFruit);
        if (DEFENSES.containsKey(fruitName)) player.addDefense(defense);
        enemyApple.setTarget(player);
        playerFruit.setTarget(appleMan);
        player.setTarget(appleMan);
        appleMan.setTarget(player);
        appleMan.setCurrentFruit(enemyApple);
        player.setCurrentFruit(playerFruit);

        AudioManager.playBattleMusic();
        frame.setContentPane(new BattlePanel(player, appleMan));
        frame.revalidate();
        frame.repaint();
    }

    public void showGame() {
        GamePanel game = new GamePanel();
        frame.setContentPane(game);
        frame.revalidate();
        frame.repaint();
        game.startLoop();
    }
}
