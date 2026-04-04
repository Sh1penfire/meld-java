package meld.world.blocks.crafting.modules.rework;

import arc.util.*;
import meld.world.blocks.crafting.ModularCrafter.*;

/// Can be used to make blocks consume, regardless of whether they're doing something.
/// To do this, you can place it after a producer module, and make it consume from its input pin.
public class EfficiencyVoidModule extends CrafterModule{
    /// Pins to consume efficiency of.
    public int[] inputPins;
    /// The amount of efficiency consumed.
    public float amount = Float.POSITIVE_INFINITY;

    public EfficiencyVoidModule(int... inputPins){
        this.inputPins = inputPins;
    }

    @Override
    public void update(ModularCrafterBuild build){
        for(int i : inputPins) build.setPin(i, Math.max(0f, build.getPin(i) - amount * build.timeScale() * Time.delta));
    }
}
