package meld.world.blocks.crafting;

import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.struct.IntFloatMap.*;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ctype.*;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.units.UnitAssemblerModule.*;
import mindustry.world.meta.*;

import java.util.*;

import static mindustry.Vars.*;
import static mindustry.Vars.tilesize;

public class ModularCrafter extends PayloadBlock{

    //Modules updates every update, listeners are for modules with specific events
    public Seq<CrafterModule> modules = new Seq<CrafterModule>();
    public ObjectMap<Object, Seq<CrafterModule>> listeners = new ObjectMap<Object, Seq<CrafterModule>>();

    //Lists of stuff which this block accepts
    //note: could be a single HashSet<UnlockableContent>.
    public Seq<Liquid> acceptedLiquids = new Seq<>();
    public Seq<Item> acceptedItems = new Seq<>();
    public Seq<UnlockableContent> acceptedPayloads = new Seq<>();

    //Lists of stuff which gets dumped
    public Seq<Liquid> dumpedLiquids = new Seq<>();
    public Seq<Item> dumpedItems = new Seq<>();
    public Seq<UnlockableContent> dumpedPayloads = new Seq<>();

    public boolean replaceBars = true;
    public int payloadCapacity = 3;

    /// Default float data array
    public IntFloatMap defaultData = new IntFloatMap();
    /// When true, shows a table with the data map in the block's display.
    public boolean debugTable = true;


    public ModularCrafter(String name) {
        super(name);
        update = true;
        solid = true;
        sync = true;
    }

    @Override
    public void setBars() {
        super.setBars();

        if(replaceBars && acceptedLiquids.size + dumpedLiquids.size > 0){
            removeBar("liquid");

            for(Liquid liquid: acceptedLiquids){
                addLiquidBar(liquid);
            }
            for(Liquid liquid: dumpedLiquids){
                addLiquidBar(liquid);
            }
        }
    }

    @Override
    public void init(){
        super.init();
        modules.each(m -> m.setup(this));
    }

    public static void trigger(ModularCrafter block, ModularCrafterBuild build, Object event){
        Seq<CrafterModule> events = block.listeners.get(event);
        if(events != null) events.each(c -> c.update(build));
    }

    public void hook(Object event, CrafterModule module){
        Seq<CrafterModule> events = listeners.get(event);
        if(events == null) {
            listeners.put(event, Seq.with(module));
            return;
        }
        events.add(module);
    }

    public void hookAll(Object event, CrafterModule... module){
        Seq<CrafterModule> events = listeners.get(event);
        if(events == null) {
            listeners.put(event, Seq.with(module));
            return;
        }
        events.add(module);
    }

    public static class BlockEvent{
        public enum Defaults{
            update,
            proximityUpdate
        }
    }

    public static abstract class CrafterModule{
        public void update(ModularCrafterBuild build){

        }

        public void setup(ModularCrafter block){

        }
    }

    public static abstract class CraftingModule extends CrafterModule{
        public int efficiencyPin = -1;
        public int[] efficiencyPins;
        public int progressPin;
        public float craftTime = 60;

        public boolean canCraft(ModularCrafterBuild build){
            return true;
        };

        public void update(ModularCrafterBuild build){
            if(!canCraft(build)) return;

            float efficiency = 1;
            if(efficiencyPin != -1) efficiency = build.getPin(efficiencyPin);
            if(efficiencyPins != null){
                for(int i: efficiencyPins){
                    efficiency *= build.getPin(i);
                }
            }

            float progress = build.getPin(progressPin);
            progress += efficiency * Time.delta;

            if(progress > craftTime) {
                craft(build);
                progress %= craftTime;
            }
            build.setPin(progressPin, progress);
        }

        public abstract void craft(ModularCrafterBuild build);
    }


    //Just some static finals to help readability
    public static class ModOUT{
        public static final float ON = 1, OFF = 0;
        public static final int
            ZERO = 0,
            ONE = 1,
            TWO = 2,
            THREE = 3,
            FOUR = 4,
            FIVE = 5,
            SIX = 6,
            SEVEN = 7,
            EIGHT = 8,
            NINE = 9,
            TEN = 10,
            ELEVEN = 11,
            TWELVE = 12;
    };
    public static class ModIN{
        public static final float ON = 1, OFF = 0;
        public static final int
            ZERO = 0,
            ONE = 1,
            TWO = 2,
            THREE = 3,
            FOUR = 4,
            FIVE = 5,
            SIX = 6,
            SEVEN = 7,
            EIGHT = 8,
            NINE = 9,
            TEN = 10,
            ELEVEN = 11,
            TWELVE = 12;
    };

    public static final float ON = 1, OFF = 0;

    public class ModularCrafterBuild extends PayloadBlockBuild<Payload>{
        public ModularCrafter modular;
        public IntFloatMap data = new IntFloatMap();
        public PayloadSeq payloads = new PayloadSeq();

        @Override
        public void draw(){
            super.draw();

            drawPayload();
        }

        @Override
        public void display(Table table){
            super.display(table);

            if(debugTable){
                table.row();
                table.table(d -> {
                    Runnable rebuild = () -> {
                        d.clearChildren();
                        d.table(debug -> {
                            data.iterator().forEachRemaining(entry -> {
                                debug.add(entry.key + ": " + StatValues.fixValue(entry.value)).left();
                                debug.row();
                            });
                        }).left();
                        d.table().growX(); //ugh
                    };
                    d.update(rebuild);
                    rebuild.run();
                }).growX(); //It doesn't actually grow for some fucking reason. See ugh above.
            }
        }

        @Override
        public void created() {
            super.created();
            data.putAll(defaultData);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            this.dumpOutputs();
        }

        public void dumpOutputs(){
            dumpedLiquids.each(this::dumpLiquid);
            dumpedItems.each(this::dump);
        }

        public void setPin(int pin, float value){
            data.put(pin, value);
        }

        public float getPin(int pin){
            return data.get(pin);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return super.acceptLiquid(source, liquid) || acceptedLiquids.contains(liquid);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return (this.block.consumesItem(item) || acceptedItems.contains(item)) && this.items.get(item) < this.getMaximumAccepted(item);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return this.payload == null && acceptedPayloads.contains(payload.content()) && payloads.get(payload.content()) < payloadCapacity;
        }

        @Override
        public Building create(Block block, Team team) {
            modular = (ModularCrafter) block;
            return super.create(block, team);
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            trigger(modular, this, BlockEvent.Defaults.proximityUpdate);
        }

        @Override
        public void update() {
            super.update();
            modules.each(c -> c.update(this));
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(data.size);
            int[] keys = data.keys().toArray().toArray();

            for(int i: keys){
                write.i(i);
                write.f(data.get(i));
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            data = new IntFloatMap();
            int len = read.i();
            for (int i = 0; i < len; i++){
                data.put(read.i(), read.f());
            }
        }
    }
}
