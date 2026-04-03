package meld.world.blocks.crafting.modules.rework;

import arc.util.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import mindustry.type.*;

public class StupidConsumeLiquidModule extends ConsumeModule{
    public LiquidStack[] liquids;

    public float baseEfficiency = 0;
    public float efficiencyIncrease = 1;

    public StupidConsumeLiquidModule(int... outputPins){
        super(outputPins);
    }

    @Override
    public void update(ModularCrafterBuild build){
        //Find the least consumed output, it signals inactivity
        float current = getCurrent(build);
        //Get the amount of efficiency that was eaten
        float consumed = (efficiencyIncrease - current - baseEfficiency) / (efficiencyIncrease - baseEfficiency);
        float output = Math.max(baseEfficiency, current);

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
                output = baseEfficiency + efficiencyIncrease * min;
            }
        }

        build.setPins(outputPins, output);
    }

    @Override
    public void setup(ModularCrafter block){
        block.hasLiquids = true;
        for(LiquidStack stack : liquids){
            block.acceptedLiquids.add(stack.liquid);
        }
    }
}
