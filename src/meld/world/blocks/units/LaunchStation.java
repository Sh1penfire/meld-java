package meld.world.blocks.units;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Log;
import arc.util.Tmp;
import meld.content.MeldStatusEffects;
import meld.world.blocks.SonarSpire;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.blocks.production.BeamDrill;

import static mindustry.Vars.player;

public class LaunchStation extends Block {

    public float range = 48;
    public float launchRange = -1;

    public float dragForce = 160;

    public TextureRegion flippedRegion, rangeRegion, glowRegion, baseRegion, launcherShadow;

    public float chargeRate = 1/60f/3f, chargeCost = 1;

    public float maxSize = 24;

    public float statusDuration = 30;

    public LaunchStation(String name) {
        super(name);
        update = true;
        solid = false;
        rotate = true;
        quickRotate = true;

        liquidCapacity = 500;
    }

    @Override
    public void load() {
        super.load();
        flippedRegion = Core.atlas.find(name + "-flipped");
        rangeRegion = Core.atlas.find(name + "-base-range");
        glowRegion = Core.atlas.find(name + "-glow");
        baseRegion = Core.atlas.find(name + "-base");
        launcherShadow = Core.atlas.find(name + "-launcher-shadow");
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{baseRegion, launcherShadow, region};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        float x = plan.drawx(), y = plan.drawy();
        float rot = plan.rotation * 90;

        Draw.z(Layer.blockUnder);
        Draw.rect(rangeRegion, x, y);
        Draw.z(Layer.blockUnder + 0.1f);
        Draw.rect(baseRegion, x, y);
        Draw.rect(launcherShadow, x, y, rot);

        Draw.z(Layer.block);
        Draw.rect((plan.rotation + 1) % 4 > 1 ? flippedRegion : region, x, y, rot);

        Drawf.dashLine(Pal.accent, Tmp.v1.set(x, y).x, y, Tmp.v1.add(Tmp.v2.trns(rot, statusDuration * 7)).x, Tmp.v1.y, (int) Tmp.v2.len()/Vars.tilesize);
    }


    @Override
    public void init() {
        super.init();
        if(launchRange == -1) launchRange = size/2f * Vars.tilesize + 4;
    }

    public class LaunchStationBuild extends Building{

        public float charge = 0;
        public float warmup = 0;

        @Override
        public boolean shouldConsume() {
            return super.shouldConsume() && charge < 1;
        }

        @Override
        public void update() {
            super.update();

            warmup = Mathf.lerpDelta(warmup, charge >= chargeCost ? 1 : 0, charge >= chargeCost ? 0.01f : 0.05f);

            charge = Mathf.clamp(charge + edelta() * chargeRate, 0, 1);

            if(charge >= chargeCost && warmup > 0.5f) Groups.unit.intersect(x - range, y - range, range * 2, range * 2, target -> {

                if(charge < chargeCost || !target.isValid() || !within(target, range) || target.hasEffect(MeldStatusEffects.boostingIframes) || target.hitSize() > maxSize) return;

                float prog = dst(target)/range;
                if(!target.hasEffect(MeldStatusEffects.stuck))target.impulse(Tmp.v1.trns(target.angleTo(this), Mathf.lerp(dragForce * 0.5f, dragForce, prog * prog)));

                if(within(target, launchRange)){
                    charge -= chargeCost;
                    if(!target.hasEffect(MeldStatusEffects.boosting)) target.set(this.x, this.y);
                    target.rotation = rotation * 90;
                    target.apply(MeldStatusEffects.boosting, statusDuration);
                    target.apply(MeldStatusEffects.boostingIframes, 5);
                    target.vel.trns(target.rotation, target.vel.len());
                }

            });
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            Draw.z(Layer.overlayUI);
            Drawf.dashLine(Pal.accent, Tmp.v1.set(x, y).x, y, Tmp.v1.add(Tmp.v2.trns(drawrot(), statusDuration * 7)).x, Tmp.v1.y, (int) Tmp.v2.len()/Vars.tilesize);
        }

        @Override
        public void draw() {
            Draw.z(Layer.blockUnder);
            Draw.rect(rangeRegion, x, y);
            Draw.z(Layer.blockUnder + 0.1f);
            Draw.rect(baseRegion, x, y);
            Draw.rect(launcherShadow, x, y, drawrot());

            Draw.z(Layer.block);
            Draw.rect((rotation + 1) % 4 > 1 ? flippedRegion : block.region, x, y, drawrot());

            Draw.blend(Blending.additive);
            Draw.color(Pal.accent);

            float a = (Mathf.absin(10, 0.5f) + 0.5f) * warmup;

            Draw.alpha(a);
            Draw.rect(glowRegion, x, y, drawrot());

            Draw.z(Layer.effect);
            Draw.alpha(a/2);
            Draw.rect(glowRegion, x, y, drawrot());

            Draw.blend();
        }
    }
}
