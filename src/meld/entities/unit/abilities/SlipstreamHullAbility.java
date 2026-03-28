package meld.entities.abilities;

import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class SlipstreamHullAbility extends Ability {
    @Override
    public void update(Unit unit) {
        super.update(unit);
        if(unit.tileOn() != null) {
            unit.dragMultiplier /= Math.max(unit.tileOn().floor().dragMultiplier, 1);
        }
    }
}
