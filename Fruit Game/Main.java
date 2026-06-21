// Main.java
import gui.AudioManager;
import gui.SceneManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
      SwingUtilities.invokeLater(Main::initGame);
   
    }


    private static void initGame() {
        JFrame frame = new JFrame("My Game");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AudioManager.stop();
                System.exit(0);
            }
        });
        frame.setSize(1200, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        SceneManager.init(frame);
        SceneManager.getInstance().showMenu();

        frame.setVisible(true);
        
    }
}