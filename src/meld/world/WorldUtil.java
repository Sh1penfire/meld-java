package meld.world;

import arc.func.Cons;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Tmp;
import meld.world.blocks.LiquidUtil;
import meld.world.blocks.production.GrindingQuary;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;
import mindustry.world.modules.ItemModule;

import static mindustry.Vars.world;

public class WorldUtil {

    //Really useful for totalQuarry
    static ItemModule module = new ItemModule();

    static ObjectFloatMap<Item> speeds = new ObjectFloatMap<>();

    static Seq<Tile> tempTiles = new Seq<>();

    public static ItemModule totalQuarry(int x1, int y1, int x2, int y2){
        module.clear();

        for(int y = y1; y < y2; y++){
            for(int x = x1; x < x2; x++){
                Tile tile = world.tile(x, y);
                if(tile == null) continue;

                GrindingQuary.GrinderEntry entry = GrindingQuary.grinderMap.get(tile.floor());

                while (entry != null){
                    for (ItemStack itemStack : entry.output) {
                        module.add(itemStack.item, itemStack.amount);
                    }
                    entry = GrindingQuary.grinderMap.get(entry.floor);
                }
            }
        }
        return module;
    }

    public static ItemModule totalQuarry(float x, float y, Block block, boolean blockable){
        module.clear();

        Tile startTile = world.tileWorld(x, y);
        if(startTile == null) return module;
        startTile.getLinkedTilesAs(block, (tile) -> {
            if(tile == null || tile.solid() && blockable && tile.block() != block) return;

            GrindingQuary.GrinderEntry entry = GrindingQuary.grinderMap.get(tile.floor());

            while (entry != null){
                for (ItemStack itemStack : entry.output) {
                    module.add(itemStack.item, itemStack.amount);
                }
                entry = GrindingQuary.grinderMap.get(entry.floor);
            }
        });

        return module;
    }
    public static ObjectFloatMap<Item> quarrySpeed(float x, float y, GrindingQuary block){
        speeds.clear();

        Tile startTile = world.tileWorld(x, y);
        if(startTile == null) return speeds;
        startTile.getLinkedTilesAs(block, (tile) -> {
            if(tile == null) return;
            Item item = tile.overlay().itemDrop;
            if(item != null) speeds.put(item, speeds.get(item, 0) + block.drillSpeedOverlay(tile));

            item = tile.floor().itemDrop;
            if(item != null) speeds.put(item, speeds.get(item, 0) + block.drillSpeedFloor(tile));
        });

        return speeds;
    }

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
