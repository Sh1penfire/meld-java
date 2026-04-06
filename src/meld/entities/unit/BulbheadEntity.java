package meld.entities.unit;

import arc.Events;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.*;
import meld.Meld;
import meld.content.MeldUnits;
import meld.graphics.Draww;
import meld.graphics.MeldRegions;
import meld.world.blocks.CoreRaft;
import mindustry.Vars;
import mindustry.async.PhysicsProcess;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.EntityCollisions;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Sounds;
import mindustry.gen.UnitWaterMove;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.storage.CoreBlock;

import static meld.Meld.slowFactor;
import static meld.Meld.slowProg;

//Bandaid fix for multiplayer units dying randomly because I legit have no idea how to fix it otherwise
public class BulbheadEntity extends UnitWaterMove {

    @Nullable
    public CoreRaft.CoreRaftBuild nearbyRaft;

    //Actually counts upwards... kinda counter intuitively named
    public float corelinkGrace = 0;

    //skip all the logic involved and just give rotation
    @Override
    public float prefRotation() {
        return this.moving() && this.type.omniMovement ? this.vel().angle() : this.rotation;
    }

    @Override
    public boolean canShoot() {
        return super.canShoot();
    }


    //Try this...
    @Override
    public void rotateMove(Vec2 vec) {
        this.moveAt(Tmp.v2.trns(this.rotation, vec.len()));
        if (!vec.isZero()) {
            this.rotation = Angles.moveToward(this.rotation, vec.angle(), this.type.rotateSpeed * Time.delta * this.speedMultiplier/ slowFactor);
        }

    }

    @Override
    public void update() {
        dragMultiplier *= slowFactor;

        Time.delta /= slowFactor;

        getNearbyLink();

        Tile tile = this.tileOn();

        if (tile != null && !this.canPassOn()) {
            for(int i = 0; i < 4; i++){
                Tile other = tile.nearby(i);
                if(!EntityCollisions.waterSolid(other.x, other.y)){
                    x = other.worldx();
                    y = other.worldy();

                    Log.info("SAVED");
                }
            }
        }
        super.update();

        Time.delta *= slowFactor;

        //speedMultiplier /= slowFactor;
    }

    public void getNearbyLink(){
        nearbyRaft = CoreRaft.rafts.sort(raft -> raft.dst(this)).first();
        if(!within(nearbyRaft, type.fogRadius * Vars.tilesize)) nearbyRaft = null;
    }

    //Copying a lot from vanilla's source code... yeah...
    //Reuseable code I guess
    @Override
    public boolean activelyBuilding() {
        if (this.isBuilding()) {
            BuildPlan plan = this.buildPlan();
            if (!Vars.state.isEditor() && plan != null && !withinBuildRange(plan)) {
                return false;
            }
        }

        return this.isBuilding() && this.updateBuilding;
    }

    public boolean withinBuildRange(Position p){
        return Vars.state.rules.infiniteResources || within(p, type.buildRange) || nearbyRaft != null && nearbyRaft.within(p, nearbyRaft.fogRadius() * Vars.tilesize);
    }

    public float buildDistance(Position p){
        return nearbyRaft != null ? Math.min(dst(p), nearbyRaft.dst(p)) : dst(p);
    }

