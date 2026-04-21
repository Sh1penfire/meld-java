package meld.world.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import meld.world.blocks.crafting.recipe.SpoolRecipe;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;

public class Gauze extends Block {

    public float craftTime = 60;
    public float spoolStorage = 800;

    public Seq<SpoolRecipe> recipies = new Seq<>();

    public float reload = 120;
    public float detectHeadstart = 0.75f;

    public float syncMargin = 20;

    public int propagateMaxRange = 10;

    public float pulseStrength = 200;
    public float maxFract = 0.25f;

    Seq<Building> searching = new Seq<>(), qued = new Seq<>(), explored = new Seq<>();
    Seq<GauzeBuild> connectedGauzes = new Seq<>();

    public Gauze(String name) {
        super(name);
        update = true;
        solid = true;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar(
                "spool",
                entity -> new Bar("stats.spool", Pal.heal, () -> ((GauzeBuild)entity).spoolf()).blink(Color.red)
        );
    }

    public class GauzeBuild extends Building {

        Gauze gauze;

        public float time;
        public float craftTimer;
        public float energy = 0;

        public float spoolf(){
            return energy/spoolStorage;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(energy >= pulseStrength){
                if(time >= reload) {
                    time %= reload;
                    propagate();
                }
                time += Time.delta;
            }

            craftTimer += efficiency;
            if(craftTimer >= craftTime){
                craft();
            }
        }

        @Override
        public void created() {
            super.created();
            gauze = (Gauze) block;
        }

        public boolean shouldCraft(){
            return recipies.copy().find(r -> r.valid(gauze, this)) != null;
        }

        @Override
        public boolean shouldConsume() {
            return super.shouldConsume() && shouldCraft();
        }

        public void craft(){
                SpoolRecipe recipe = recipies.find(r -> r.valid(gauze, this));
                if(recipe != null){
                    //I've somehow had recipe be null so im not taking any chances here
                    recipe.apply(gauze, this);
                    craftTimer %= craftTime;
                }
        }

        //Only propagates through 1x1 blocks and their adjacent blocks
        public void propagate(){
            explored.clear();
            searching.clear();
            qued.clear();

            explored.add(this);
            searching.addAll(this.proximity);

            boolean healed = false;
            float charge = pulseStrength;

            for(int i = 0; i < propagateMaxRange; i++){
                for(Building build: searching){
                    Fx.healBlock.at(build.x, build.y, 1);
                    if(build.damaged()){
                        /**
                         * Max of a few numbers
                         * -Missing health
                         * -maxFract * Math.max(build.maxHealth pulseStrength)
                         * -Remaining charge
                         * -0
                         */
                        float healAmount = Mathf.maxZero(Math.min(Math.min(Math.min(build.maxHealth, pulseStrength) * maxFract, build.block.health - build.health), charge));
                        if(healAmount > 0){
                            Fx.healBlockFull.at(build.x, build.y, 0, Pal.heal, build.block);
                            build.heal(healAmount);
                            charge -= healAmount;
                            healed = true;
                        }
                    }

                    if(build.block.size == 1) build.proximity.each( b -> !explored.contains(b), qued::addUnique);
                    if(build instanceof GauzeBuild gauze){
                        connectedGauzes.add(gauze);
                    }
                }

                explored.add(searching);
                searching.set(qued);
                qued.clear();

                //No more healing to expand, goodbye world
                if(charge == 0) break;
            }

            if(healed) time = reload * detectHeadstart;
            energy = energy - Mathf.clamp(pulseStrength - charge, 0, pulseStrength);

            float totalTime = 0;
            connectedGauzes.addAll(this);

            //Remove gauzes close to firing, then sync

            connectedGauzes.removeAll(b -> reload - b.time < syncMargin);
            for(GauzeBuild gauze: connectedGauzes){
                totalTime += gauze.time;
            }

            float averageTime = totalTime / (connectedGauzes.size);

            for(GauzeBuild gauze: connectedGauzes){
                gauze.time = averageTime;
            }

            connectedGauzes.clear();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(time);
            write.f(craftTimer);
            write.f(energy);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            time = read.f();
            craftTimer = read.f();
            energy = read.f();
        }

        @Override
        public void draw() {
            super.draw();

            Lines.arc(x, y, Vars.tilesize * 3, time/reload);
        }
    }
}
