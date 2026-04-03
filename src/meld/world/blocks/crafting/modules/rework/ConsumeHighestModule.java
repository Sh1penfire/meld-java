package meld.world.blocks.crafting.modules.rework;

import meld.world.blocks.crafting.ModularCrafter.*;

/// Will only consume from the pin with the highest efficiency.
public class ConsumeHighestModule extends ConsumeModule{
    public int[] inputPins;

    public ConsumeHighestModule(int... outputPins){
        super(outputPins);
    }

    public void update(ModularCrafterBuild build){
        //Get the highest input pin
        float input = 0f;
        int inputPin = 0;
        for(int i : inputPins){
            if(build.getPin(i) > input){
                input = build.getPin(i);
                inputPin = i;
            }
        }

        //Find the least consumed output, it signals inactivity
        float current = getCurrent(build);

        if(input > current){
            //Just swap pin contents
            build.setPin(inputPin, current);
            build.setPins(outputPins, input);
        }

    }
}
