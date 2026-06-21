package cards;

import entities.Player;

public class DefenseCard {
    protected Player player;
    protected String name = "";
    protected int moveEnergy = 0;

    public void setTarget(Player player) { this.player = player; }
    public String getName() { return name; }

    public void executeMove(int index) {}
    public void use() {}
    public void resetDisgustAndFullness() {}
    public int getEnergyCost() { return 0; }

    public void checkEnoughEnergy() {
        if (moveEnergy > player.getEnergy()) {
            System.out.printf("%s doesn't have enough energy, %s gets hit anyways.%n",
                player.getName(), player.getName());
            player.setEnoughEnergy(false);
        } else {
            player.setEnoughEnergy(true);
        }
    }

    public void useEnergy() {
        player.spendEnergy(moveEnergy);
    }
}
