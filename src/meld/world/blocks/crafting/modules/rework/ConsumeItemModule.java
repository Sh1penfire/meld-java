package meld.world.blocks.crafting.modules.rework;

import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import meld.world.blocks.crafting.modules.rework.base.*;
import mindustry.type.*;

public class ConsumeItemModule extends ConsumeDiscreteModule{
    public ItemStack[] items;

    public ConsumeItemModule(int... outputPins){
        super(outputPins);
    }

    @Override
    public boolean canConsume(ModularCrafterBuild build){
        return build.items.has(items);
    }

    @Override
    public void consume(ModularCrafterBuild build){
        build.items.remove(items);
    }

    @Override
    public void setup(ModularCrafter block){
        block.hasItems = true;
        for(ItemStack stack : items){
            block.acceptedItems.add(stack.item);
        }
    }
}
