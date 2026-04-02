package meld.world.blocks.crafting.modules;

import arc.util.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import mindustry.type.*;

public class StupidConsumeLiquidModule extends CrafterModule{
    public LiquidStack[] liquids;
    public float baseEfficiency = 0;
    public float efficiencyIncrease = 1;
    /// Pin to provide efficiency on.
    public int providePin;

    public StupidConsumeLiquidModule(int providePin){
        this.providePin = providePin;
    }

    @Override
    public void update(ModularCrafterBuild build){
        float provide = build.getPin(providePin);
        //Get the amount of efficiency that was eaten
        float consumed = (efficiencyIncrease - provide - baseEfficiency) / (efficiencyIncrease - baseEfficiency);

        if(provide < baseEfficiency) build.setPin(providePin, baseEfficiency);

        //if efficiency has been used
        if(consumed > 0f){
            float min = 10000f;
            for(LiquidStack stack : liquids){
                min = Math.min(build.liquids.get(stack.liquid) / (stack.amount * consumed * Time.delta), min);
            }

            if(min > 0.00001f){
                for(LiquidStack stack : liquids){
                    build.liquids.remove(stack.liquid, stack.amount * min);
                }
                build.setPin(providePin, baseEfficiency + efficiencyIncrease * min);
            }
        }
    }

    @Override
    public void setup(ModularCrafter block){
        block.hasLiquids = true;
        for(LiquidStack stack : liquids){
            block.acceptedLiquids.add(stack.liquid);
        }
    }
}
