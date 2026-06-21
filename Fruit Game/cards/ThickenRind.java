package cards;

public class ThickenRind extends DefenseCard {
    private static final int ENERGY_COST = 10;
    private int rindResist = 15;
    private int thickenCount = 0;
    private boolean activated = false;

    public ThickenRind() { name = "Thicken Rind"; }

    @Override
    public int getEnergyCost() { return ENERGY_COST; }

    // Runs every round passively — applies accumulated resistance to this round's incoming damage
    public void applyResistance() {
        if (!activated) return;
        int dmgD = player.getDisgustTaken();
        int dmgF = player.getFullnessTaken();
        if (dmgD == 0 && dmgF == 0) return;
        int reducedDisgust  = (dmgD * thickenCount) / 4;
        int reducedFullness = (dmgF * thickenCount) / 4;
        player.setReducedDisgust(reducedDisgust);
        player.setReducedFullness(reducedFullness);
        player.setUsingDefense(reducedDisgust > 0 || reducedFullness > 0);
        player.addToDisgust(reducedDisgust);
        player.addToFullness(reducedFullness);
        rindResist--;
        if(rindResist > 0){
        System.out.printf("%d rind usages left%n", rindResist);
        }
        if (rindResist <= 0) {
            System.out.printf("The watermelon exploded! %s is destroyed!%n", player.getName());
            player.subtractDisgust(200);
            player.subtractFullness(200);
        }
    }

    // Runs only when player picks Thicken Rind — adds a stack, checks explosion
    @Override
    public void use() {
        moveEnergy = ENERGY_COST;
        checkEnoughEnergy();
        if (!player.isEnoughEnergy()) return;

        activated = true;
        thickenCount++;
        rindResist = rindResist - 2;

        if (rindResist <= 0) {
            System.out.printf("The watermelon exploded! %s is destroyed!%n", player.getName());
            player.subtractDisgust(200);
            player.subtractFullness(200);
            useEnergy();
            return;
        }

        System.out.printf("%s thickened the rind! Level %d, %d uses left before explosion%n",
                player.getName(), thickenCount, rindResist);
        useEnergy();
    }

    @Override
    public void executeMove(int index) {
        if (index == player.getCurrentFruit().getMoveSetLength() + 1) {
            use();
        }
    }
}
