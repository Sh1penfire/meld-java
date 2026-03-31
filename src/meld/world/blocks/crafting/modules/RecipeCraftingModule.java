package meld.world.blocks.crafting.modules;

import meld.world.blocks.crafting.ModularCrafter;
import meld.world.blocks.crafting.Recipe;

public class RecipeCraftingModule extends ModularCrafter.CraftingModule {
    public Recipe recipe;


    @Override
    public boolean canCraft(ModularCrafter.ModularCrafterBuild build) {
        return recipe.valid(build.block, build);
    }

    @Override
    public void craft(ModularCrafter.ModularCrafterBuild build) {
        recipe.apply(build.block, build);
    }
}
