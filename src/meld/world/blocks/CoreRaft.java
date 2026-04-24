package meld.world.blocks;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import meld.content.MeldStatusEffects;
import meld.graphics.Draww;
import meld.graphics.MeldLayers;
import meld.graphics.MeldShaders;
import meld.world.blocks.crafting.StorageIncinerator;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Incinerator;
import mindustry.world.blocks.production.ItemIncinerator;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;
import static mindustry.Vars.net;


//Rallies units nearby, provides an extended build radius for core units
public class CoreRaft extends CoreBlock {

    public static Seq<CoreRaftBuild> rafts = new Seq<>();

    private float nearestLen;

    public int spawnRange;

    public float rallyDuration = 60;

    public int rallyTimer = timers++;

    public CoreRaft(String name) {
        super(name);
        fogRadius = 40;
        lightRadius = 720;
        clipSize = 720;
        Events.on(EventType.WorldLoadEvent.class, e -> {
            rafts.clear();
            Team.sharded.cores().each(c -> {
                if(c instanceof CoreRaftBuild raft) rafts.add(raft);
            });
        });
        emitLight = true;
    }

    @Override
    public void init() {
        //Yknow I wish we had like a set defaults for this but nvm
        int f = fogRadius;
        float l = lightRadius;

        super.init();

        fogRadius = f;
        lightRadius = l;
    }

    //Allow spawning at an offset
    public static void playerSpawn(Tile tile, Player player) {
        if (player != null && tile != null) {
            Building var3 = tile.build;
            if (var3 instanceof CoreBuild) {
                CoreBuild core = (CoreBuild) var3;
                UnitType spawnType = ((CoreBlock) core.block).unitType;
                if (core.wasVisible) {
                    Fx.spawn.at(core);
                }

                player.set(core);
                if (!Vars.net.client()) {
                    Unit unit = spawnType.create(tile.team());
                    WeaponMount[] var5 = unit.mounts;
                    int var6 = var5.length;

                    for (int var7 = 0; var7 < var6; ++var7) {
                        WeaponMount mount = var5[var7];
                        mount.reload = mount.weapon.reload;
                    }

                    unit.set(core);
                    unit.rotation(90.0F);
                    unit.impulse(0.0F, 3.0F);
                    unit.spawnedByCore(true);
                    unit.controller(player);
                    unit.add();
                }

                if (Vars.state.isCampaign() && player == Vars.player) {
                    spawnType.unlock();
                }

                return;
            }
        }
    }

    public class CoreRaftBuild extends CoreBuild {
        public @Nullable Tile targetTile;
        public boolean building = false, lastBuilding = false;

        public Building incinerator;

        public boolean canIncinerate = false;

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            incinerator = null;
            proximity.each(b -> {
                if(b instanceof Incinerator.IncineratorBuild ||
                        b instanceof ItemIncinerator.ItemIncineratorBuild ||
                        b.block instanceof StorageIncinerator
                ) incinerator = b;
            });
            canIncinerate = incinerator != null;
        }

        @Override
        public void onRemoved() {
            super.onRemoved();
            rafts.remove(this);
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            rafts.add(this);
            return super.init(tile, team, shouldAdd, rotation);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < getMaximumAccepted(item) || canIncinerate && incinerator.acceptItem(source, item);
        }

        @Override
        public int getMaximumAccepted(Item item){
            return canIncinerate ? incinerator.getMaximumAccepted(item) : storageCapacity;
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source){
            boolean incinerate = canIncinerate && incinerateNonBuildable && !item.buildable;
            int realAmount = incinerate ? 0 : Math.min(amount, storageCapacity - items.get(item));
            super.handleStack(item, realAmount, source);

            if(team == state.rules.defaultTeam && state.isCampaign()){
                if(realAmount == 0 && incinerate){
                    incinerator.handleStack(item, amount, source);
                }

                state.rules.sector.info.handleCoreItem(item, amount);

            }
        }

        @Override
        public void handleItem(Building source, Item item){
            boolean incinerate = incinerateNonBuildable && !item.buildable;

            if(team == state.rules.defaultTeam){
                state.stats.coreItemCount.increment(item);
            }

            if(net.server() || !net.active()){
                if(team == state.rules.defaultTeam && state.isCampaign() && !incinerate){
                    state.rules.sector.info.handleCoreItem(item, 1);
                }

                if(items.get(item) >= storageCapacity || incinerate){
                    //create item incineration effect at random intervals
                    incinerator.handleItem(source, item);
                }else{
                    super.handleItem(source, item);
                }
            }else if(((state.rules.coreIncinerates && canIncinerate && items.get(item) >= storageCapacity) || incinerate) && !noEffect){
                incinerator.handleItem(source, item);
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();
            lastBuilding = building;
            building = false;

            if(timer.get(rallyTimer, 5)){
                Units.nearby(team, x, y, fogRadius() * tilesize, (other) -> {
                    other.apply(MeldStatusEffects.rally, rallyDuration);
                });
            }

            if(lastBuilding){

            }
        }

        @Override
        public void draw() {
            super.draw();
            Lines.stroke(4);
            Draw.color(Pal.accent);
            float layer = Draw.z();
            Draw.z(Layer.buildBeam);

            Draww.drawSonar(x, y, fogRadius * tilesize - 2, 4, MeldLayers.sonar, Pal.accent);

            Lines.circle(x, y, fogRadius * Vars.tilesize - 2);
            for (int i = 0; i < 1; i++) {
                float prog = ((Time.time + 200 * i) % 600) / 600;
                Lines.stroke(4 * (1 - prog));
                Lines.circle(x, y, fogRadius * Vars.tilesize * Interp.pow10Out.apply(prog));
            }

            Draw.z(layer);
        }

        /*
        @Override
        public void created() {
            super.created();
        }

        //Find the closest tile safe to spawn on, checking in 8 directions
        public void setupSafe(){
            if(!EntityCollisions.waterSolid(tile.x, tile.y)) targetTile = tile;
            int tx = tile.x, ty = tile.y;

            for(int i = 0; i < 8; i++){
                //Used in the spawn finder raycast
                boolean collided = false;

                Point2 dir = Geometry.d8(i);
                Tmp.v1.set(dir.x, dir.y).setLength(spawnRange).add(tx, ty);

                //on odd numbers, we're checking diagonals
                int steps = i % 2 == 0 ? spawnRange : (int) Math.sqrt(spawnRange);

                //Start outside of the core
                for(int t = size; t < steps; t++){
                    if(collided) continue;
                    Log.info("X: @, Y: @", tx + dir.x * t, ty + dir.y * t);

                    Tile current = Vars.world.tile(tx + dir.x * t, ty + dir.y * t);
                    if(current == null || current.solid()) {
                        collided = true;
                        continue;
                    }


                    Fx.explosion.at(current.worldx(), current.worldy());

                    //closest tile found
                    if(!EntityCollisions.waterSolid(current.x, current.y) && t < nearestLen) {
                        targetTile = current;
                        nearestLen = t;
                    }
                }
            }
        }

        public void requestSpawn(Player player) {
            if (unitType.supportsEnv(Vars.state.rules.env) && allowSpawn) {
                Call.playerSpawn(targetTile, player);
            }
        }
         */

        //Todo: figure out how to offset player spawns
    }
}
