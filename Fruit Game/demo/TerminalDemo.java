package demo;

import cards.AppleFruit;
import cards.BananaFruit;
import cards.SmallBite;
import cards.WatermelonFruit;
import entities.Enemy;
import entities.Player;
import java.util.Scanner;

public class TerminalDemo {

    public void initGame() {
        AppleFruit enemyApple = new AppleFruit();
        AppleFruit playerApple = new AppleFruit();
        BananaFruit playerBanana = new BananaFruit();
        WatermelonFruit playerWatermelon = new WatermelonFruit();
        SmallBite smallBite = new SmallBite();
        Enemy appleMan = new Enemy();
        Player player = new Player();
        player.setTarget(appleMan);
        appleMan.setTarget(player);
        appleMan.setName("Apple Man");
        appleMan.addFruit(enemyApple);
        player.addFruit(playerApple);
        player.addFruit(playerBanana);
        player.addFruit(playerWatermelon);
        appleMan.setCurrentFruit(enemyApple);

        try (Scanner scnr = new Scanner(System.in)) {
            System.out.print("Input name: ");
            String name = scnr.nextLine();
            player.setName(name);
            System.out.printf("Welcome to the demo %s!%n", name);
            System.out.println("Game Start.");
            System.out.println("Your opponent is: Apple Man");

            while (player.getCurrentFruit() == null) {
                System.out.println("Select Starter Fruit");
                for (int i = 0; i < player.getFruits().size(); i++) {
                    System.out.printf("%d. %s%n", i + 1, player.getFruits().get(i).getName());
                }
                int choice;
                try {
                    choice = scnr.nextInt() - 1;
                    scnr.nextLine();
                } catch (Exception e) {
                    System.out.println("Not a number, try again!");
                    scnr.nextLine();
                    continue;
                }
                try {
                    player.setCurrentFruit(player.getFruits().get(choice));
                    System.out.println("You've chosen " + player.getCurrentFruit().getName());
                    if (!(player.getCurrentFruit() instanceof WatermelonFruit)) {
                        player.addDefense(smallBite);
                    }
                } catch (Exception e) {
                    System.out.println("Invalid choice! Try again.");
                }
            }

            while (player.isAlive() && appleMan.isAlive()) {
                System.out.println("Select your move!");
                player.printFruitCards();
                player.printDefenseCards();
                System.out.println();

                int currentChoice = -1;
                boolean choosing = true;
                while (choosing) {
                    try {
                        currentChoice = scnr.nextInt();
                        scnr.nextLine();
                    } catch (Exception e) {
                        System.out.println("Not a number, try again!");
                        scnr.nextLine();
                        continue;
                    }
                    if (currentChoice > player.getCurrentFruit().getMoveSetLength() + player.getDefense().size() || currentChoice < 1) {
                        System.out.println("Not a choice in this move set!");
                    } else {
                        choosing = false;
                    }
                }

                player.getCurrentFruit().executeMove(currentChoice);
                if (!player.isEnoughEnergy()) continue;
                appleMan.chooseRandomMoveApple();
                if (currentChoice <= player.getCurrentFruit().getMoveSetLength()) {
                    player.getCurrentFruit().applyPassiveEffects();
                }
                player.executeDefense(currentChoice);

                System.out.printf("Apple Man: %d disgust %d fullness %d energy%n",
                    appleMan.getDisgust(), appleMan.getFullness(), appleMan.getEnergy());
                System.out.printf("%s: %d disgust %d fullness %d energy%n",
                    player.getName(), player.getDisgust(), player.getFullness(), player.getEnergy());

                player.endRound();
                appleMan.endRound();
            }

            if (player.isAlive()) {
                System.out.println("You win!");
            } else {
                System.out.println("Apple Man Wins.");
            }
        }
    }
}
