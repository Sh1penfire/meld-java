package meld.world.blocks.crafting.modules.rework;

import arc.math.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import meld.world.blocks.crafting.modules.rework.base.*;

public class ConsumeHeatModule extends ConsumeModule{
    public float baseEfficiency = 0f;
    /// Base heat requirement for 100% efficiency.
    public float heatRequirement = 10f;
    /// After heat meets this requirement, excess heat will be scaled by this number.
    public float overheatScale = 1f;
    /// Maximum possible efficiency after overheat.
    public float maxEfficiency = 4f;

    public ConsumeHeatModule(int... outputPins){
        super(outputPins);
    }

    @Override
    public void update(ModularCrafterBuild build){
        build.heat = build.calculateHeat(build.sideHeat);

        float over = Math.max(build.heat - heatRequirement, 0f);
        float scaled = Math.min(Mathf.clamp(build.heat / heatRequirement) + over / heatRequirement * overheatScale, maxEfficiency);

        for(int i : outputPins) build.setPin(i, Math.max(baseEfficiency + scaled, build.getPin(i)));
    }
}
