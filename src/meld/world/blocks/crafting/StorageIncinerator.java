package meld.world.blocks.crafting;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Item;
import mindustry.world.blocks.production.ItemIncinerator;
import mindustry.world.meta.BlockStatus;

public class StorageIncinerator extends ItemIncinerator {
    public StorageIncinerator(String name) {
        super(name);
        hasItems = true;
        hasLiquids = true;
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    public class ItemIncineratorBuild extends Building {

        @Override
        public void updateTile(){
        }

        @Override
        public BlockStatus status(){
            return !enabled ? BlockStatus.logicDisable : efficiency > 0 ? BlockStatus.active : BlockStatus.noInput;
        }

        @Override
        public void draw(){
            super.draw();

            if(liquidRegion.found()){
                Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color);
            }
            if(topRegion.found()){
                Draw.rect(topRegion, x, y);
            }
        }

        @Override
        public void handleItem(Building source, Item item){
            if(efficiency > 0){
                if(Mathf.chance(effectChance)) effect.at(x, y);
            }
            else super.handleItem(source, item);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < itemCapacity || efficiency > 0;
        }

        @Override
        public int getMaximumAccepted(Item item) {
            return efficiency > 0 ? Integer.MAX_VALUE/2 : Mathf.clamp(itemCapacity - items.get(item), 0, itemCapacity);
        }
    }
}
