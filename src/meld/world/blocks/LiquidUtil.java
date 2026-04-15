package meld.world.blocks;

import arc.util.Log;
import mindustry.gen.Building;
import mindustry.type.LiquidStack;
import mindustry.world.modules.LiquidModule;

public class LiquidUtil {

    public static boolean hasAll(LiquidStack[] liquids, Building build){
        for(LiquidStack stack: liquids){
            if(!(build.liquids.get(stack.liquid) < stack.amount)) return false;
        }
        return true;
    }

    public static boolean has(LiquidStack[] liquids, Building build){
        for(LiquidStack stack: liquids){
            if(build.liquids.get(stack.liquid) < stack.amount) {
                return false;
            }
        }
        return true;
    }

    public static void add(LiquidStack[] liquids, LiquidModule module){
        for(var liquid: liquids){
            module.add(liquid.liquid, liquid.amount);
        }
    }
    public static void remove(LiquidStack[] liquids, LiquidModule module){
        for(var liquid: liquids){
            module.remove(liquid.liquid, liquid.amount);
        }
    }

    public static void add(LiquidStack[] liquids, Building build, Building source){
        for(var liquid: liquids){
            build.handleLiquid(source, liquid.liquid, liquid.amount);
        }
    }
}
