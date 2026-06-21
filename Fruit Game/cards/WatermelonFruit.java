package cards;
import entities.Player;
import gui.AudioManager;
import java.util.Random;

public class WatermelonFruit extends FruitCard {
    private final Random rand = new Random();
    private static final int HOLLOW_OUT_ENERGY = 80;
    private static final int BITE_ENERGY = 25;
    private final ThickenRind thickenRind = new ThickenRind();

    public WatermelonFruit() {
        disgust = 5;
        fullness = 10;
        name = "Watermelon";
        moveSet = new String[]{"Hollow Out", "Thicken Rind", "Bite", "Do Nothing"};
    }

    @Override
    public void setUser(Player user) {
        super.setUser(user);
        thickenRind.setTarget(user);
    }

    @Override
    public void resetFruit() {
        disgust = 5;
        fullness = 10;
    }

    public void hollowOut() {
        moveEnergy = HOLLOW_OUT_ENERGY;
        checkEnoughEnergy();
        if (!user.isEnoughEnergy()) return;
        System.out.printf("%s scraped the watermelon%n clean for %s!%n", user.getName(), enemy.getName());
        fullness += 55;
        disgust += 40;
        bite();
        System.out.printf("%s had to help%n %s finish!%n", user.getName(), enemy.getName());
        int mult = user.getFullness() > 40 || user.getDisgust() > 40 ? 2 : 1;
        if(user.getFullness() > 80 && user.getDisgust() > 80){ mult = 3; }
        user.subtractFullness(20 * mult);
        user.subtractDisgust(10 * mult);
        useEnergy();
    }

    @Override
    public void bite() {
        if (skipped) return;
        checkEnoughEnergy();
        if (!user.isEnoughEnergy()) return;
        AudioManager.playChomp();
        if (moveEnergy != HOLLOW_OUT_ENERGY) { moveEnergy = BITE_ENERGY; }
        if (moveEnergy == HOLLOW_OUT_ENERGY) {
            enemy.subtractDisgust(disgust);
            enemy.subtractFullness(fullness);
            enemy.setDisgustTaken(disgust);
            enemy.setFullnessTaken(fullness);
        } else if (moveEnergy == BITE_ENERGY) {
            useEnergy();
            int chance = rand.nextInt(100);
            boolean bigSlice = chance >= 65;
            int added = bigSlice ? rand.nextInt(8, 11) : rand.nextInt(4);
            System.out.printf("%s got a %s slice!%n", enemy.getName(), bigSlice ? "big" : "small");
            enemy.subtractDisgust(disgust + added);
            enemy.subtractFullness(fullness + added);
            enemy.setDisgustTaken(disgust + added);
            enemy.setFullnessTaken(fullness + added);
        }
        enemy.setUsingDefense(false);
        stacking = false;
    }

    @Override
    public void executeDefenseMove(int choice) {
        if (choice == 2) thickenRind.use();
    }

    @Override
    public void applyPassiveEffects() {
        thickenRind.applyResistance();
    }

    @Override
    public int[] getMoveDamage(int index) {
        if (index == 3) return new int[]{disgust, fullness};
        return null;
    }

    @Override
    public int getEnergyCost(int index) {
        switch (index) {
            case 1: return HOLLOW_OUT_ENERGY;
            case 2: return thickenRind.getEnergyCost();
            case 3: return BITE_ENERGY;
            default: return 0;
        }
    }

    @Override
    public void executeMove(int index) {
        switch (index) {
            case 1:
                System.out.println(formattedString("Hollow Out"));
                hollowOut();
                break;
            case 2:
                System.out.println(formattedString("Thicken Rind"));
                executeDefenseMove(2);
                break;
            case 3:
                System.out.println(formattedString("Bite"));
                bite();
                break;
            case 4:
                System.out.println(formattedString("Do Nothing"));
                doNothing();
                break;
        }
    }
}
