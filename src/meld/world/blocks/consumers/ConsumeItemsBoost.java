package meld.world.blocks.consumers;

import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.world.consumers.ConsumeItems;

public class ConsumeItemsBoost extends ConsumeItems {

    public float boostAmount;

    public ConsumeItemsBoost(ItemStack[] items, float boostAmount){
        super(items);
        this.boostAmount = boostAmount;
    };


    @Override
    public float efficiencyMultiplier(Building build) {
        return boostAmount;
    }
}
