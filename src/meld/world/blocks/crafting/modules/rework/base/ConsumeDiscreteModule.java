package meld.world.blocks.crafting.modules.rework.base;

import arc.util.*;
import meld.world.blocks.crafting.ModularCrafter.*;

public abstract class ConsumeDiscreteModule extends ConsumeModule{
    /// Pin to store consumption progress.
    public int progressPin;
    public float time = 60f;

    public float baseEfficiency = 0f;
    public float efficiencyIncrease = 1f;

    public ConsumeDiscreteModule(int... outputPins){
        super(outputPins);
    }

    public abstract boolean canConsume(ModularCrafterBuild build);

    public abstract void consume(ModularCrafterBuild build);

    @Override
    public void update(ModularCrafterBuild build){
        float current = getCurrent(build);
        //Get the amount of efficiency that was eaten
        float consumed = (efficiencyIncrease - current - baseEfficiency) / (efficiencyIncrease - baseEfficiency);
        float output = Math.max(baseEfficiency, current);

        //if efficiency has been used
        if(consumed > 0f && canConsume(build)){
            float progress = build.getPin(progressPin);
            progress += consumed * Time.delta;
            output = baseEfficiency + efficiencyIncrease;

            //consumption
            while(progress > time && canConsume(build)){
                consume(build);
                progress -= time;
            }
            build.setPin(progressPin, progress);
        }

        build.setPins(outputPins, output);
    }
}
