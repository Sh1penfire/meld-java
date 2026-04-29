package meld.world.blocks.consumers;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.IntFloatMap;
import arc.struct.IntMap;
import arc.struct.ObjectFloatMap;
import arc.util.Log;
import arc.util.Time;
import meld.fluid.AspectGroup;
import meld.world.blocks.crafting.MeldGenericCrafter;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.*;

public class StupidConsumeAspects extends ConsumeLiquidFilter implements MeldGenericCrafter.BarConsumer {

    //grab it from the group
    public AspectGroup group;

    public float boostMultiplier = 1;


    public StupidConsumeAspects(float amount, AspectGroup group){
        super();
        this.amount = amount;
        this.group = group;

        filter = group.stats::containsKey;
    }
    public StupidConsumeAspects(float amount, AspectGroup group, float boostMultiplier){
        this(amount, group);
        this.boostMultiplier = boostMultiplier;
    }

    private static Liquid currentBest;

    @Override
    public void apply(Block block) {
        block.hasLiquids = true;
        Vars.content.liquids().each(filter, (item) -> {
            block.liquidFilter[item.id] = true;
        });
    }


    @Override
    public void update(Building build) {
        Liquid consumed = getConsumed(build);
        if(consumed == null) return;
        build.liquids.remove(consumed, this.amount * build.edelta()/group.getDensity(consumed));
    }

    @Override
    public Liquid getConsumed(Building build){
        float highest = 0;
        currentBest = null;
        for(Liquid liquid: group.stats.keys()){
            if(!Mathf.zero(build.liquids.get(liquid))) {
                float value = group.getEfficiency(liquid);

                if(value > highest){
                    highest = value;
                    currentBest = liquid;
                }
            }
        }
        return currentBest;
    }

    @Override
    public float efficiency(Building build) {
        Liquid liq = this.getConsumed(build);
        float ed = build.timeScale() * Time.delta;
        if(Mathf.zero(ed) || liq == null) return 0;
        else {
            return Math.min(build.liquids.get(liq) / (this.amount * ed), 1);
        }
    }

    @Override
    public float efficiencyMultiplier(Building build) {
        Liquid liq = this.getConsumed(build);
        return liq == null ? 1 : group.getEfficiency(liq) * boostMultiplier;
    }

    @Override
    public void setBars(MeldGenericCrafter crafter) {

        crafter.addBar("liquid", entity -> {
                    Liquid current = getConsumed(entity);

                    return new Bar(
                            () -> current == null || entity.liquids.get(current) <= 0.001f ? Core.bundle.get("bar.liquid") : current.localizedName,
                            () -> current == null ? Color.clear : current.barColor(),
                            () -> current == null ? 0f : entity.liquids.get(current) / crafter.liquidCapacity
                    );
                }
        );

    }
}
