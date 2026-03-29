package meld.world.blocks;

import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import meld.world.WorldUtil;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Attribute;

public class ModularCrafter extends Block {

    //Modules updates every update, listeners are for modules with specific events
    public Seq<CrafterModule> modules = new Seq<CrafterModule>();
    public ObjectMap<Object, Seq<CrafterModule>> listeners = new ObjectMap<Object, Seq<CrafterModule>>();

    public ModularCrafter(String name) {
        super(name);
        update = true;
    }

    //Default float data array
    public float[] defaultData;

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
    };

    public static abstract class CrafterModule{
        public void update(ModularCrafterBuild build){

        }
    }

    public static abstract class CraftingModule extends CrafterModule{
        public int efficiencyPin;
        public int progressPin;
        public float craftTime;

        public void update(ModularCrafterBuild build){
            float efficiency = build.data[efficiencyPin];
            float progress = build.data[progressPin];
            progress += efficiency * Time.delta;

            if(progress > craftTime) {
                craft(build);
                progress %= craftTime;
            }
            build.data[progressPin] = progress;
        }

        public abstract void craft(ModularCrafterBuild build);
    }

    public static class MultiplierModule extends CrafterModule{
        public int[] inputPins;
        public int outputPin;

        @Override
        public void update(ModularCrafterBuild build) {
            float value = 1;
            for(int i: inputPins){
                value *= build.data[i];
            }
            build.data[outputPin] = value;
        }
    }

    public static class AttributeModule extends CrafterModule{
        public int efficiencyPin;

        public Attribute attribute;
        public float baseEfficiency = 1;
        public float boostScale = 1;
        public float maxBoost = 1;
        public float minEfficiency = -1;

        @Override
        public void update(ModularCrafterBuild build) {
            float efficiency = baseEfficiency + Math.min(maxBoost, boostScale * build.block.sumAttribute(attribute, build.tileX(), build.tileY()) + attribute.env());

            Log.info(efficiency);

            if(minEfficiency != -1 && efficiency < minEfficiency) {
                build.data[efficiencyPin] = 0;
                return;
            }

            build.data[efficiencyPin] = efficiency;
        }
    }

    public static class ItemCraftingModule extends CraftingModule{
        public ItemStack outputItem;
        public ItemStack[] outputItems;


        @Override
        public void craft(ModularCrafterBuild build) {
            if(outputItem != null) build.items.add(outputItem.item, outputItem.amount);

            if(outputItems != null) {
                for (ItemStack item : outputItems) {
                    build.items.add(item.item, item.amount);
                }
            }
        }
    }

    public class ModularCrafterBuild extends Building {
        public ModularCrafter modular;
        public float[] data = defaultData;

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
    }
}
