package meld.world.blocks.crafting.modules;

import meld.world.blocks.crafting.ModularCrafter;
import mindustry.type.ItemStack;

//If item requirement is not met, set the output to 0. Otherwise, set the output to one.
public class ConsumeGate extends ModularCrafter.CrafterModule {

    public ConsumeGate(ItemStack[] items, int outputPin){
        this.items = items;
        this.outputPin = outputPin;
    }

    public ItemStack[] items;

    public int outputPin;

    @Override
    public void update(ModularCrafter.ModularCrafterBuild build) {
        build.setPin(outputPin,build.items.has(items) ? 1 : 0);
    }
}
