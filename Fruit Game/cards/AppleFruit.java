package cards;

import java.util.Random;

public class AppleFruit extends FruitCard {
    private static final int BRUISE_ENERGY = 17;
    private static final int SIT_IN_DIRT_ENERGY = 25;
    int randomGiven = 0;

    private final Random rand = new Random();
    private final int[] DIRT_DISGUST = {
        rand.nextInt(14, 17), rand.nextInt(17, 30), rand.nextInt(18, 25),
        rand.nextInt(30, 40), 42, 53
    };

    public AppleFruit() {
        fullness = 12;
        disgust = 5;
        name = "Apple";
        moveSet = new String[]{"Bruise", "Sit In Dirt", "Bite", "Do Nothing"};
    }

    public void bruise() {
        moveEnergy = BRUISE_ENERGY;
        checkEnoughEnergy();
        if (!user.isEnoughEnergy()) return;

        randomGiven = rand.nextInt(5, 12);
        disgust += randomGiven;
        System.out.println(user.getName() + " gave the Apple " + randomGiven + " disgust.");
        bite();
        useEnergy();
    }

    public void sitInDirt() {
        moveEnergy = SIT_IN_DIRT_ENERGY;
        checkEnoughEnergy();
        if (!user.isEnoughEnergy()) return;

        if (roundCounter <= 2) {
            stacking = true;
            if (rand.nextInt(100) >= 67) {
                roundCounter = 100;
            }
        } else if (roundCounter == 3) {
            if (rand.nextInt(100) >= 45) {
                roundCounter = 100;
            }
        } else if (roundCounter == 4 || roundCounter == 5) {
            if (rand.nextInt(100) >= 15) {
                roundCounter = 100;
            }
        }

        if (roundCounter < DIRT_DISGUST.length) {
            disgust += DIRT_DISGUST[roundCounter];
            System.out.printf("%s stacked %d disgust%n", user.getName(), disgust);
            roundCounter++;
        } else {
            System.out.println("The apple got eaten by worms!");
            resetFruit();
        }
        useEnergy();
    }

    @Override
    public void resetFruit() {
        fullness = 10;
        disgust = 5;
        roundCounter = 0;
    }

    @Override
    public int[] getMoveDamage(int index) {
        if (index == 3) return new int[]{disgust, fullness};
        return null;
    }

    @Override
    public int getEnergyCost(int index) {
        switch (index) {
            case 1: return BRUISE_ENERGY;
            case 2: return SIT_IN_DIRT_ENERGY;
            case 3: return 5;
            default: return 0;
        }
    }

    public void executeMove(int index) {
        if (skipped) {
            System.out.printf("%s is still on the ground!%n", user.getName());
            return;
        }
        switch (index) {
            case 1:
                System.out.println(formattedString("Bruise"));
                bruise();
                break;
            case 2:
                System.out.println(formattedString("Sit In Dirt"));
                sitInDirt();
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
