package meld.world.blocks.crafting.recipe;

import mindustry.gen.Building;
import mindustry.world.Block;

public abstract class Recipe<T extends Block, A extends Building> {

        public abstract boolean valid(T block, A building);
        public abstract void apply(T block, A building);
        public void update(T block, A building) {}
    //accepted liquids ey?
        public void init(T block) {}

}
