package meld.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.distribution.Router;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.Vars.content;

//A container which outputs only to distribution blocks, helping to filter outputs
public class Depot extends StorageBlock {
    public TextureRegion centerRegion;

    public float speed = 1f;

    public Depot(String name) {
        super(name);
        update = true;
        hasItems = true;
        configurable = true;
        saveConfig = true;
        itemCapacity = 0;
        noUpdateDisabled = true;
        clearOnDoubleTap = true;
        unloadable = false;

        config(Item.class, (DepotBuild tile, Item item) -> tile.sortItem = item);
        configClear((DepotBuild tile) -> tile.sortItem = null);
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    @Override
    public void load() {
        super.load();
        centerRegion = Core.atlas.find(name + "-center");
    }

    public class DepotBuild extends StorageBuild{
        public float time;
        public Item sortItem = null;
        public Item lastItem;
        public Tile lastInput;

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(block, table, content.items(), () -> sortItem, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public Item config(){
            return sortItem;
        }


        @Override
        public void updateTile(){
            if(lastItem == null && items.any()){
                //branching just for the sake of my sanity here
                if(sortItem == null || sortItem == items.first()) lastItem = items.first();
            }

            if(lastItem != null){
                time += 1f / speed * delta();
                Building target = getTileTarget(lastItem, lastInput, false);

                if(target != null && (time >= 1f || !(target.block instanceof Router || target.block.instantTransfer))){
                    getTileTarget(lastItem, lastInput, true);
                    target.handleItem(this, lastItem);
                    items.remove(lastItem, 1);
                    lastItem = null;
                }
            }
        }

        public Building getTileTarget(Item item, Tile from, boolean set){
            int counter = rotation;
            for(int i = 0; i < proximity.size; i++){
                Building other = proximity.get((i + counter) % proximity.size);
                if(set) rotation = ((byte)((rotation + 1) % proximity.size));
                if(other.tile == from && from.block() == Blocks.overflowGate) continue;
                if(other.acceptItem(this, item)){
                    return other;
                }
            }
            return null;
        }

        @Override
        public void draw(){
            super.draw();

            Draw.color(sortItem == null ? Color.clear : sortItem.color);
            Draw.rect(centerRegion, x, y);
            Draw.color();
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            drawItemSelection(sortItem);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(sortItem == null ? -1 : sortItem.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int id = read.s();
            sortItem = id == -1 ? null : content.item(id);
        }
    }
}
