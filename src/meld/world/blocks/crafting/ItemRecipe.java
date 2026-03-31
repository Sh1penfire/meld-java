package meld.world.blocks.crafting;

import meld.world.blocks.LiquidUtil;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;

public class ItemRecipe extends Recipe<Block, Building>{
    public ItemStack[] inputItems;
    public ItemStack[] outputItems;

    public LiquidStack[] inputLiquids;
    public LiquidStack[] outputLiquids;

    public ItemRecipe(){

    }

    public ItemRecipe(ItemStack[] inputItems, ItemStack[] outputItems){
        this.inputItems = inputItems;
        this.outputItems = outputItems;
    };

    @Override
    public boolean valid(Block block, Building build){
        if(outputItems != null){
            for(var output : outputItems){
                if(build.items.get(output.item) + output.amount > build.block.itemCapacity){
                    return false;
                }
            }
        }
        if(outputLiquids != null){
            for(var output: outputLiquids){
                if(build.liquids.get(output.liquid) + output.amount >= build.block.liquidCapacity){
                    return false;
                }
            }
        }
        if(inputLiquids != null && !LiquidUtil.has(inputLiquids, build)) return false;

        //Set the output to zero if the build doesn't have the items, otherwise set it to whatever the out is
        return build.items.has(inputItems);
    }

    @Override
    public void apply(Block block, Building building) {
        if(inputItems != null) building.items.remove(inputItems);

        if(outputItems != null) {
            for (ItemStack item : outputItems) {
                building.items.add(item.item, item.amount);
            }
        }

        if(inputLiquids != null) LiquidUtil.remove(inputLiquids, building.liquids);
        if(outputLiquids != null) LiquidUtil.add(outputLiquids, building.liquids);
    }

    //Builder methods
    public ItemRecipe input(ItemStack[] items){
        inputItems = items;
        return this;
    }
    public ItemRecipe output(ItemStack[] items){
        outputItems = items;
        return this;
    }
    public ItemRecipe input(LiquidStack[] liquids){
        inputLiquids = liquids;
        return this;
    }
    public ItemRecipe output(LiquidStack[] liquids){
        outputLiquids = liquids;
        return this;
    }
}
