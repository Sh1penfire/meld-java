package meld.world.blocks.crafting.modules;

import arc.util.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import mindustry.type.*;

public class StupidConsumeItemModule extends CrafterModule{
    public ItemStack[] items;
    /// Pin to provide efficiency on.
    public int providePin;
    /// Pin to store consumption progress.
    public int progressPin;
    public float time = 60f;

    public float baseEfficiency = 0f;
    public float efficiencyIncrease = 1f;

    public StupidConsumeItemModule(int providePin, int progressPin){
        this.providePin = providePin;
        this.progressPin = progressPin;
    }

    @Override
    public void update(ModularCrafterBuild build){
        float provide = build.getPin(providePin);
        float consumed = (efficiencyIncrease - provide - baseEfficiency) / (efficiencyIncrease - baseEfficiency);
        float progress = build.getPin(progressPin);

        if(provide < baseEfficiency) build.setPin(providePin, baseEfficiency);

        //if efficiency has been used
        if(consumed > 0f && build.items.has(items)){
            build.setPin(providePin, baseEfficiency + efficiencyIncrease);
            progress += consumed * Time.delta;

            //consumption
            while(progress > time && build.items.has(items)){

                build.items.remove(items);
                progress -= time;
            }
            build.setPin(progressPin, progress);
        }
    }

    @Override
    public void setup(ModularCrafter block){
        block.hasItems = true;
        for(ItemStack stack : items){
            block.acceptedItems.add(stack.item);
        }
    }
}
