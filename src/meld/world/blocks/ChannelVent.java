package meld.world.blocks;

import mindustry.Vars;
import mindustry.entities.Puddles;
import mindustry.type.Liquid;
import mindustry.world.blocks.Attributes;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.meta.Attribute;

public class ChannelVent extends LiquidRouter {

    public float minPressure = 0;
    public float ventRate = 1;

    public ChannelVent(String name) {
        super(name);
    }

    public class ChannelVentBuild extends LiquidRouterBuild{
        @Override
        public void updateTile() {
            super.updateTile();

            Liquid liquid = liquids.current();

            if(liquid.gas || liquid.boilPoint <= Attribute.heat.env()){
                float ventEfficiency = (liquids.get(liquid)/liquidCapacity - minPressure)/(1 - minPressure);

                if(ventEfficiency > 0){

                    float leakAmount = ventEfficiency * ventRate;
                    Puddles.deposit(tile, tile, liquid, leakAmount, true, true);
                    liquids.remove(liquid, leakAmount);
                }
            }
        }
    }
}
