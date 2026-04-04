package meld.world.blocks.crafting.modules.rework;

import arc.math.*;
import arc.util.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import meld.world.blocks.crafting.modules.rework.base.*;

public class ProduceHeatModule extends ProduceModule{
    public float heatOutput = 10f;
    public float warmupRate = 0.15f;

    public ProduceHeatModule(int... inputPins){
        super(inputPins);
    }

    @Override
    public void update(ModularCrafterBuild build){
        float input = takeEfficiency(build);

        build.heat = Mathf.approachDelta(build.heat, heatOutput * input, warmupRate * Time.delta);

        build.visualEfficiency = Math.max(input, build.visualEfficiency);
    }
}
