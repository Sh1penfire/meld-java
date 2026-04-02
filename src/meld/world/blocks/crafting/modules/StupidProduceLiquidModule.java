package meld.world.blocks.crafting.modules;

import arc.util.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import mindustry.type.*;

//TODO remake this to be the same as the consumption modules
public class StupidProduceLiquidModule extends CrafterModule{
    public LiquidStack[] liquids;
    /// Pin used to read production efficiency.
    public int efficiencyPin;
    /// Pin used to store whether this module can output. Assigned every tick.
    public int workingPin;

    public boolean dumpExtraLiquid = false;

    public StupidProduceLiquidModule(int workingPin, int efficiencyPin){
        this.efficiencyPin = efficiencyPin;
        this.workingPin = workingPin;
    }

    //Entirely GenericCrafter copy-paste, probably Sucks Ass.
    @Override
    public void update(ModularCrafterBuild build){
        //check full
        for(LiquidStack stack : liquids){
            if(build.liquids.get(stack.liquid) < build.block.liquidCapacity - 0.001f){
                //if there's still space left, it's not full for all liquids
                build.setPin(workingPin, 1f);
            }else if(!dumpExtraLiquid){
                build.setPin(workingPin, 0f);
                return;
            }
        }

        //continuously output based on efficiency
        for(LiquidStack stack : liquids){
            build.handleLiquid(build, stack.liquid,
                Math.min(
                    stack.amount * build.getPin(efficiencyPin) * Time.delta,
                    build.block.liquidCapacity - build.liquids.get(stack.liquid)
                )
            );
        }
    }

    @Override
    public void setup(ModularCrafter block){
        block.hasLiquids = true;
        for(LiquidStack stack : liquids){
            block.dumpedLiquids.add(stack.liquid);
        }
    }
}
