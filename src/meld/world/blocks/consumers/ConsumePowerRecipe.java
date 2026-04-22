package meld.world.blocks.consumers;

import arc.func.Floatf;
import meld.world.blocks.crafting.RecipeCrafter;
import mindustry.gen.Building;
import mindustry.world.consumers.ConsumePowerDynamic;

public class ConsumePowerRecipe extends ConsumePowerDynamic {
    public ConsumePowerRecipe() {

        super(b -> {
            if(b instanceof RecipeCrafter.RecipeCrafterBuild r){
                return r.consPower != null ? r.consPower.requestedPower(b) : 0;
            }
            return 0;
        });
    }
}
