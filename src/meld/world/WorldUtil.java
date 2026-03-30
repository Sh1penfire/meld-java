package meld.world;

import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;

import static mindustry.Vars.world;

public class WorldUtil {

    static Seq<Tile> tempTiles = new Seq<>();

    //Coppied from Block
    public static float sumAttribute(@Nullable Attribute attr, int x, int y){
        if(attr == null) return 0;
        Tile tile = world.tile(x, y);
        if(tile == null) return 0;
        tile.getLinkedTiles(tempTiles);
        return tempTiles.sumf(other -> other.floor().attributes.get(attr));
    }
}
