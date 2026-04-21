package meld.world.blocks.crafting.recipe;

import arc.struct.Seq;
import meld.world.blocks.crafting.RecipeCrafter;
import meld.world.blocks.producer.Produce;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;

public class TimedRecipe extends Recipe<Block, Building> {

    public Seq<Consume> consumers = new Seq<>();
    public Seq<Produce> producers = new Seq<>();

    public float craftTime = 60;

    public TimedRecipe() {
    }
    public TimedRecipe(float craftTime){
        this.craftTime = craftTime;
    }

    public TimedRecipe addConsumers(Consume... list){
        consumers.addAll(list);
        return this;
    }
    public TimedRecipe addProducers(Produce... list){
        producers.addAll(list);
        return this;
    }

    @Override
    public void update(Block block, Building building) {

        building.efficiency = efficiency(block, building);

        consumers.each(c -> c.update(building));
        producers.each(p -> p.update(building));

        building.efficiency = building.efficiency * boostEfficiency(block, building);
    }

    public float efficiency(Block block, Building building){
        float minEfficiency = 1;

        for(Consume consumer: consumers){
            minEfficiency = Math.min(consumer.efficiency(building), minEfficiency);
        }
        for(Produce producer: producers){
            minEfficiency = Math.min(producer.efficiency(building), minEfficiency);
        }

        return minEfficiency;
    }

    public float boostEfficiency(Block block, Building building){
        float efficiency = 1;

        for(Consume consumer: consumers){
            efficiency *= consumer.efficiencyMultiplier(building);
        }

        return efficiency;
    }

    @Override
    public void apply(Block block, Building building) {
        consumers.each(c -> c.trigger(building));
        producers.each(p -> p.trigger(building));
    }

    @Override
    public boolean valid(Block block, Building build) {
        return efficiency(block, build) > 0;
    }
}
