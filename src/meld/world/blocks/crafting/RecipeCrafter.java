package meld.world.blocks.crafting;

import arc.Core;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import meld.world.blocks.crafting.recipe.ItemRecipe;
import meld.world.blocks.crafting.recipe.Recipe;
import meld.world.blocks.crafting.recipe.TimedRecipe;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

//HAAHHHAHAHAHHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//So on top of basic crafter functions we have... uh... yeah.......
public class RecipeCrafter extends Block {

    public ObjectMap<Liquid, int[]> outputDirections = new ObjectMap<>();

    private static final Seq<Liquid> tmpSlots = new Seq<>();

    public RecipeCrafter(String name) {
        super(name);
        update = true;
        solid = true;
    }

    public void setRecipes(TimedRecipe... recipies){
        this.recipes.set(recipies);
    }

    public int inputLiquidSlots, outputLiquidSlots;

    //list of items/liquids which this block accepts
    public Seq<Liquid> inputLiquids = new Seq<>();
    public Seq<Item> inputItems = new Seq<>();

    //List of items/liquids which get dumped
    public Seq<Liquid> outputLiquids = new Seq<>();
    public Seq<Item> outputItems = new Seq<>();

    public Seq<TimedRecipe> recipes = new Seq<>();

    public DrawBlock drawer = new DrawDefault();

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public boolean outputsItems(){
        return outputItems != null;
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out){
        drawer.getRegionsToOutline(this, out);
    }

    @Override
    public void init() {
        super.init();

        //MeldBlocks.aspectOutlet.inputLiquids.add(MeldLiquids.aether);MeldBlocks.aspectOutlet.outputLiquids.add(MeldLiquids.aspect)
        recipes.each(recipe -> {
            if(recipe.inputItems != null) for(ItemStack item: recipe.inputItems){
                inputItems.add(item.item);
                itemFilter[item.item.id] = true;
            }
            if(recipe.outputItems != null) for(ItemStack item: recipe.outputItems){
                outputItems.add(item.item);
            }
            if(recipe.inputLiquids != null) for(LiquidStack item: recipe.inputLiquids){
                inputLiquids.add(item.liquid);
                liquidFilter[item.liquid.id] = true;
            }
            if(recipe.outputLiquids != null) for(LiquidStack item: recipe.outputLiquids){
                outputLiquids.add(item.liquid);
            }
        });
    }

    //Just addLiquidBar but with a name
    public <T extends Building> void addLiquidBar(String name, Func<T, Liquid> current){
        addBar(name, entity -> new Bar(
                () -> current.get((T)entity) == null || entity.liquids.get(current.get((T)entity)) <= 0.001f ? Core.bundle.get("bar.liquid") : current.get((T)entity).localizedName,
                () -> current.get((T)entity) == null ? Color.clear : current.get((T)entity).barColor(),
                () -> current.get((T)entity) == null ? 0f : entity.liquids.get(current.get((T)entity)) / liquidCapacity)
        );
    }


    @Override
    public void setBars() {
        super.setBars();

        if(inputLiquidSlots + outputLiquidSlots > 0){
            removeBar("liquid");
            for(int i = 0; i < inputLiquidSlots; i++){
                final int j = i;
                addLiquidBar("liquid-in" + i, build -> {
                    RecipeCrafterBuild crafter = ((RecipeCrafterBuild) build);
                    return crafter.inputSlots.size <= j ? null : crafter.inputSlots.get(j);
                });
            }

            for(int i = 0; i < outputLiquidSlots; i++){
                final int j = i;
                addLiquidBar("liquid-out" + i, build -> {
                    RecipeCrafterBuild crafter = ((RecipeCrafterBuild) build);
                    return crafter.outputSlots.size <= j ? null : crafter.outputSlots.get(j);
                });
            }
        }
    }

    @Override
    public void load(){
        super.load();

        drawer.load(this);
    }

    public class RecipeCrafterBuild extends Building{

        RecipeCrafter crafter;
        public TimedRecipe recipe = null;

        public int last = -1;
        public float[] times;

        public Seq<Liquid> inputSlots = new Seq<>(), outputSlots = new Seq<>();

        @Override
        public void created() {
            super.created();
            crafter = (RecipeCrafter) block;
            times = new float[recipes.size];
        }

        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public void transferLiquid(Building next, float amount, Liquid liquid) {
            super.transferLiquid(next, amount, liquid);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            //If the respective slot is full, don't accept any more liquids
            if(source == this ?
                    !outputSlots.contains(liquid) && outputSlots.size >= outputLiquidSlots
                    :
                    !inputSlots.contains(liquid) && inputSlots.size >= inputLiquidSlots
            ) return false;

            return super.acceptLiquid(source, liquid);
        }


        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount) {
            //TODO: Handle multislot
            if(inputLiquids.contains(liquid) && outputLiquids.contains(liquid)){

            }

            Seq<Liquid> slots = source == this ? outputSlots : inputSlots;
            if(!slots.contains(liquid)){
                slots.add(liquid);
            }

            this.liquids.add(liquid, amount);
        }

        @Override
        public void updateTile() {
            super.updateTile();

            tmpSlots.set(inputSlots);
            tmpSlots.each(l -> {
                if(Mathf.zero(liquids.get(l))) inputSlots.remove(l);
            });
            tmpSlots.set(outputSlots);
            tmpSlots.each(l -> {
                if(Mathf.zero(liquids.get(l))) outputSlots.remove(l);
            });

            if(recipe != null && recipe.valid(block, this)){
                recipe.update(block, this);
                float time = times[last];
                time += efficiency;
                if(time >= recipe.craftTime){
                    recipe.apply(crafter, this);
                    time %= recipe.craftTime;
                }
                times[last] = time;
            }
            else {
                recipe = recipes.find(r -> r.valid(crafter, this));
                if(recipe != null) last = recipes.indexOf(recipe);
            }

            dumpOutputs();
        }


        public void dumpOutputs(){
            if(outputSlots.size > 0){

                for(Liquid output: outputSlots){
                    if(!outputDirections.containsKey(output)) {
                        dumpLiquid(output);
                        continue;
                    }

                    int[] dirs = outputDirections.get(output);
                    for(int dir: dirs){
                        dumpLiquid(output, 2f, dir);
                    }
                }
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(last);
            write.i(times.length);
            for(float f: times){
                write.f(f);
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            last = read.i();
            int length = read.i();

            times = new float[recipes.size];
            for(int i = 0; i < length; i++){
                times[i] = read.i();
            }
            rehashSlots();
        }

        public void rehashSlots(){
            Vars.content.liquids().each(l -> {
                if(outputLiquids.contains(l)) outputSlots.add(l);
                if(inputLiquids.contains(l)) inputSlots.add(l);
            });
        }
    }
}
