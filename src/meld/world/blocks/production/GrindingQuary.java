package meld.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import meld.graphics.Draww;
import meld.graphics.TextModifiers;
import meld.graphics.TileDrawers;
import meld.world.WorldUtil;
import meld.world.blocks.io.BlockIO;
import meld.world.util.ItemLogic;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.consumers.Consume;
import mindustry.world.modules.ItemModule;

import static meld.graphics.TileDrawers.tileRad;

public class GrindingQuary extends Block {

    public static ObjectMap<Block, GrinderEntry> grinderMap = new ObjectMap<>();
    public static Seq<Item> alwaysDump = new Seq<>();

    public static ObjectFloatMap<Item> drillMultipliers = new ObjectFloatMap<>();

    public float drillSpeed = 0.25f;

    public int targetTimer = timers++;

    public float itemDuration = 60;

    public GrindingQuary(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        itemCapacity = 50;
    }

    public Seq<Tile> valid = new Seq<>(), overlay = new Seq<>();
    float fogTiles = 0;

    @Override
    public void drawOverlay(float x, float y, int rotation) {
        super.drawOverlay(x, y, rotation);


        Tile start = Vars.world.tileWorld(x, y);
        fogTiles = 0;
        float multiplier = 1;

        if(start != null){
            valid.clear();
            overlay.clear();

            if(Vars.control.input.block == null && start.build instanceof GrindingQuaryBuild build && start== build.tile && start.block() == this){
                multiplier = build.boostAmount;
            }

            start.getLinkedTilesAs(this, tile -> {
                if(tile != null){
                    if(!Vars.fogControl.isDiscovered(Vars.player.team(), tile.x, tile.y)){
                        fogTiles++;
                        TileDrawers.drawFog(tile.worldx(), tile.worldy(), tileRad);
                        return;
                    }
                    if(tile.solid() && tile.block() != this){
                        TileDrawers.drawInvalid(tile.worldx(), tile.worldy(), tileRad);
                        return;
                    }
                    if(grinderMap.containsKey(tile.floor())){
                        valid.add(tile);
                    }
                    if(tile.overlay().itemDrop != null){
                        overlay.add(tile);
                    }
                }
            });

            //Draw this in stages
            valid.each(tile -> {
                Draw.color(Pal.accent);
                Draw.alpha(0.5f);
                Fill.square(tile.worldx(), tile.worldy(), Vars.tilesize/2f);

            });

            Lines.stroke(3.75f, Pal.gray);
            Lines.square(x, y, size * Vars.tilesize/2f + 1.25f);
            Lines.stroke(1.25f, Pal.accent);
            Lines.square(x, y, size * Vars.tilesize/2f);

            valid.each(tile -> {
                Draw.color(Color.white);
                float prog = Mathf.absin(Time.globalTime/30 + (tile.x + tile.y) * Mathf.pi/3, 1, 1);
                float prog2 = Time.globalTime/240 + (tile.x + tile.y) * Mathf.pi % 1;
                Draw.alpha(0.5f + 0.5f * prog);
                Draw.rect(tile.floor().region, tile.worldx(), tile.worldy() -2 + prog * 4, 4 + 4 * prog, 4 + 4 * Mathf.sin(prog), -15 + 30 * prog + 360 * prog2);
            });
            overlay.each(tile -> {
                if(tile.overlay().itemDrop != null){
                    Draw.color(Color.white);
                    Draw.rect(tile.overlay().itemDrop.fullIcon, tile.worldx(), tile.worldy());
                }
            });
        }

        Drawf.text();

        boolean found = false;

        float fogPercent = fogTiles/size/size;
        float fin = fogPercent * fogPercent;
        float glitchChance = fin, glitchSpeed = 2 + fin * 8;

        float drawx = x, drawy = y - (size * Vars.tilesize)/2f - 2;
        ItemModule depositItems = WorldUtil.totalQuarry(x, y, this, true, true);
        for (Item item : Vars.content.items()) {
            float amount = depositItems.get(item);
            if(amount == 0) continue;
            found = true;
            Draww.itemText(TextModifiers.glitchy(item.localizedName + ": " + TextModifiers.glitchyNumb(amount, glitchChance * 4, glitchSpeed * 4), 2, glitchChance, glitchSpeed),
                    drawx, drawy, item);

            drawy -= 10;
        }
        if(!found){
            Draww.drawTextUnderlined(TextModifiers.glitchyEntry("overlay.deposit-missing", 2, glitchChance, glitchSpeed),
                    drawx, drawy, Pal.lightishGray);
        }

        found = false;

        drawy = y + (size * Vars.tilesize)/2f + 2;
        ObjectFloatMap<Item> drillSpeeds = WorldUtil.quarrySpeed(x, y, this, true);
        for (Item item : Vars.content.items()) {
            float amount = drillSpeeds.get(item, 0);
            if(amount == 0) continue;
            found = true;
            Draww.itemText(TextModifiers.glitchy(item.localizedName + ": " + TextModifiers.glitchyNumb(amount * multiplier, glitchChance * 4, glitchSpeed * 4) + "/s", 2, glitchChance, glitchSpeed),
                    drawx, drawy, item);

            drawy += 10;
        }

        if(!found){
            Draww.drawTextUnderlined(TextModifiers.glitchyEntry("overlay.ore-missing", 2, glitchChance, glitchSpeed),
                    drawx, drawy, Pal.lightishGray);
        }

        drawx = x - (size * Vars.tilesize)/2f - 2;
        drawy = y;

        if(multiplier > 1){
            Draww.drawTextUnderlined(multiplier + "x ", drawx, drawy, Pal.accent);
        }
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
        return drillMultipliers.get(tile.floor().itemDrop, 1) * drillSpeed;
    }
    public float drillSpeedOverlay(Tile tile){
        if(tile == null || tile.overlay().itemDrop == null) return 0;
        return drillMultipliers.get(tile.overlay().itemDrop, 1) * drillSpeed;
    }

