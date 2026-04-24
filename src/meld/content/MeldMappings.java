package meld.content;

import meld.world.blocks.production.GrindingQuary;
import meld.world.blocks.production.GrindingQuary.GrinderEntry;
import mindustry.type.ItemStack;

import static meld.content.MeldEnvironment.*;
import static mindustry.type.ItemStack.with;

public class MeldMappings {
    public static void load(){
        GrindingQuary.grinderMap.putAll(
                redSand, new GrinderEntry(180, redSilt, with(MeldItems.clayMallows, 20)),
                dissonantFragments, new GrinderEntry(360, dissonantShale, with(MeldItems.clayMallows, 12, MeldItems.glassMallows, 25, MeldItems.dissonitre, 50)),
                dissonantShaleScorched, new GrinderEntry(130, dissonantShale, with(MeldItems.clayMallows, 4, MeldItems.glassMallows, 15, MeldItems.dissonitre, 5)),
                dissonantShaleStruck, new GrinderEntry(60, dissonantShale, with(MeldItems.clayMallows, 8, MeldItems.glassMallows, 12, MeldItems.dissonitre, 3)),
                dissonantShale, new GrinderEntry(240, redSilt, with(MeldItems.clayMallows, 2, MeldItems.dissonitre, 1)),

                likesand, new GrinderEntry(60, likestone, with(MeldItems.likestoneSediments, 20)),
                likestone, new GrinderEntry(120, likesalt, with(MeldItems.likestoneSediments, 10)),

                meldCrystalFloor, new GrinderEntry(720, meldCrystalScattered, with(MeldItems.meldShard, 10))
        );
    }
}
