package meld.world.blocks.crafting.modules;

import meld.world.blocks.crafting.ModularCrafter.*;

public class StupidConsumeHighestModule extends CrafterModule{
    public int[] requestPins;
    /// Pin to provide efficiency on.
    public int providePin;

    public StupidConsumeHighestModule(int providePin){
        this.providePin = providePin;
    }

    public void update(ModularCrafterBuild build){
        float provide = build.getPin(providePin);

        //Get highest
        float max = 0f;
        int maxPin = 0;
        for(int i : requestPins){
            if(build.getPin(i) > max){
                max = build.getPin(i);
                maxPin = i;
            }
        }

        if(max > provide){
            //Just swap pin contents
            build.setPin(maxPin, provide);
            build.setPin(providePin, max);
        }

    }
}
