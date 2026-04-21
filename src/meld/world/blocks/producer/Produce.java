package meld.world.blocks.producer;

import arc.scene.ui.layout.Table;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.Stats;

/**
 * A copy of Consume but for production
 */
public class Produce {

    /** @return if true, this consumer will be ignored in the production list (no updates or valid() checks) */
    public boolean ignore(){
        return false;
    }

    public void build(Building build, Table table){

    }

    /** Called when a production is triggered manually. */
    public void trigger(Building build){

    }

    public void update(Building build){

    }

    /** @return [0, 1] efficiency multiplier based on input. Returns 0 if not valid in subclasses. Should return fraction it can partially output. */
    public float efficiency(Building build){
        return 1f;
    }

    public void display(Stats stats){

    }
}
