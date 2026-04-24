package meld.entities.unit.abilities;

import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import meld.content.MeldFx;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

public class FabricatorBatteryAbility extends Ability {
    protected float charge = 0;
    protected float zapTimer = 0;
    protected boolean enabled = true;

    public float chargeCap = 60 * 60;

    public float speedMulti = 5;
    public float drainSpeed = 8;

    public float minCharge = 60;

    @Override
    public void update(Unit unit) {
        super.update(unit);

        if(unit.activelyBuilding()){
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
        Lines.arc(unit.x, unit.y, Vars.tilesize * 3, charge/chargeCap);
    }
}
