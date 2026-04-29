package meld.world.blocks.defense;

import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Door;

public class LakeRim extends Door {
    public LakeRim(String name) {
        super(name);
    }

    //Placeable on tiles which are nearby liquids, but aren't liquids themselves
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if(tile == null || tile.floor().isLiquid) return false;

        for (Point2 offset : Geometry.d8) {
            Tile nearby = tile.nearby(offset.x, offset.y);
            if(nearby.floor().isLiquid) return true;
        }
        return false;


    }

    public class LakeRimBuild extends DoorBuild{

        @Override
        public boolean checkSolid(){
            return !open;
        }
    }
}