    public class GrindingQuaryBuild extends Building{
        public float time = 0;
        public Tile target = null;
        public GrinderEntry entry;
        public float boostAmount = 1;

        public Seq<Building> nearbyDepots = new Seq<>();

        @Override
        public void onProximityAdded() {
            super.onProximityAdded();
        }

        //Timer used to determine when the current batch of items expires
        public float progress = 0;

        public ObjectFloatMap<Item> drillTimers = new ObjectFloatMap<>(), itemRates = new ObjectFloatMap<>();

        @Override
        public boolean shouldConsume() {
            for (Item key : itemRates.keys()) {
                if(itemRates.get(key, 0) * drillMultipliers.get(key, 1) > 0 && acceptItem(this, key)) return true;
            }
            return target != null && ItemLogic.capacity(entry.output, this);
        }

        @Override
        public void update(){
            super.update();
            updateDrilling();

            for (Item key : drillTimers.keys()) {
                dumpAccumulate(key);
            }

            alwaysDump.each(this::dumpAccumulate);

            progress += Time.delta/itemDuration;
            if(progress >= 1){
                progress %= 1;

                //Get the speed boost from the non optional aspect consumer
                boostAmount = 1;

                for (Consume consumer : consumers) {
                    if(!consumer.booster && consumer.efficiency(this) > 0) boostAmount = consumer.efficiencyMultiplier(this);
                }
                //Get the speed boost from the non optional aspect consumer

                for (Consume consumer : consumers) {
                    if(consumer.booster && consumer.optional && consumer.efficiency(this) > 0) boostAmount *= consumer.efficiencyMultiplier(this);
                    consumer.trigger(this);
                }
            }
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            nearbyDepots.clear();
            proximity.each(b -> {
                if(b instanceof Depot.DepotBuild) nearbyDepots.add(b);
            });

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

        @Override
        public boolean acceptItem(Building source, Item item) {
            if((source instanceof Depot.DepotBuild)) return false;
            if(source == this){
                return nearbyDepots.size > 0 ? nearbyDepots.find(b -> b.acceptItem(source, item)) != null : this.items.get(item) < this.getMaximumAccepted(item);
            }
            return this.block.consumesItem(item) && this.items.get(item) < this.getMaximumAccepted(item);
        }

        @Override
        public void handleItem(Building source, Item item) {
            if(source == this && nearbyDepots.size > 0){
                nearbyDepots.find(b -> b.acceptItem(source, item)).handleItem(source, item);
                return;
            }
            super.handleItem(source, item);
        }

        public void updateDrilling(){

            for(Item item: itemRates.keys().toArray()){
                float current = drillTimers.get(item, 0);
                //Note that productionRates is in items/SECCOND, not tick, so we divide by 60
                current = Mathf.clamp(current + itemRates.get(item, 0)/60f * drillMultipliers.get(item, 1) * edelta() * boostAmount, 0, 1);

                if(current >= 1 && acceptItem(this, item)){
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
