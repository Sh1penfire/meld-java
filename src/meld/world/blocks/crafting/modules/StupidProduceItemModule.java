package meld.world.blocks.crafting.modules;

import arc.util.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import mindustry.type.*;

import java.util.*;

import static java.lang.Math.min;

public class StupidProduceItemModule extends CrafterModule{
    public ItemStack[] items;
    /// Pin used to read production efficiency.
    public int efficiencyPin;
    /// Pin used to store crafting progress.
    public int progressPin;
    /// Pin used to store whether this module can output. Assigned every tick.
    public int workingPin;
    public float time = 60f;

    public StupidProduceItemModule(int workingPin, int efficiencyPin, int progressPin){
        this.efficiencyPin = efficiencyPin;
        this.workingPin = workingPin;
        this.progressPin = progressPin;
    }

    @Override
    public void update(ModularCrafterBuild build){
        if(!fits(build, items)){
            build.setPin(workingPin, 0f);
            return;
        }
        build.setPin(workingPin, 1f);

        float progress = build.getPin(progressPin);
        progress += build.getPin(efficiencyPin) * Time.delta;

        while(progress > time && fits(build, items)){
            for(ItemStack stack : items){
                build.items.add(stack.item, stack.amount);
            }
            progress -= time;
        }
        build.setPin(progressPin, progress);
    }

    public static boolean fits(ModularCrafterBuild build, ItemStack[] itemStacks){
        return Arrays.stream(itemStacks).allMatch(stack -> build.items.get(stack.item) + stack.amount <= build.block.itemCapacity);
    }

    @Override
    public void setup(ModularCrafter block){
        block.hasItems = true;
        for(ItemStack stack : items){
            block.dumpedItems.add(stack.item);
        }
    }
}
