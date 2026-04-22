package meld.content;

import meld.world.blocks.production.GrindingQuary;
import meld.world.blocks.production.GrindingQuary.GrinderEntry;
import mindustry.type.ItemStack;

import static meld.content.MeldEnvironment.*;
import static mindustry.type.ItemStack.with;

public class MeldMappings {
    public static void load(){
        GrindingQuary.grinderMap.putAll(
                redSand, new GrinderEntry(60, redSilt, with(MeldItems.clayMallows, 20)),

                likesand, new GrinderEntry(60, likestone, with(MeldItems.likestoneSediments, 20)),
                likestone, new GrinderEntry(120, likesalt, with(MeldItems.likestoneSediments, 10)),

                meldCrystalFloor, new GrinderEntry(180, meldCrystalScattered, with(MeldItems.meldShard, 10))
        );
    }
}
