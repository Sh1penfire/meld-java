package meld.world.blocks.crafting.modules;

import meld.world.blocks.crafting.ModularCrafter;
import mindustry.type.LiquidStack;

public class ProduceLiquidModule extends ModularCrafter.CrafterModule {
    public LiquidStack liquid;
    public int efficiencyPin;

    public ProduceLiquidModule(LiquidStack liquid, int efficiencyPin){
        this.liquid = liquid;
        this.efficiencyPin = efficiencyPin;
    }

    @Override
    public void update(ModularCrafter.ModularCrafterBuild build) {
        float efficiency = build.getPin(efficiencyPin);
        build.liquids.add(liquid.liquid, liquid.amount * efficiency);
    }
}
