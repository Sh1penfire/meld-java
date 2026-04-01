package meld.world.blocks.consumers;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import arc.util.Log;
import meld.world.blocks.crafting.MeldGenericCrafter;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.consumers.*;

public class ConsumeAspects extends ConsumeLiquidFilter implements MeldGenericCrafter.BarConsumer {

    public ObjectFloatMap<Liquid> efficiencyMap, densityMap;


    public ConsumeAspects(float amount, ObjectFloatMap<Liquid> efficiencyMap, ObjectFloatMap<Liquid> densityMap){
        super();
        this.efficiencyMap = efficiencyMap;
        this.densityMap = densityMap;
        this.amount = amount;
        filter = efficiencyMap::containsKey;
    }

    private static Liquid currentBest;

    @Override
    public void apply(Block block) {
        block.hasLiquids = true;
        Vars.content.liquids().each(l -> efficiencyMap.containsKey(l), (item) -> {
            block.liquidFilter[item.id] = true;
        });
    }

    @Override
    public boolean consumes(Liquid liquid) {
        return efficiencyMap.containsKey(liquid);
    }

    @Override
    public void update(Building build) {
        Liquid consumed = getConsumed(build);
        build.liquids.remove(consumed, this.amount * build.edelta()/densityMap.get(consumed, 1));
    }

    @Override
    public Liquid getConsumed(Building build){
        float highest = 0;
        currentBest = null;
        for(Liquid liquid: efficiencyMap.keys()){
            if(!Mathf.zero(build.liquids.get(liquid))) {
                float value = efficiencyMap.get(liquid, 0);

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
        float ed = build.edelta();
        if(Mathf.zero(ed)) return 0;
        else {

            float multi = efficiencyMap.get(liq, 0);

            return liq != null ? Math.min(build.liquids.get(liq) / (this.amount * ed * multi), multi) : 0;
        }
    }

    @Override
    public float efficiencyMultiplier(Building build) {
        Liquid liq = this.getConsumed(build);
        return liq == null ? 0.0F : efficiencyMap.get(liq, 0);
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
