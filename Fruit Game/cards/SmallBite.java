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
            player.setReducedDisgust((player.getDisgustTaken() / 2));
            player.setReducedFullness((player.getFullnessTaken() / 2));
            player.setUsingDefense(true);
            player.addToDisgust(player.getReducedDisgust());
            player.addToFullness(player.getReducedFullness());
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
