package meld.world.blocks.crafting.modules;

import arc.struct.Seq;
import meld.world.blocks.LiquidUtil;
import meld.world.blocks.crafting.ModularCrafter;
import meld.world.blocks.crafting.ItemRecipe;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;

public class GateModule extends ModularCrafter.CrafterModule {


    int outputPin;

    public Seq<CrafterCondition> conditions = new Seq<>();

    public GateModule(){

    }

    public GateModule(int outputPin, CrafterCondition... conditions){
        this.outputPin = outputPin;
        this.conditions.set(conditions);
    }

    @Override
    public void update(ModularCrafter.ModularCrafterBuild build) {
        build.setPin(outputPin, validate(build) ? 1 : 0);
    }

    public boolean validate(ModularCrafter.ModularCrafterBuild build){
        for (var condition: conditions){
            if(!condition.valid(build)) return false;
        }
        return true;
    }

    public abstract static class CrafterCondition{
        public abstract boolean valid(ModularCrafter.ModularCrafterBuild build);
    }

    public static class ConsumeCondition extends CrafterCondition{
        public ConsumeCondition(ItemStack[] items){
            this.items = items;
        }

        public ConsumeCondition(LiquidStack[] liquids){
            this.liquids = liquids;
        }

        public ConsumeCondition(ItemStack[] items, LiquidStack[] liquids){
            this.items = items;
            this.liquids = liquids;
        }

        public ItemStack[] items;
        public LiquidStack[] liquids;

        public boolean valid(ModularCrafter.ModularCrafterBuild build){
            if(liquids != null && !LiquidUtil.has(liquids, build)) return false;

            //Set the output to zero if the build doesn't have the items, otherwise set it to whatever the out is
            return build.items.has(items);
        }
    }
    public static class OutputCondition extends CrafterCondition{
        public OutputCondition(ItemStack[] items){
            this.items = items;
        }

        public OutputCondition(LiquidStack[] liquids){
            this.liquids = liquids;
        }

        public OutputCondition(ItemStack[] items, LiquidStack[] liquids){
            this.items = items;
            this.liquids = liquids;
        }

        public ItemStack[] items;
        public LiquidStack[] liquids;

        public boolean valid(ModularCrafter.ModularCrafterBuild build){
            if(items != null){
                for(var output : items){
                    if(build.items.get(output.item) + output.amount > build.block.itemCapacity){
                        return false;
                    }
                }
            }
            if(liquids != null){
                for(var output: liquids){
                    if(build.liquids.get(output.liquid) + output.amount >= build.block.liquidCapacity){
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static class RecipeCondition extends CrafterCondition{
        public ItemRecipe recipe;

        public RecipeCondition(ItemRecipe recipe){
            this.recipe = recipe;
        }

        @Override
        public boolean valid(ModularCrafter.ModularCrafterBuild build) {
            return recipe.valid(build.block, build);
        }
    }

}
