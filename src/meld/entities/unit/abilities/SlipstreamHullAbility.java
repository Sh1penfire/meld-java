package meld.entities.unit.abilities;

import arc.math.Mathf;
import arc.util.Tmp;
import meld.content.MeldFx;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.world.Tile;

public class SlipstreamHullAbility extends Ability {
    @Override
    public void update(Unit unit) {
        super.update(unit);
        if(unit.tileOn() != null) {
            Tile tile = unit.tileOn();
            unit.dragMultiplier /= Math.max(tile.floor().dragMultiplier, 1);
            unit.speedMultiplier /= Math.max(tile.floor().speedMultiplier, 1);

            //TODO: Better wakes for naval units
            /*
            if(tile.floor().isLiquid && unit.vel.len() > 4 && Mathf.chance(unit.vel.len()/5f)){
                Tmp.v1.trns(unit.rotation, unit.hitSize).add(unit.x, unit.y);
                MeldFx.waterShear.at(Tmp.v1.x, Tmp.v1.y, unit.rotation, tile.floor().mapColor, unit);
                MeldFx.waterShearFollow.at(Tmp.v1.x, Tmp.v1.y, unit.rotation, tile.floor().mapColor, unit);
            }

             */
        }
    }
}
