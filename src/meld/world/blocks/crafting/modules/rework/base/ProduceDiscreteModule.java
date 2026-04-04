package meld.world.blocks.crafting.modules.rework.base;

import arc.util.*;
import meld.world.blocks.crafting.ModularCrafter.*;

public abstract class ProduceDiscreteModule extends ProduceModule{

    /// Pin used to store crafting progress.
    public int progressPin;
    public float time = 60f;

    public ProduceDiscreteModule(int... inputPins){
        super(inputPins);
    }

    @Override
    public void update(ModularCrafterBuild build){
        if(!canOutput(build)) return;

        //Sum efficiencies and timescale up
        float input = takeEfficiency(build);

        float progress = build.getPin(progressPin);
        progress += input * Time.delta;

        while(progress > time && canOutput(build)){
            output(build);
            progress -= time;
        }
        build.setPin(progressPin, progress);

        build.visualEfficiency = Math.max(input, build.visualEfficiency);
    }

    public abstract boolean canOutput(ModularCrafterBuild build);

    public abstract void output(ModularCrafterBuild build);
}
