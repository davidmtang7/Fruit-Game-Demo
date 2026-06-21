package cards;

import entities.Player;
import gui.AudioManager;

public class FruitCard {
    protected int fullness;
    protected int disgust;
    protected String name = "";
    protected String[] moveSet;
    protected int roundCounter = 0;
    protected Player enemy;
    protected Player user;
    protected boolean stacking = false;
    protected int moveEnergy = 0;
    protected boolean skipped = false;
    private int skipRoundsLeft = 0;

    private static final int BITE_ENERGY = 5;

    public void setTarget(Player enemy) { this.enemy = enemy; }
    public void setUser(Player user) { this.user = user; }
    public String getName() { return name; }
    public Player getTarget() { return enemy; }
    public int getFullness() { return fullness; }
    public int getDisgust() { return disgust; }
    public boolean isStacking() { return stacking; }
    public boolean getSkipped() { return skipped; }
    public int getMoveSetLength() { return moveSet.length; }
    public String[] getMoveSet() { return moveSet; }
    public int getRoundCounter() { return roundCounter; }
    public void resetMoveEnergy() { moveEnergy = 0; }
    public int getEnergyCost(int index) { return 0; }
    public int[] getMoveDamage(int index) { return null; }
    public void setSkipped(boolean choice) { skipped = choice; }
    public void setSkipRounds(int rounds) { skipRoundsLeft = rounds; skipped = rounds > 0; }
    public void tickSkip() { if (skipRoundsLeft > 0) skipRoundsLeft--; skipped = skipRoundsLeft > 0; }
    public int getSkipRoundsLeft() { return skipRoundsLeft; }
    
    public void handleSkip() {
        if (skipRoundsLeft == 2) {
            System.out.printf("%s slipped on the banana and can't move!%n", user.getName());
        } else {
            System.out.printf("%s is still on the ground!%n", user.getName());
        }
    }

    public String formattedString(String move) {
        return String.format("%s picked %s", user.getName(), move);
    }

    public void bite() {
        if(skipped) return;
        if (moveEnergy != 17) {
            moveEnergy = BITE_ENERGY;
        }
        checkEnoughEnergy();
        if (!user.isEnoughEnergy()) return;

        if (moveEnergy == BITE_ENERGY) {
            useEnergy();
        }
        AudioManager.playChomp();
        enemy.subtractDisgust(disgust);
        enemy.subtractFullness(fullness);
        System.out.printf("%s gave the %s to %s.%n", user.getName(), name, enemy.getName());
        enemy.setUsingDefense(false);
        stacking = false;
        enemy.setDisgustTaken(disgust);
        enemy.setFullnessTaken(fullness);
    }

    public void doNothing() {
        moveEnergy = -100;
        checkEnoughEnergy();
        user.gainEnergy(10);
        System.out.printf("%s did nothing.%n", user.getName());
         
        if(skipped){
            System.out.printf("%s laughed that they slipped while doing nothing,%n %s gains 10 energy%n", user.getName(), user.getName());
            user.gainEnergy(10);
        }
    }

    public void resetFruit() {}
    public void executeMove(int index) {}
    public void executeDefenseMove(int choice) {}
    public void applyPassiveEffects() {}

    public void checkEnoughEnergy() {
        if (moveEnergy > user.getEnergy()) {
            System.out.println("You don't have enough energy");
            user.setEnoughEnergy(false);
        } else {
            user.setEnoughEnergy(true);
        }
    }

    public void useEnergy() {
        user.spendEnergy(moveEnergy);
    }
   
}
