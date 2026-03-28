package meld.entities.abilities;

import arc.math.Mathf;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class SolidSpeedAbility extends Ability {


    protected float warmup;
    public float speedMultiplier = 1, warmupSpeed = 1/60f;

    public SolidSpeedAbility(){
        super();

    }
    @Override
    public void update(Unit unit) {
        super.update(unit);
        float target = 1;
        if(unit.tileOn() != null && unit.tileOn().floor().isLiquid) {
            target = 0;
            unit.speedMultiplier *= Mathf.lerp(1, speedMultiplier, warmup);
        }
        warmup = Mathf.lerpDelta(warmup, target, warmupSpeed);
    }
}
