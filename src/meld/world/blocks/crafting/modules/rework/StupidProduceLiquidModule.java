package meld.world.blocks.crafting.modules.rework;

import arc.util.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import mindustry.type.*;

public class StupidProduceLiquidModule extends CrafterModule{
    public LiquidStack[] liquids;
    /// Pin to consume efficiency from.
    public int[] inputPins;

    public StupidProduceLiquidModule(int... inputPins){
        this.inputPins = inputPins;
    }

    @Override
    public void update(ModularCrafterBuild build){
        float input = 1f;
        for(int i : inputPins) input *= build.getPin(i);

        //After summing, subtract the final efficiency
        for(int i : inputPins) build.setPin(i, build.getPin(i) - input);

        //check full
        for(LiquidStack stack : liquids){
            if(build.liquids.get(stack.liquid) >= build.block.liquidCapacity - 0.001f){
                return;
            }
        }

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
