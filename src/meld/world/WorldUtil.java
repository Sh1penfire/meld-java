package meld.world;

import arc.func.Cons;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.world.Block;
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

    public static void nearbyTiles(float x, float y, int size, Cons<Tile> cons){
        Tmp.v1.set(size, (size - 1)/2f);
    }

    //Just to note
    //Rotation 0/0: (1, 0)
    //Rotation 90/1: (0, 1)
    //Rotation 180/2: (-1, 0)
    //Rotation 270/3: (0, -1)

    //Really hyper compact and im not sure this is the best way to do it but like
    public static int relativeTo(int x1, int y1, int x2, int y2){
        int dx = x2 - x1, dy = y2 - y1;
        int ax = Math.abs(dx), ay = Math.abs(dy);

        boolean up = ay > ax;
        int rotation = 0;
        if(up){
            rotation++;
            if(Mathf.sign(dy) == -1) rotation += 2;
            return rotation;
        }

        //Flippies!
        if(Mathf.sign(dx) == -1) rotation += 2;
        return rotation;
    }
}
