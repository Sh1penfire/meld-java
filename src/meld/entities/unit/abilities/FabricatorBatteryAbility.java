package meld.entities.unit.abilities;

import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class FabricatorBatteryAbility extends Ability {
    protected float charge = 0;
    protected boolean enabled = true;

    public float chargeCap = 60 * 60;

    public float speedMulti = 5;
    public float drainSpeed = 8;

    public float minCharge = 60;

    @Override
    public void update(Unit unit) {
        super.update(unit);

        if(unit.activelyBuilding() && enabled){
            charge -= Time.delta * drainSpeed;
            unit.buildSpeedMultiplier *= speedMulti;
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
