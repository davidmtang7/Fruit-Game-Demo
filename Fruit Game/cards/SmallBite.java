package cards;

import entities.Player;
import gui.AudioManager;

public class SmallBite extends DefenseCard {
    private static final int ENERGY_COST = 16;

    public SmallBite() {
        name = "Small Bite";
    }

    @Override
    public int getEnergyCost() { return ENERGY_COST; }

    @Override
    public void use() {
        System.out.println(player.getCurrentFruit().formattedString("Small Bite"));
        moveEnergy = ENERGY_COST;
        checkEnoughEnergy();
        if (!player.isEnoughEnergy()) return;
        AudioManager.playChomp();
        Player target = player.getTarget();
        if (target.getCurrentFruit().isStacking()) {
            System.out.printf("%s didn't attack, no damage saved%n", target.getName());
        } else if (target.getCurrentChoice() % 2 != 0) {
            System.out.printf("%s will now take half of%n the damage%n", player.getName());
            // Takes the damage the player will take and divides by 2, stores it in player for the print. Adds it back to fullness/disgust bar to mimic reduction
            int reducedDisgust = player.getDisgustTaken() / 2;
            int reducedFullness = player.getFullnessTaken() / 2;
            player.setReducedDisgust((reducedDisgust));
            player.setReducedFullness((reducedFullness));
            // Ensures that card is only being used this round
            player.setUsingDefense(true);
            player.addToDisgust(reducedDisgust);
            player.addToFullness(reducedFullness);
        }
        useEnergy();
    }

    @Override
    public void executeMove(int index) {
        if (index == player.getCurrentFruit().getMoveSetLength() + 1) {
            use();
        }
    }
}
