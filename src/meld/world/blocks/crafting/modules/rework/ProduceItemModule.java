package meld.world.blocks.crafting.modules.rework;

import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import meld.world.blocks.crafting.modules.rework.base.*;
import mindustry.type.*;
import java.util.*;

public class ProduceItemModule extends ProduceDiscreteModule{
    public ItemStack[] items;

    public ProduceItemModule(int... inputPins){
        super(inputPins);
    }

    @Override
    public void update(ModularCrafterBuild build){
        super.update(build);

        for(ItemStack stack : items){
            build.dump(stack.item);
        }
    }

    @Override
    public boolean canOutput(ModularCrafterBuild build){
        return Arrays.stream(items).allMatch(stack -> build.items.get(stack.item) + stack.amount <= build.block.itemCapacity);
    }

    @Override
    public void output(ModularCrafterBuild build){
        for(ItemStack stack : items){
            build.items.add(stack.item, stack.amount);
        }
    }

    @Override
    public void setup(ModularCrafter block){
        block.hasItems = true;
        for(ItemStack stack : items){
            block.dumpedItems.add(stack.item);
        }
    }
}