    //God this is a lot of reused code isn't it
    public void updateBuildLogic() {
        if (!(this.type.buildSpeed <= 0.0F)) {
            if (!Vars.headless) {
                if (this.lastActive != null && this.buildAlpha <= 0.01F) {
                    this.lastActive = null;
                }

                this.buildAlpha = Mathf.lerpDelta(this.buildAlpha, this.activelyBuilding() ? 1.0F : 0.0F, 0.15F);
            }

            this.validatePlans();
            if (this.updateBuilding && this.canBuild()) {
                boolean infinite = Vars.state.rules.infiniteResources || this.team().rules().infiniteResources;
                this.buildCounter += Time.delta;
                if (Float.isNaN(this.buildCounter) || Float.isInfinite(this.buildCounter)) {
                    this.buildCounter = 0.0F;
                }

                this.buildCounter = Math.min(this.buildCounter, 10.0F);
                boolean instant = Vars.state.rules.instantBuild && Vars.state.rules.infiniteResources;
                int maxPerFrame = instant ? this.plans.size : 10;
                int count = 0;
                CoreBlock.CoreBuild core = this.core();
                if (core != null || infinite) {
                    while((this.buildCounter >= 1.0F || instant) && count++ < maxPerFrame && this.plans.size > 0) {
                        --this.buildCounter;
                        boolean hasAll;
                        if (this.plans.size > 1) {
                            int total = 0;
                            int size = this.plans.size;
                            float bestDst = Float.MAX_VALUE;
                            hasAll = false;

                            int bestIndex;
                            for(bestIndex = -1; total < size; ++total) {
                                BuildPlan plan = this.buildPlan();

                                //Tweakead so anything within the nearest raft core's build radius gets validated
                                float dst = plan.dst2(this);
                                boolean within = withinBuildRange(plan);
                                if (within && !this.shouldSkip(plan, core)) {
                                    hasAll = true;
                                    break;
                                }

                                if (within && dst < bestDst) {
                                    bestIndex = total;
                                    bestDst = dst;
                                }

                                this.plans.removeFirst();
                                this.plans.addLast(plan);
                            }

                            if (!hasAll && bestIndex > 0 && withinBuildRange(buildPlan())) {
                                for(int i = 0; i < bestIndex; ++i) {
                                    this.plans.addLast(this.plans.removeFirst());
                                }
                            }
                        }

                        BuildPlan current = this.buildPlan();
                        Tile tile = current.tile();
                        this.lastActive = current;
                        this.buildAlpha = 1.0F;
                        if (current.breaking) {
                            this.lastSize = tile.block().size;
                        }

                        if (withinBuildRange(tile)) {
                            if (!Vars.headless) {
                                Vars.control.sound.loop(Sounds.loopBuild, tile, 1.3F);
                            }

                            Building var18 = tile.build;
                            ConstructBlock.ConstructBuild entity;
                            if (var18 instanceof ConstructBlock.ConstructBuild) {
                                entity = (ConstructBlock.ConstructBuild)var18;
                                if (tile.team() != this.team && tile.team() != Team.derelict || !current.breaking && (entity.current != current.block || entity.tile != current.tile())) {
                                    this.plans.removeFirst();
                                    continue;
                                }
                            } else if (!current.initialized && !current.breaking && Build.validPlaceIgnoreUnits(current.block, this.team, current.x, current.y, current.rotation, true, true)) {
                                if (!Build.checkNoUnitOverlap(current.block, current.x, current.y)) {
                                    this.plans.removeFirst();
                                    this.plans.addLast(current);
                                    continue;
                                }

                                hasAll = infinite || current.isRotation(this.team) || tile.team() == Team.derelict && tile.block() == current.block && tile.build != null && tile.block().allowDerelictRepair && Vars.state.rules.derelictRepair || !Structs.contains(current.block.requirements, (ix) -> {
                                    return !core.items.has(ix.item, Math.min(Mathf.round((float)ix.amount * Vars.state.rules.buildCostMultiplier), 1));
                                });
                                if (hasAll) {
                                    Call.beginPlace(this, current.block, this.team, current.x, current.y, current.rotation, current.block.instantBuild ? current.config : null);
                                    if (!Vars.net.client() && current.block.instantBuild) {
                                        if (this.plans.size > 0) {
                                            this.plans.removeFirst();
                                        }
                                        continue;
                                    }
                                } else {
                                    current.stuck = true;
                                }
                            } else {
                                if (current.initialized || !current.breaking || !Build.validBreak(this.team, current.x, current.y)) {
                                    this.plans.removeFirst();
                                    continue;
                                }

                                Call.beginBreak(this, this.team, current.x, current.y);
                            }

                            if (tile.build instanceof ConstructBlock.ConstructBuild && !current.initialized) {
                                Events.fire(new EventType.BuildSelectEvent(tile, this.team, this, current.breaking));
                                current.initialized = true;
                            }

                            var18 = tile.build;
                            if (var18 instanceof ConstructBlock.ConstructBuild) {
                                entity = (ConstructBlock.ConstructBuild)var18;
                                float bs = 1.0F / entity.buildCost * this.type.buildSpeed * this.buildSpeedMultiplier * Vars.state.rules.buildSpeed(this.team);
                                if (current.breaking) {
                                    entity.deconstruct(this, core, bs);
                                } else if (entity.current != null && (Vars.state.isEditor() || Vars.state.rules.waves && this.team == Vars.state.rules.waveTeam && entity.current.isVisible() || entity.current.unlockedNowHost() && entity.current.environmentBuildable() && entity.current.isPlaceable())) {
                                    entity.construct(this, core, bs, current.config);
                                }

                                current.stuck = Mathf.equal(current.progress, entity.progress);
                                current.progress = entity.progress;
                            }
                        }
                    }

                }
            }
        }
    }

