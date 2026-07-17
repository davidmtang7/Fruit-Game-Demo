package entities;

import java.util.Random;

public class Enemy extends Player {
    private final Random rand = new Random();

    public Enemy() {}

    public void chooseRandomMoveApple() {
      
        if(getCurrentFruit().getSkipped()){
            getCurrentFruit().handleSkip();
            return;
        }
        int chance = rand.nextInt(100);
        // Higher odds to sit in dirt at higher energy
        if(getEnergy() > 50){
            chance = rand.nextInt(15,100);
        }
        // Guaranteed sit in dirt if sit in dirt odds are good
        if(getCurrentFruit().getRoundCounter() > 0 && getCurrentFruit().getRoundCounter() < 2){
            chance = rand.nextInt(100);
        }
        // Higher odds to bruise if sit in dirt odds are lower
        else if (getCurrentFruit().getRoundCounter() >= 2 && getCurrentFruit().getRoundCounter() < 4) {
            chance = rand.nextInt(45);
        // Nearly guaranteed chance to bruise if sit in dirt odds are too low
        } else if (getCurrentFruit().getRoundCounter() >= 4) {
            chance = rand.nextInt(31);
        }
        // If target is low hp or user has low energy than high odds to bruise
        if (getDisgust() <= 30 || getFullness() <= 30 || getEnergy() < 45) {
            chance = rand.nextInt(33);
        }

        int choice;
        if (chance >= 30) {
            choice = 2;
        } else {
            choice = 1;
        }
        if(getEnergy() < 5){
            choice = 4;
        }
        // If low energy randomly decide between bite or do nothing
        else if(getEnergy() < 15){
            choice = rand.nextInt(3, 5);
        }
        // If user has barely enoguh energy to bruise then guaranteed bruise
        else if(getEnergy() < 25){
            choice = 1;
        }

        

        if (getCurrentFruit().getEnergyCost(choice) > getEnergy()) {
            // If can't use bite, do nothing
            choice = getEnergy() >= 5 ? 3 : 4;
        }
        setCurrentChoice(choice);
        getCurrentFruit().executeMove(choice);
    }
}
