package meld.world.blocks.crafting.modules.rework;

import meld.world.blocks.crafting.ModularCrafter.*;

public abstract class ConsumeModule extends CrafterModule{
    /// Pins to provide efficiency on.
    public int[] outputPins;
    /// Makes the module ignore other pins that are full until no more can be output.
    public boolean dumpExtra = false;

    public ConsumeModule(int... outputPins){
        this.outputPins = outputPins;
    }

    public float getCurrent(ModularCrafterBuild build){
        if(!dumpExtra){
            //Find the least consumed output, it signals inactivity
            float max = Float.NEGATIVE_INFINITY;
            for(int i : outputPins){
                max = Math.max(max, build.getPin(i));
            }
            return max;
        }else{
            //If we don't care about a few full inputs, pick the minimum instead
            float min = Float.POSITIVE_INFINITY;
            for(int i : outputPins){
                min = Math.min(min, build.getPin(i));
            }
            return min;
        }

    }
}
