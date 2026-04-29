package meld.world.blocks.units;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import arc.util.pooling.Pool.*;
import meld.graphics.TileDrawers;
import mindustry.Vars;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.input.DesktopInput;
import mindustry.input.InputHandler;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

//Attempts to load balance a specific item on a unit to a specific amount, managing the items from a nearby storage
public class UnitLogisticsPad extends Block {

    public TextureRegion centerRegion;

    @Override
    public void load() {
        super.load();
        centerRegion = Core.atlas.find(name + "-center", Core.atlas.find("unloader-center"));
    }

    public float speed = 1f;
    public float range = -1;

    /** Cached result of content.items() */
    static Item[] allItems;

    public UnitLogisticsPad(String name){
        super(name);
        update = true;
        solid = true;
        health = 70;
        hasItems = true;
        configurable = true;
        saveConfig = true;
        itemCapacity = 0;
        noUpdateDisabled = true;
        clearOnDoubleTap = true;
        unloadable = false;

        config(Item.class, (UnloadingPadBuild tile, Item item) -> tile.sortItem = item);
        config(Float.class, (UnloadingPadBuild tile, Float value) -> tile.targetPercent = value);
        configClear((UnloadingPadBuild tile) -> tile.sortItem = null);
    }

    @Override
    public void init(){
        super.init();

        allItems = content.items().toArray(Item.class);
        if(range == -1) range = size * tilesize;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.speed, 60f / speed, StatUnit.itemsSecond);
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list){
        drawPlanConfigCenter(plan, plan.config, "unloader-center");
    }

    @Override
    public void drawOverlay(float x, float y, int rotation) {
        x += offset;
        y += offset;

        int tx = (int)x/tilesize, ty = (int)y/tilesize;

        int tileRange = (int)(range/tilesize/2f);
        for(int dx = -tileRange; dx <= tileRange; dx++){
            for(int dy = -tileRange; dy <= tileRange; dy++){
                if(!fogControl.isDiscovered(player.team(), tx + dx, ty + dy)){
                    TileDrawers.drawFog((tx + dx) * tilesize, (ty + dy) * tilesize, TileDrawers.tileRad);
                }
            }
        }
        Drawf.dashSquare(Pal.accent, x, y, range);
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("items");
    }

    public class UnloadingPadBuild extends Building{

        public float targetPercent;

        public Item sortItem;
        public float unloadTimer;
        public Seq<Unit> candidates;
        public Seq<Building> loadTargets = Seq.with();

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            rebuildTargets();
        }

        public void rebuildTargets(){
            loadTargets.clear();
            proximity.each(b -> {
                if(b.interactable(team) && b.items != null){
                    loadTargets.add(b);
                }
            });

            loadTargets.sort(b -> b.block instanceof CoreBlock ? 0 : 1);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            unload();
        }

        public void unload(){
            unloadTimer += Time.delta;
            if (unloadTimer >= 60) {
                candidates = Groups.unit.intersect(x - range/2, y - range/2, range, range);
                if(candidates.size == 0) return;
                unloadTimer %= 60;

                //Loop through each unit over the pad
                candidates.each(unit -> {
                    //Pls don't load units with items already, thanks
                    if(unit.hasItem() && sortItem != null && unit.item() != sortItem) return;

                    Item item = unit.stack.amount == 0 ? sortItem : unit.item();
                    if(item == null) return;

                    //How many items to remove from the unit. If it's negative, how many items to load into the unit instead.
                    int transferAmount = unit.stack.amount - (int) (unit.type.itemCapacity * targetPercent);

                    if(transferAmount == 0) return;

                    //If we have items to unload, loop through nearby buildings and try to push into them
                    if(transferAmount > 0){
                        //How many items should be left in the unit
                        float target = unit.stack.amount - transferAmount;

                        loadTargets.each(build -> {

                            //Get the minimum between the items we can push, and the amount to deposit
                            int transfering = Math.min(build.getMaximumAccepted(item) - build.items.get(item), (int) Mathf.maxZero(unit.stack.amount - target));

                            //No more items left to transport :D
                            if(transfering == 0) return;

                            Call.transferItemTo(unit, item, transfering, unit.x, unit.y, build);
                        });

                        return;
                    }

                    //Treat transfer amount as the amount of items we want the unit to gain from its initial amount
                    transferAmount *= -1;

                    //The max amount of items the unit will hold
                    float target = unit.stack.amount + transferAmount;

                    loadTargets.each(build -> {

                        //Get the minimum between the items we can push, and the amount to deposit
                        int transfering = Math.min(build.items.get(item), (int) Mathf.maxZero(target - unit.stack.amount));

                        //No more items left to transport :D
                        if(transfering == 0) return;
                        InputHandler.takeItems(build, item,transfering, unit);
                    });

                });
            }
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
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(block, table, content.items(), () -> sortItem, this::configure, selectionRows, selectionColumns);
            table.row();
            table.slider(0, 1, 0.1f, targetPercent, Vars.net.active(), this::configure).size(240, 40f);
        }

        @Override
        public Item config(){
            return sortItem;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(sortItem == null ? -1 : sortItem.id);
            write.f(targetPercent);
            write.f(unloadTimer);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            id = read.i();
            sortItem = id == -1 ? null : content.item(id);
            targetPercent = read.f();
            unloadTimer = read.f();
        }
    }
}