package meld.world.blocks.crafting.modules.rework;

import arc.util.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import mindustry.type.*;

public class StupidProduceLiquidModule extends ProduceModule{
    public LiquidStack[] liquids;

    public StupidProduceLiquidModule(int... inputPins){
        super(inputPins);
    }

    @Override
    public void update(ModularCrafterBuild build){
        //check full
        for(LiquidStack stack : liquids){
            if(build.liquids.get(stack.liquid) >= build.block.liquidCapacity - 0.001f){
                return;
            }
        }

        //Sum efficiencies and timescale up
        float input = takeEfficiency(build);

        //continuously output based on efficiency
        for(LiquidStack stack : liquids){
            build.handleLiquid(build, stack.liquid,
                Math.min(
                    stack.amount * input * build.timeScale() * Time.delta,
                    build.block.liquidCapacity - build.liquids.get(stack.liquid)
                )
            );
        }
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
