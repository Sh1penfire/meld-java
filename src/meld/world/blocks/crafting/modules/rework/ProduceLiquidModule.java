package meld.world.blocks.crafting.modules.rework;

import arc.util.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import meld.world.blocks.crafting.modules.rework.base.*;
import mindustry.type.*;

public class ProduceLiquidModule extends ProduceModule{
    public LiquidStack[] liquids;

    public ProduceLiquidModule(int... inputPins){
        super(inputPins);
    }

    @Override
    public void update(ModularCrafterBuild build){
        //dump/check full
        boolean full = false;
        for(LiquidStack stack : liquids){
            build.dumpLiquid(stack.liquid);

            if(build.liquids.get(stack.liquid) >= build.block.liquidCapacity - 0.001f){
                full = true;
            }
        }
        if(full) return;

        //Sum efficiencies and timescale up
        float input = takeEfficiency(build);

        //continuously output based on efficiency
        for(LiquidStack stack : liquids){
            build.handleLiquid(build, stack.liquid,
                Math.min(
                    stack.amount * input * Time.delta,
                    build.block.liquidCapacity - build.liquids.get(stack.liquid)
                )
            );
        }

        build.visualEfficiency = Math.max(input, build.visualEfficiency);
    }

    @Override
    public void setup(ModularCrafter block){
        block.hasLiquids = true;
        block.outputsLiquid = true;
        for(LiquidStack stack : liquids){
            block.dumpedLiquids.add(stack.liquid);
        }
    }
}
