package meld.world.blocks.fluid;

import arc.graphics.g2d.Draw;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.entities.Puddles;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.meta.Attribute;

public class ChannelVent extends LiquidRouter {

    public float minPressure = 0;
    public float ventRate = 1;
    public float ventDelay = 10;

    int ventTimer = timers++;

    public Interp ventScaling = Interp.pow2In;

    public ChannelVent(String name) {
        super(name);
    }

    public class ChannelVentBuild extends LiquidRouterBuild{


        @Override
        public void updateTile() {
            super.updateTile();

            Liquid liquid = liquids.current();

            if(timer.get(ventTimer, ventDelay) && liquid.gas || liquid.boilPoint <= Attribute.heat.env()){
                float ventEfficiency = ventScaling.apply((liquids.get(liquid)/liquidCapacity - minPressure)/(1 - minPressure));

                if(ventEfficiency > 0){

                    float leakAmount = Math.min(ventEfficiency * ventRate * ventDelay, liquids.get(liquid));

                    if(leakAmount > 1E-4){
                        Puddles.deposit(tile, tile, liquid, leakAmount, true, true);
                        liquids.remove(liquid, leakAmount);
                    }
                }
            }
        }
    }
}
