package meld.world.blocks.crafting.modules;

import meld.world.blocks.crafting.ModularCrafter.*;

//TODO remake this to be the same as the consumption modules
public class StupidCraftingModule extends CrafterModule{
    public int[] requestPins;
    public int outputPin;

    public StupidCraftingModule(int outputPin){
        this.outputPin = outputPin;
    }

    public void update(ModularCrafterBuild build){
        float efficiency = build.timeScale();
        //Sum up the efficiency
        for(int i : requestPins) efficiency *= build.getPin(i);

        if(efficiency > 0){
            //Subtract the usable efficiency
            for(int i : requestPins) build.setPin(i, build.getPin(i) - efficiency);
        }

        build.setPin(outputPin, efficiency);
    }
}

