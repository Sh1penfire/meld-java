package meld.world.blocks.crafting.modules;

import arc.util.Log;
import meld.world.blocks.crafting.ModularCrafter;
import mindustry.world.meta.Attribute;

public class AttributeModule extends ModularCrafter.CrafterModule {
    public int efficiencyPin;

    public Attribute attribute;
    public float baseEfficiency = 1;
    public float boostScale = 1;
    public float maxBoost = 1;
    public float minEfficiency = -1;

    @Override
    public void update(ModularCrafter.ModularCrafterBuild build) {
        float efficiency = baseEfficiency + Math.min(maxBoost, boostScale * build.block.sumAttribute(attribute, build.tileX(), build.tileY()) + attribute.env());

        if(minEfficiency != -1 && efficiency < minEfficiency) {
            build.setPin(efficiencyPin, 0);
            return;
        }

        build.data.put(efficiencyPin, efficiency);
    }
}