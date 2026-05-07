package meld.world.blocks.producer;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;

//Continuously produces liquid
public class ProduceLiquid extends Produce{

    public LiquidStack output;

    public ProduceLiquid(Liquid liquid, float amount){
        output = new LiquidStack(liquid, amount);
    }

    public ProduceLiquid(LiquidStack output){
        this.output = output;
    }

    @Override
    public float efficiency(Building build) {
        return Mathf.clamp(build.block.liquidCapacity - build.liquids.get(output.liquid) / output.amount);
    }


    @Override
    public void update(Building build) {
        super.update(build);
        build.handleLiquid(build, output.liquid, output.amount * Time.delta);
    }
}
