package entities;

import cards.DefenseCard;
import cards.FruitCard;
import java.util.ArrayList;

public class Player {
    private String name = "";
    private int disgustMeter = 100;
    private int fullnessMeter = 100;
    private ArrayList<FruitCard> fruits = new ArrayList<>();
    private ArrayList<DefenseCard> defense = new ArrayList<>();
    private int energy = 100;
    private int currentChoice = 0;
    private boolean alive = true;
    private FruitCard currentFruit = null;
    private boolean usingDefense = false;
    private int reducedDisgust;
    private int reducedFullness;
    private Player target;
    private boolean enoughEnergy = true;
    private int disgustTaken;
    private int fullnessTaken;
    private boolean vulnerable = false;
    private int vulnerableRoundsLeft = 0;

    public String getName() { return name; }
    public int getDisgust() { return disgustMeter; }
    public int getFullness() { return fullnessMeter; }
    public int getEnergy() { return energy; }
    public boolean isAlive() { return alive; }
    public FruitCard getCurrentFruit() { return currentFruit; }
    public int getCurrentChoice() { return currentChoice; }
    public boolean isEnoughEnergy() { return enoughEnergy; }
    public ArrayList<FruitCard> getFruits() { return fruits; }
    public ArrayList<DefenseCard> getDefense() { return defense; }
    public int getReducedDisgust() { return reducedDisgust; }
    public int getReducedFullness() { return reducedFullness; }
    public boolean isUsingDefense() { return usingDefense; }
    public int getDisgustTaken() { return disgustTaken; }
    public int getFullnessTaken() { return fullnessTaken; }
    public Player getTarget() { return target; }
    public boolean getVulnerable(){return vulnerable;}

    public void setName(String name) { this.name = name; }
    public void setTarget(Player player) { this.target = player; }
    public void setCurrentFruit(FruitCard fruit) { this.currentFruit = fruit; }
    public void setCurrentChoice(int choice) { this.currentChoice = choice; }
    public void setEnoughEnergy(boolean enough) { this.enoughEnergy = enough; }
    public void setUsingDefense(boolean using) { this.usingDefense = using; }
    public void setReducedDisgust(int amount) { this.reducedDisgust = amount; }
    public void setReducedFullness(int amount) { this.reducedFullness = amount; }
    public void setDisgustTaken(int amount) { this.disgustTaken = amount; }
    public void setFullnessTaken(int amount) { this.fullnessTaken = amount; }
    public void setVulnerable(boolean choice) { this.vulnerable = choice; if (!choice) vulnerableRoundsLeft = 0; }
    public void setVulnerableRounds(int rounds) { vulnerableRoundsLeft = rounds; vulnerable = rounds > 0; }
    

    public void spendEnergy(int amount) { energy -= amount; }
    public void gainEnergy(int amount) { energy = Math.min(energy + amount, 100); }

    public void subtractDisgust(int damage) { disgustMeter -= damage; }
    public void subtractFullness(int damage) { fullnessMeter -= damage; }
    public void addToDisgust(int added) { disgustMeter += added; }
    public void addToFullness(int added) { fullnessMeter += added; }

    public void executeDefense(int index) {
        for (DefenseCard card : defense) {
            card.executeMove(index);
        }
    }

    public void printFruitCards() {
        for (int i = 0; i < currentFruit.getMoveSetLength(); i++) {
            System.out.printf("%d. %s ", i + 1, currentFruit.getMoveSet()[i]);
        }
    }

    public void printDefenseCards() {
        for (int i = 0; i < defense.size(); i++) {
            System.out.printf("%d. %s ", currentFruit.getMoveSetLength() + i + 1, defense.get(i).getName());
        }
    }

    public void addFruit(FruitCard fruit) {
        fruits.add(fruit);
        fruit.setUser(this);
        fruit.setTarget(this.target);
        
    }

    public void addDefense(DefenseCard created) {
        defense.add(created);
        created.setTarget(this);
    }

    public final void endRound() {
        if (fullnessMeter <= 0 || disgustMeter <= 0) {
            alive = false;
        }
        if (!currentFruit.isStacking()) {
            currentFruit.resetFruit();
        }
        energy = Math.min(energy + 5, 100);
        currentFruit.resetMoveEnergy();
        target.setReducedDisgust(0);
        target.setReducedFullness(0);
        disgustTaken = 0;
        fullnessTaken = 0;
        currentFruit.tickSkip();
        if (vulnerableRoundsLeft > 0) {
            vulnerableRoundsLeft--;
            if (vulnerableRoundsLeft == 0) vulnerable = false;
        }
    }

    public void resetPlayer() {
        disgustMeter = 100;
        fullnessMeter = 100;
        energy = 100;
        alive = true;
    }
}
