package meld.world.blocks.power;

import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.consumers.Consume;

public class ConsumeThermal extends ThermalGenerator {
    public ConsumeThermal(String name) {
        super(name);
    }

    public class ConsumeThermalBuild extends ThermalGeneratorBuild{

        float consBoost = 1;

        @Override
        public void updateTile() {
            super.updateTile();
            consBoost = 1;
            for (Consume consumer : consumers) {
                consBoost *= consumer.efficiencyMultiplier(this);
            }
        }

        @Override
        public float getPowerProduction() {
            return enabled ? powerProduction * productionEfficiency * efficiency * consBoost : 0f;
        }
    }
}
