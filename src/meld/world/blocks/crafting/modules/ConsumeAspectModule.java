package meld.world.blocks.crafting.modules;

import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import meld.world.blocks.crafting.ModularCrafter;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;

public class ConsumeAspectModule extends ModularCrafter.CrafterModule{
    public ObjectFloatMap<Liquid> efficiencyMap;
    public ObjectFloatMap<Liquid> densityMap;

    float consumeRate;
    public int efficiencyPin;
    public int outputEfficiencyPin;

    public ConsumeAspectModule(float consumeRate, ObjectFloatMap<Liquid> efficiencyMap, ObjectFloatMap<Liquid> densityMap, int efficiencyPin, int outputEfficiencyPin) {
        this.consumeRate = consumeRate;
        this.efficiencyMap = efficiencyMap;
        this.densityMap = densityMap;
        this.efficiencyPin = efficiencyPin;
        this.outputEfficiencyPin = outputEfficiencyPin;
    }

    @Override
    public void update(ModularCrafter.ModularCrafterBuild build) {
        float efficiency = build.getPin(efficiencyPin);
        if(efficiency == 0) return;

        float highest = 0;
        Liquid liquid = null;
        for(Liquid aspect: efficiencyMap.keys()){
            if(!Mathf.zero(build.liquids.get(aspect))){
                float current = efficiencyMap.get(aspect, 1);
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
        float consumeAmount = consumeRate * efficiency/densityMap.get(liquid, 1);
        float maxEfficiency = Math.min(amount/consumeAmount, 1);

        consumeAmount *= maxEfficiency;

        build.liquids.remove(liquid, consumeAmount);

        build.setPin(outputEfficiencyPin, efficiency * maxEfficiency * efficiencyMap.get(liquid, 1));
    }
}
