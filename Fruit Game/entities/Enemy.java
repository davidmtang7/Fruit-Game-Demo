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
        if(getEnergy() > 50){
            chance = rand.nextInt(15,100);
        }
        if(getCurrentFruit().getRoundCounter() > 0 && getCurrentFruit().getRoundCounter() < 2){
            chance = rand.nextInt(100);
        }
        else if (getCurrentFruit().getRoundCounter() >= 2 && getCurrentFruit().getRoundCounter() < 4) {
            chance = rand.nextInt(45);
        } else if (getCurrentFruit().getRoundCounter() >= 4) {
            chance = rand.nextInt(31);
        }

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
        else if(getEnergy() < 15){
            choice = rand.nextInt(3, 5);
        }
        else if(getEnergy() < 25){
            choice = 1;
        }

        

        if (getCurrentFruit().getEnergyCost(choice) > getEnergy()) {
            choice = getEnergy() >= 5 ? 3 : 4;
        }
        setCurrentChoice(choice);
        getCurrentFruit().executeMove(choice);
    }
}
