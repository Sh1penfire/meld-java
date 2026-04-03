package meld.world.blocks.crafting.modules.rework;

import arc.util.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import mindustry.type.*;

import java.util.*;

public abstract class ProduceDiscreteModule extends CrafterModule{
    /// Pins to consume efficiency from.
    public int[] inputPins;
    /// Pin used to store crafting progress.
    public int progressPin;
    public float time = 60f;

    public ProduceDiscreteModule(int... inputPins){
        this.inputPins = inputPins;
    }

    @Override
    public void update(ModularCrafterBuild build){
        if(!canOutput(build)) return;

        float input = 1f;
        for(int i : inputPins) input *= build.getPin(i);

        //After summing, subtract the final efficiency
        for(int i : inputPins) build.setPin(i, build.getPin(i) - input);

        float progress = build.getPin(progressPin);
        progress += input * build.timeScale() * Time.delta;

        while(progress > time && canOutput(build)){
            output(build);
            progress -= time;
        }
        build.setPin(progressPin, progress);

    }

    public abstract boolean canOutput(ModularCrafterBuild build);

    public abstract void output(ModularCrafterBuild build);
}
