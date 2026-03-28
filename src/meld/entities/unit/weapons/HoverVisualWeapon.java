package meld.type.weapons;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;

public class HoverVisualWeapon extends VisualWeapon {
    public float radius = 4.0F;
    public float phase = 50.0F;
    public float stroke = 3.0F;
    public float minStroke = 0.12F;
    public int circles = 2;
    public int sides = 4;
    public Color color;
    public float layer;
    public float layerOffset;

    public HoverVisualWeapon(){
        super();

        this.color = Color.white;
        this.mirror = false;
        this.layer = -1.0F;
        this.layerOffset = 0.0F;
        reload = 60;
    }

    @Override
    public void update(Unit unit, WeaponMount mount) {
        float target = 0;
        if(!unit.tileOn().floor().isLiquid) target = 1;
        mount.warmup = Mathf.lerpDelta(mount.warmup, target, 1/reload);
    }

    @Override
    public void draw(Unit unit, WeaponMount mount) {
        float rotation = unit.rotation - 90.0F;
        float realRecoil = Mathf.pow(mount.recoil, this.recoilPow) * this.recoil;
        float weaponRotation = rotation + (this.rotate ? mount.rotation : this.baseRotation);
        float wx = unit.x + Angles.trnsx(rotation, this.x, this.y) + Angles.trnsx(weaponRotation, 0.0F, -realRecoil);
        float wy = unit.y + Angles.trnsy(rotation, this.x, this.y) + Angles.trnsy(weaponRotation, 0.0F, -realRecoil);


        float z = Draw.z();
        if (this.layer > 0.0F) {
            Draw.z(this.layer);
        }

        Draw.z(Draw.z() + this.layerOffset);
        int len = 1;
        Draw.color(this.color);

        for(int c = 0; c < this.circles; ++c) {
            float fin = (Time.time / this.phase + (float)c / (float)this.circles) % 1.0F;
            Lines.stroke((1.0F - fin) * this.stroke + this.minStroke);

            for(int s = 0; s < len; ++s) {
                Lines.poly(wx, wy, this.sides, this.radius * fin * mount.warmup, weaponRotation);
            }
        }

        Draw.reset();
        Draw.z(z);
    }
}
