package meld.world.blocks.defense;

import arc.graphics.g2d.Draw;
import meld.content.MeldStatusEffects;
import meld.world.meta.BuildingPriority;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.world.Block;
import mindustry.world.Tile;

public class FrictionPad extends Block {

    public float wearDamage = 0.1f;

    public FrictionPad(String name) {
        super(name);
        update = true;
        targetable = false;
        underBullets = true;
        hasShadow = false;
        alwaysReplace = true;
        priority = BuildingPriority.landmine;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile != null && tile.floor().isLiquid && !tile.floor().isDeep();
    }

    public class FrictionPadBuild extends Building {

        @Override
        public void unitOn(Unit unit) {
            super.unitOn(unit);

            if(unit.team != team) damageContinuous(wearDamage);

            unit.apply(MeldStatusEffects.stuck, 5);
            unit.unapply(MeldStatusEffects.boosting);
        }


        @Override
        public void draw() {
            Draw.alpha(0.35f);
            super.draw();
        }
    }
}
