package meld.world.util;

import mindustry.gen.Building;
import mindustry.type.ItemStack;

public class ItemLogic {

    public static boolean capacity(ItemStack[] stacks, Building build){
        for(var stack: stacks){
            if(build.getMaximumAccepted(stack.item) - build.items.get(stack.item) < stack.amount) return false;
        }
        return true;
    }

    public static void addStacks(ItemStack[] stacks, Building source, Building dest){
        for(var stack: stacks){
            dest.handleStack(stack.item, stack.amount, source);
        }
    }
    public static void addStacks(ItemStack[] stacks, Building source){
        addStacks(stacks, source, source);
    }
}
