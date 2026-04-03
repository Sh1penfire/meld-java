package meld.world.blocks.crafting.modules.rework;

import meld.world.blocks.crafting.ModularCrafter.*;

public abstract class ProduceModule extends CrafterModule{
    /// Pins to consume efficiency from.
    public int[] inputPins;

    public ProduceModule(int... inputPins){
        this.inputPins = inputPins;
    }

    public float takeEfficiency(ModularCrafterBuild build){
        //Sum efficiencies and timescale up
        float input = build.timeScale();
        for(int i : inputPins) input *= build.getPin(i);

        //After summing, subtract the final efficiency
        for(int i : inputPins) build.setPin(i, build.getPin(i) - input);

        return input;
    }
}
