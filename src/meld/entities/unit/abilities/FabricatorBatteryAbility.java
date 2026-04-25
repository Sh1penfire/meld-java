package meld.entities.unit.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import meld.content.MeldFx;
import meld.graphics.MeldRegions;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

public class FabricatorBatteryAbility extends Ability {

    public Interp chargeInterp = Interp.pow2;
    public float lingerTime = 0;
    public float visualsWarmup = 0;

    public int stage = 0;
    public int stageMax = 0;

    protected float charge = 0;
    protected float zapTimer = 0;
    protected boolean enabled = true;

    float x = 12, y = 0;

    public float chargeCap = 60 * 60;

    public float speedMulti = 5;
    public float drainSpeed = 8;

    public float minCharge = 60;

    Color[] colors = new Color[]{Color.red, Color.orange, Color.blue, Pal.accent};

    @Override
    public void update(Unit unit) {
        super.update(unit);
        boolean active = unit.activelyBuilding();
        boolean visualActive = unit.vel.len() > 1f && !active && lingerTime <= 0;

        if(!visualActive && visualsWarmup < 0.1f) stageMax = stage;

        visualsWarmup = Mathf.slerpDelta(visualsWarmup, visualActive ? 0 : 1, 0.08f);
        lingerTime -= Time.delta;

        int toStage = Mathf.floor(charge/chargeCap * 4);
        if(toStage != stage){
            if(toStage > stageMax){
                Fx.coreExplosion.at(unit.x, unit.y);
                lingerTime = 240;
            }
            stage = toStage;
            stageMax = stage;
        }

        if(active){
            if(enabled){
                charge -= Time.delta * drainSpeed;
                unit.buildSpeedMultiplier *= speedMulti;
                zapTimer += Time.delta;
                if(zapTimer > 12){
                    zapTimer %= 12;
                    Vec2 targ = new Vec2();
                    if(unit.buildPlan().build() != null) {
                        targ.set(unit.buildPlan().build().x, unit.buildPlan().build().y);
                    }
                    else {
                        Tmp.v1.trns(Mathf.random(360), unit.type.hitSize + Mathf.random(0, 12)).add(unit);
                        targ.set(Tmp.v1);
                    }

                    MeldFx.chainLightning.at(unit.x, unit.y, 0, Pal.accent, new MeldFx.VisualLightningHolder() {
                        @Override
                        public Position start() {
                            return unit;
                        }

                        @Override
                        public Position end() {
                            return targ;
                        }

                        @Override
                        public float width() {
                            return 2;
                        }

                        @Override
                        public float segLength() {
                            return 7;
                        }

                        @Override
                        public float arc() {
                            return 0.2f;
                        }

                        @Override
                        public int coils() {
                            return 3;
                        }
                    });
                }
                Sounds.shootArc.at(unit.x, unit.y, Mathf.range(0.8f, 1.2f));
            }
        }
        else charge += Time.delta;

        if(charge >= chargeCap){
            unit.healthMultiplier *= 2;
            unit.speedMultiplier *= 1.5f;
        }


        charge = Mathf.clamp(charge, 0, chargeCap);
        if(charge == 0) enabled = false;
        if(charge >= minCharge) enabled = true;
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);
        float dx = x + unit.x, dy = y + unit.y;

        float fin = charge/chargeCap;

        float rad = 4;
        float fullRad = 4;
        float fract = 0.75f;

        Draw.color(Color.darkGray);
        Draw.alpha(visualsWarmup);
        Lines.stroke(3.5f);
        Lines.arc(dx, dy, fullRad, fract);
        Draw.color(Color.gray);
        Draw.alpha(visualsWarmup);
        Lines.stroke(1.5f);
        Lines.arc(dx, dy, fullRad, fract);

        for(int i = 0; i < colors.length; i++){
            Draw.color(colors[i]);
            Draw.alpha(visualsWarmup);

            float finp = Mathf.clamp(fin * colors.length - i, 0, 1);
            finp = chargeInterp.apply(finp);

            if(finp < 0.02f) continue;

            Lines.stroke(1.5f);
            Lines.arc(dx, dy, rad, Mathf.lerp(0.01f, fract, finp));
        }

        Draw.color();
        Draw.alpha(visualsWarmup);
        Draw.rect(MeldRegions.chargeRegions[stage], dx - x * 2, dy);
    }
}
