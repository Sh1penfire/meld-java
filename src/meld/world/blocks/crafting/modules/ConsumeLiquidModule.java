package meld.world.blocks.crafting.modules;

import meld.world.blocks.crafting.ModularCrafter;
import mindustry.type.LiquidStack;

public class ConsumeLiquidModule extends ModularCrafter.CrafterModule {

    public ConsumeLiquidModule(LiquidStack[] consumed, int efficiencyPin, int outputEfficiencyPin){
        this.consumed = consumed;
        this.efficiencyPin = efficiencyPin;
        this.outputEfficiencyPin = outputEfficiencyPin;
    }

    public int efficiencyPin;
    public int outputEfficiencyPin;
    public LiquidStack[] consumed;

    @Override
    public void update(ModularCrafter.ModularCrafterBuild build) {
        float efficiency = build.getPin(efficiencyPin);
        if(efficiency == 0) return;

        float maxEfficiency = 1;

        for(LiquidStack stack: consumed){
            float target = build.liquids.get(stack.liquid);
            float consumeAmount = stack.amount * efficiency;

            maxEfficiency = Math.min(maxEfficiency, target/consumeAmount);
        }

        efficiency *= maxEfficiency;

        for (LiquidStack stack: consumed){
            float removed = efficiency * stack.amount;
            build.liquids.remove(stack.liquid, removed);
        }

        build.setPin(outputEfficiencyPin, efficiency);
    }
}
