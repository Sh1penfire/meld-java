package meld.world.blocks.crafting.modules;

import meld.world.blocks.crafting.ModularCrafter;

public class MultiplierModule extends ModularCrafter.CrafterModule {

    public MultiplierModule(){

    }

    public MultiplierModule(int outputPin, int... inputPins){
        this.inputPins = inputPins;
        this.outputPin = outputPin;
    }

    public int[] inputPins;
    public int outputPin;

    @Override
    public void update(ModularCrafter.ModularCrafterBuild build) {
        float value = 1;
        for(int i: inputPins){
            value *= build.getPin(i);
        }
        build.setPin(outputPin, value);
    }
}
