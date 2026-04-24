package meld.world.blocks.production;

import arc.math.Mathf;
import arc.struct.IntMap;
import arc.struct.IntSeq;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import meld.world.blocks.io.BlockIO;
import meld.world.util.ItemLogic;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.weapons.BuildWeapon;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.consumers.Consume;

public class GrindingQuary extends Block {

    public static ObjectMap<Block, GrinderEntry> grinderMap = new ObjectMap<>();

    public static ObjectFloatMap<Item> drillMultipliers = new ObjectFloatMap<>();

    public int targetTimer = timers++;

    public float itemDuration = 60;

    public GrindingQuary(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        itemCapacity = 50;
    }

    public static class GrinderEntry{

        public GrinderEntry(float time, Block tile){
            this(time, tile.asFloor());
        }

        public GrinderEntry(float time, Floor floor){
            this.time = time;
            this.floor = floor;
        }

        public GrinderEntry(float time, Floor floor, ItemStack[] output){
            this.time = time;
            this.floor = floor;
            this.output = output;
        }

        public float time;
        public Floor floor;
        public ItemStack[] output;
    }

    //Returned in items/seccond
    public float drillSpeedFloor(Tile tile){
        if(tile == null || tile.floor().itemDrop == null) return 0;
        return 0.25f;
    }
    public float drillSpeedOverlay(Tile tile){
        if(tile == null || tile.overlay().itemDrop == null) return 0;
        return 0.25f;
    }

    public class GrindingQuaryBuild extends Building{
        public float time = 0;
        public Tile target = null;
        public GrinderEntry entry;
        public float boostAmount = 1;

        //Timer used to determine when the current batch of items expires
        public float progress = 0;

        public ObjectFloatMap<Item> drillTimers = new ObjectFloatMap<>(), itemRates = new ObjectFloatMap<>();

        @Override
        public boolean shouldConsume() {
            return itemRates.size > 0 || target != null;
        }

        @Override
        public void update(){
            super.update();
            updateDrilling();
            dump();

            progress += Time.delta/itemDuration;
            if(progress >= 1){
                progress %= 1;
                boostAmount = 1;
                for (Consume consumer : consumers) {
                    if(consumer.booster && consumer.optional && consumer.efficiency(this) > 0) boostAmount *= consumer.efficiencyMultiplier(this);
                    consumer.trigger(this);
                }
            }
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            itemRates.clear();

            tile.getLinkedTiles(t -> {
                //We check both the floor and the overlay here
                float productivity = drillSpeedFloor(t);
                if(productivity > 0) {
                    Item output = t.floor().itemDrop;
                    float value = itemRates.get(output, 0);
                    itemRates.put(output, value + productivity);
                }

                productivity = drillSpeedOverlay(t);
                if(productivity > 0) {
                    Item output = t.overlay().itemDrop;

                    float value = itemRates.get(output, 0);
                    itemRates.put(output, value + productivity);
                }
            });
        }

        public void updateDrilling(){

            for(Item item: itemRates.keys().toArray()){
                float current = drillTimers.get(item, 0);
                //Note that productionRates is in items/SECCOND, not tick, so we divide by 60
                current = Mathf.clamp(current + itemRates.get(item, 0)/60f * drillMultipliers.get(item, 1) * edelta() * boostAmount, 0, 1);

                if(current >= 1 && items.get(item) < itemCapacity){
                    current = 0;
                    handleItem(this, item);
                }

                drillTimers.put(item, current);
            }

            if(target == null && findTarget() == null) {
                time = 0;
                return;
            }

            if(entry.output == null || ItemLogic.capacity(entry.output, this)){
                time += edelta();

                //Reset the target tile after we're done
                if(time >= entry.time) {
                    if(entry.output != null) ItemLogic.addStacks(entry.output, this);
                    target.setFloor(entry.floor);
                    time = 0;
                    target = null;
                }
            }
        }

        public Tile findTarget(){
            entry = null;
            target = null;

            if(timer.get(targetTimer, 5)) tile.getLinkedTiles(t -> {
                GrinderEntry tileEntry = grinderMap.get(t.floor());
                if(tileEntry == null) return;

                target = t;
                entry = tileEntry;
            });
            return target;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
            write.f(time);
            BlockIO.writeItemMap(drillTimers, write);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            progress = read.f();
            time = read.f();
            drillTimers = BlockIO.readItemMap(read);
        }
    }
}
