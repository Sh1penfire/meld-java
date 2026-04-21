package meld.world.blocks.producer;

import mindustry.gen.Building;
import mindustry.type.ItemStack;

public class ProduceItem extends Produce{
    public ItemStack output;

    public ProduceItem(ItemStack output){
        this.output = output;
    }

    @Override
    public float efficiency(Building build) {
        return build.getMaximumAccepted(output.item) - build.items.get(output.item) >= output.amount ? 1 : 0;
    }

    @Override
    public void trigger(Building build) {
        build.items.add(output.item, output.amount);
    }
}