    public void draw(){
        super.draw();

        //Draww.drawChain(chain, x, y, Core.input.mouseWorldX(), Core.input.mouseWorldY(), 0);

        if(nearbyRaft != null){
            float bsize = nearbyRaft.block.size/2f * Vars.tilesize;
            Draw.z(Layer.blockBuilding - 1);
            Draw.quad(nearbyRaft.block.fullIcon,
                    nearbyRaft.x - bsize, nearbyRaft.y - bsize, Tmp.c1.set(Color.red).shiftHue(Time.time).toFloatBits(),
                    nearbyRaft.x - bsize, nearbyRaft.y + bsize, Tmp.c1.set(Color.green).shiftHue(Time.time).toFloatBits(),
                    nearbyRaft.x + bsize, nearbyRaft.y + bsize, Tmp.c1.set(Color.blue).shiftHue(Time.time).toFloatBits(),
                    nearbyRaft.x + bsize, nearbyRaft.y - bsize, Tmp.c1.set(Color.yellow).shiftHue(Time.time).toFloatBits());

            Draw.blend(Blending.additive);
            Draw.z(Layer.effect);
            Draw.color(Pal.accent);
            //How close/far the unit is away from the core on a scale of 0-1
            float dist = dst(nearbyRaft);
            float stretch = 1 - dist/(type.fogRadius * Vars.tilesize);
            //Draww.drawChainAlpha(MeldRegions.chain, nearbyRaft.x, nearbyRaft.y, x, y, 1 - 0.5f * stretch, 1 + 2 * stretch, 0,a -> Interp.slope.apply(1 - a));
            Draww.drawChainAlpha(MeldRegions.chain, nearbyRaft.x, nearbyRaft.y, x, y, 1 - 0.5f * Interp.pow5.apply(1-stretch), 0.5f + 0.5f * stretch, 0, f -> Interp.slope.apply((f + dist)/(type.fogRadius * Vars.tilesize) % 1 + Time.time/60));
            Draw.blend(Blending.normal);
        }
    }

    //Attempt to draw the build beam from the unit itself. If that fails, draw it to the core instead, then draw the build beam from the core
    public void drawBuilding() {
        boolean active = this.activelyBuilding();
        if (plans.size > 0 && (active || this.lastActive != null)) {
            Draw.z(115.0F);
            BuildPlan plan = active ? this.buildPlan() : this.lastActive;
            Tile tile = plan.tile();
            CoreBlock.CoreBuild core = this.team.core();

            Position origin = within(plan, type.buildRange) ? this : nearbyRaft;
            if (tile != null && origin != null) {
                if (core != null && active && !this.isLocal() && !(tile.block() instanceof ConstructBlock)) {
                    Draw.z(84.0F);
                    this.drawPlan(plan, 0.5F);
                    this.drawPlanTop(plan, 0.5F);
                    Draw.z(115.0F);
                }

                if (type.drawBuildBeam) {
                    float focusLen = type.buildBeamOffset + Mathf.absin(Time.time, 3.0F, 0.6F);
                    float px = origin.getX();
                    float py = origin.getY();
                    if(origin == this){
                        px += Angles.trnsx(rotation, focusLen);
                        py += Angles.trnsy(rotation, focusLen);
                    }

                    drawBuildingBeam(px, py);

                    if(origin != this){
                        Draw.z(122);
                        Drawf.buildBeam(origin.getX(), origin.getY(), x, y, 4);
                        Lines.stroke(2);

                        px = x + Angles.trnsx(rotation, focusLen);
                        py = y + Angles.trnsy(rotation, focusLen);
                        Lines.dashLine(px, py, plan.drawx(), plan.drawy(), 5);
                    }
                }
            }
        }
    }

    @Override
    public void controlWeapons(boolean rotateShoot) {
        super.controlWeapons(rotateShoot);
    }

    public void drawBuildingBeam(float px, float py) {

        boolean active = this.activelyBuilding();
        if (active || this.lastActive != null) {
            Draw.z(115.0F);
            BuildPlan plan = active ? this.buildPlan() : this.lastActive;
            Tile tile = Vars.world.tile(plan.x, plan.y);
            if (tile != null) {
                int size = plan.breaking ? (active ? tile.block().size : this.lastSize) : plan.block.size;
                float tx = plan.drawx();
                float ty = plan.drawy();
                Lines.stroke(1.0F, plan.breaking ? Pal.remove : Pal.accent);
                Draw.z(122.0F);
                Draw.alpha(this.buildAlpha);
                if (!active && !(tile.build instanceof ConstructBlock.ConstructBuild)) {
                    Fill.square(plan.drawx(), plan.drawy(), (float)(size * 8) / 2.0F);
                }

                Drawf.buildBeam(px, py, tx, ty, (float)(8 * size) / 2.0F);
                Fill.square(px, py, 1.8F + Mathf.absin(Time.time, 2.2F, 1.1F), this.rotation + 45.0F);
                Draw.reset();
                Draw.z(115.0F);
            }
        }
    }

    @Override
    public int classId() {
        return MeldUnits.classID(this.getClass());
    }
}
