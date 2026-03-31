package meld.world.blocks.crafting.modules;

import arc.math.Mathf;
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
        float addedAmount = Math.min(liquid.amount * efficiency, Mathf.maxZero(build.block.liquidCapacity - build.liquids.get(liquid.liquid)));
        build.liquids.add(liquid.liquid, addedAmount);
    }
}
