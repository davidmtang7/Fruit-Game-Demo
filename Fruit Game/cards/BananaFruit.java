package cards;

import gui.AudioManager;
import java.util.Random;

public class BananaFruit extends FruitCard {
    private static final int SLIP_ENERGY = 15;
    private static final int BITE_ENERGY = 23;
    private static final int RIPEN_ENERGY = 17; 
    private final Random rand = new Random();

    public BananaFruit() {
        disgust = 15;
        fullness = 10;
        name = "Banana";
        moveSet = new String[]{"Slip", "Ripen", "Bite", "Do Nothing"}; 
    }
    @Override
    public void resetFruit(){
        disgust = 15;
        fullness = 10;
    }
    // Chance to skip opponents turn
    public void slip(){
        moveEnergy = SLIP_ENERGY;
        checkEnoughEnergy();
        if (!user.isEnoughEnergy()){return;}
        int chance = rand.nextInt(100);
        if(chance >= 30){
            enemy.getCurrentFruit().setSkipRounds(2);
            System.out.printf("%s slipped on the banana!%n", user.getTarget().getName());
            
        }
        else {
            System.out.printf("%s saw the banana and walked around it.%n", user.getTarget().getName());
        }

        useEnergy();
    }
    // Weakens opponent
    public void ripen(){
        moveEnergy = RIPEN_ENERGY;
        checkEnoughEnergy();
        if(!user.isEnoughEnergy()){ return; }

        System.out.printf("%s ripened the banana!%n", user.getName());
        enemy.setVulnerableRounds(3);

        useEnergy();
       
    }

    
    @Override
    // Regular bite method but with banana ripen accounted for 
    public void bite() {
        if(skipped) return;
        
        checkEnoughEnergy();
        if (!user.isEnoughEnergy()) return;

        if (moveEnergy == BITE_ENERGY) {
            useEnergy();
        }
        AudioManager.playChomp();
        System.out.printf("%s gave the %s to %s.%n", user.getName(), name, enemy.getName());
        enemy.setUsingDefense(false);
        stacking = false;


        int mult = enemy.getVulnerable() ? 2 : 1;

        enemy.setDisgustTaken(disgust * mult);
        enemy.setFullnessTaken(fullness * mult);    
        enemy.subtractDisgust(disgust * mult);
        enemy.subtractFullness(fullness * mult);

        if(mult == 2){
            enemy.setVulnerable(false);
        }
        
        
    }

    @Override
    public int[] getMoveDamage(int index) {
        if (index == 3) {
            int mult = (enemy != null && enemy.getVulnerable()) ? 2 : 1;
            return new int[]{disgust * mult, fullness * mult};
        }
        return null;
    }

    @Override
    public int getEnergyCost(int index) {
        switch (index) {
            case 1: return SLIP_ENERGY;
            case 2: return RIPEN_ENERGY;
            case 3: return BITE_ENERGY;
            default: return 0;
        }
    }

    @Override
    public void executeMove(int index){
        switch(index){
             case 1:
                System.out.println(formattedString("Slip"));
                slip();
                break;
            case 2:
                System.out.println(formattedString("Ripen"));
                ripen();
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
