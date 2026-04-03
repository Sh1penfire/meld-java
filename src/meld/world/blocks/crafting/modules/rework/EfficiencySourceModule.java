package meld.world.blocks.crafting.modules.rework;

import meld.world.blocks.crafting.ModularCrafter.*;

/// When you have multiple modules draw from the same pin, the first module in
/// the update order to craft something will block the rest from working.
/// This module is made for when you need this behavior, but without a shared input.
public class EfficiencySourceModule extends CrafterModule{
    /// Pins to provide efficiency on.
    public int[] outputPins;
    public float baseEfficiency = 1f;

    public EfficiencySourceModule(int... outputPins){
        this.outputPins = outputPins;
    }

    @Override
    public void update(ModularCrafterBuild build){
        for(int o : outputPins) build.setPin(o, baseEfficiency);
    }
}
