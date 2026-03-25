package meld.unit.weapons;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;

public class ShadowVisualWeapon extends VisualWeapon{
    public float shadowElevation, shadowElevationTo;

    public ShadowVisualWeapon(){
        super();
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
        super.draw(unit, mount);
        if(!Mathf.zero(mount.warmup)){
            float e = unit.type.shadowElevation;

            float z = Draw.z();
            Draw.z(z + layerOffset);
            unit.type.shadowElevation = Mathf.lerp(shadowElevation, shadowElevationTo, mount.warmup);
            unit.type.drawShadow(unit);

            unit.type.shadowElevation = e;
            Draw.z(z);
        }
    }
}

