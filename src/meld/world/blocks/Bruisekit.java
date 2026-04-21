package meld.world.blocks;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import meld.content.MeldFx;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

public class Bruisekit extends FieldPulsar{

    Seq<Building> tmpDamaged = new Seq<>();

    int targetTimer = timers++;

    public float rotateSpeed = 2f;
    public float shootCone = 10f;
    public float shootY = 6;

    public float healAmount = 20;
    public float retargetInterval = 5;

    public float recentDamageMultiplier = 0.5f, smallMultiplier = 0.25f;

    public float bruiseMulti = 4;

    public TextureRegion baseRegion, turretRegion;

    public float reload = 90;

    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name + "-base", name);
        turretRegion = Core.atlas.find(name + "-turret", name);
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{baseRegion, turretRegion};
    }

    public Bruisekit(String name) {
        super(name);
        fogRadius = 0;
    }

    public class BruisekitBuild extends PulsarBuild{
        public Building target;
        public float rotation = 90;
        public float reloadTime = 0;
        public boolean shooting = false;
        public Vec2 shootOffset = new Vec2();

        @Override
        public boolean shouldConsume() {
            return super.shouldConsume() && (target != null || Math.abs(smoothRadius - range) > 1);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            shooting = false;
            shootOffset.trns(rotation, shootY).add(x, y);

            reloadTime = Math.min(reloadTime + edelta(), reload);

            if(timer.get(targetTimer, retargetInterval)) findTarget();

            if(target != null && target.isValid()) {

                if(target.damaged()){
                    float angleTowards = angleTo(target);
                    rotation = Angles.moveToward(rotation, angleTowards, rotateSpeed);

                    if(Angles.angleDist(rotation, angleTowards) <= shootCone){


                        shooting = true;

                        if(reloadTime >= reload) {
                            reloadTime = 0;
                            Vec2 end = new Vec2(target.x, target.y);

                            MeldFx.chainLightning.at(target.x, target.y, 0, Pal.heal,
                            new MeldFx.VisualLightningHolder(){
                                @Override
                                public Vec2 start() {
                                    return shootOffset;
                                }

                                @Override
                                public Vec2 end() {
                                    return end;
                                }

                                @Override
                                public float width() {
                                    return 4;
                                }

                                @Override
                                public float segLength() {
                                    return 8;
                                }

                                @Override
                                public float arc() {
                                    return 0.2f;
                                }

                                @Override
                                public int coils() {
                                    return 2;
                                }
                            });

                            float healed = healAmount * edelta();
                            if(target.wasRecentlyDamaged()) healed *= recentDamageMultiplier;
                            if(target.block.size == 0) healed *= smallMultiplier;

                            healed *= Mathf.lerp(1, bruiseMulti, Math.min(2 - 2 * target.healthf(), 1));

                            target.heal(healed);

                            Fx.healBlockFull.at(target.x, target.y, 0, Pal.heal, target.block);
                        }

                    }
                }
                else target = null;
            }
        }

        public void findTarget(){
            target = null;
            tmpDamaged.clear();

            Units.nearbyBuildings(x, y, smoothRadius, b -> {
                if(b.team == team() && b.damaged()) tmpDamaged.add(b);
            });

            tmpDamaged.sort(b -> b.block.size * 10 + (1 - b.healthf()) + Mathf.log(10, b.block.health)/10 + b.block.priority);
            if(!tmpDamaged.isEmpty()) target = tmpDamaged.pop();
        }

        @Override
        public void draw() {
            super.draw();
            Draw.reset();
            Draw.z(Layer.blockOver);
            Draw.rect(baseRegion, x, y);
            Draw.z(Layer.turret);
            Draw.rect(turretRegion, x, y, rotation - 90);
            Draw.z(Layer.buildBeam);
            if(target != null && shooting){
                Lines.line(shootOffset.x, shootOffset.y, target.x, target.y);
            }
        }
    }
}
