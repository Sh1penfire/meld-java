package meld.world.blocks.fluid;

import arc.struct.Seq;
import mindustry.gen.Building;

public class ValveController extends FlexibleSizeJunction{

    public int updateTimer = timers++;
    public float minPressure = 0.1f;

    public boolean invert = true;

    public ValveController(String name) {
        super(name);
    }

    float total;

    public class ValveControllerBuild extends FlexibleBuild{

        @Override
        public void updateTile() {
            super.updateTile();

            total = 0;
            proximity.each(b -> {
                if(b.liquids != null) total += b.liquids.currentAmount()/b.block.liquidCapacity;
            });

            boolean enable = total >= minPressure;

            if(invert) enable = !enable;

            for(Building b: proximity){
                if(b.block.configurable) b.configure(enable);
            }
        }
    }
}
