package meld.world.blocks.crafting;

import mindustry.gen.Building;
import mindustry.world.Block;

public abstract class Recipe<T extends Block, A extends Building> {

    public abstract boolean valid(T block, A building);
    public abstract void apply(T block, A building);
}
