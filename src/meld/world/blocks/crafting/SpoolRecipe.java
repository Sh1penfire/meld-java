package meld.world.blocks.crafting;

import meld.world.blocks.Gauze;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;

public class SpoolRecipe extends Recipe<Gauze, Gauze.GauzeBuild> {

    public SpoolRecipe(ItemStack ammo, float energy){
        this.ammo = ammo;
        this.energy = energy;
    }

    public ItemStack ammo;
    public float energy;

    @Override
    public boolean valid(Gauze block, Gauze.GauzeBuild build) {
        return build.items.has(ammo.item, ammo.amount) && build.energy + energy <= block.spoolStorage;
    }

    @Override
    public void apply(Gauze block, Gauze.GauzeBuild build) {
        build.removeStack(ammo.item, ammo.amount);
        build.energy += energy;
    }
}
