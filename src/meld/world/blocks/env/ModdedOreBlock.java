package meld.world.blocks.env;

import arc.graphics.Color;
import mindustry.type.Item;
import mindustry.world.blocks.environment.OreBlock;

public class ModdedOreBlock extends OreBlock {

    public boolean setDefaults = true;

    public ModdedOreBlock(String name, Item ore) {
        super(name, ore);
        setDefaults = false;
    }

    @Override
    public void setup(Item ore) {
        if(!setDefaults){
            Color mapCol = new Color(mapColor);
            super.setup(ore);
            mapColor = mapCol;

            return;
        }
        super.setup(ore);
    }
}
