package meld.entities.unit.abilities;

import arc.math.Mathf;
import arc.util.Tmp;
import meld.content.MeldStatusEffects;
import mindustry.Vars;
import mindustry.entities.abilities.SpawnDeathAbility;
import mindustry.gen.Unit;

//literally the same but with a status effect attached to it.  yes, that's literally it.
public class DeathBirthAbility extends SpawnDeathAbility {

    @Override
    public void death(Unit unit) {
        if (!Vars.net.client()) {
            int spawned = this.amount + Mathf.random(this.randAmount);

            for(int i = 0; i < spawned; ++i) {
                Tmp.v1.rnd(Mathf.random(this.spread));
                Unit u = this.unit.spawn(unit.team, unit.x + Tmp.v1.x, unit.y + Tmp.v1.y);
                u.rotation = this.faceOutwards ? Tmp.v1.angle() : unit.rotation + Mathf.range(5.0F);
                u.apply(MeldStatusEffects.newborn, 60);
            }
        }

    }
}

