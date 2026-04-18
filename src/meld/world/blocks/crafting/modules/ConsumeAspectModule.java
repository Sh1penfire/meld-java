package meld.world.blocks.crafting.modules;

import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import meld.fluid.AspectGroup;
import meld.world.blocks.crafting.ModularCrafter;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;

public class ConsumeAspectModule extends ModularCrafter.CrafterModule{
    public AspectGroup group;

    public float consumeRate;
    public int efficiencyPin;
    public int outputEfficiencyPin;

    public ConsumeAspectModule(float consumeRate, AspectGroup group, int efficiencyPin, int outputEfficiencyPin) {
        this.consumeRate = consumeRate;
        this.group = group;
        this.efficiencyPin = efficiencyPin;
        this.outputEfficiencyPin = outputEfficiencyPin;
    }

    @Override
    public void update(ModularCrafter.ModularCrafterBuild build) {
        float efficiency = build.getPin(efficiencyPin);
        if(efficiency == 0) return;

        float highest = 0;
        Liquid liquid = null;

        for(Liquid aspect: group.stats.keys()){
            if(!Mathf.zero(build.liquids.get(aspect))){
                float current = group.getEfficiency(aspect);
                if(current > highest){
                    liquid = aspect;
                    highest = current;
                }
            }
        }

        if(liquid == null){
            build.setPin(outputEfficiencyPin, 0);
            return;
        }

        float amount = build.liquids.get(liquid);
        float consumeAmount = consumeRate * efficiency/group.getDensity(liquid);
        float maxEfficiency = Math.min(amount/consumeAmount, 1);

        consumeAmount *= maxEfficiency;

        build.liquids.remove(liquid, consumeAmount);

        build.setPin(outputEfficiencyPin, efficiency * maxEfficiency * group.getEfficiency(liquid));
    }
}
