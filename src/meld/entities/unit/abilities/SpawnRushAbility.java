package meld.entities.unit.abilities;

import meld.content.MeldStatusEffects;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class SpawnRushAbility extends Ability {

    public float duration = 240;

    @Override
    public void created(Unit unit) {
        super.created(unit);
        unit.apply(MeldStatusEffects.rush, duration);
    }
}
