package meld.entities.unit.abilities;

import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class BeachedAbility extends Ability {
    @Override
    public void update(Unit unit) {
        super.update(unit);
        if(unit.tileOn() != null && !unit.tileOn().floor().isLiquid){
            unit.reloadMultiplier = 0;
        }
    }
}
