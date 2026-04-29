package meld.content;

import arc.struct.Seq;
import meld.world.blocks.production.GrindingQuary;
import meld.world.blocks.production.GrindingQuary.GrinderEntry;
import mindustry.content.Planets;
import mindustry.content.UnitTypes;
import mindustry.type.ItemStack;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;

import static meld.content.MeldEnvironment.*;
import static meld.content.MeldLiquids.*;

import static mindustry.type.ItemStack.with;

//Handles mappings for content which depend on eachother so the main classes are not filled with hjbujgjmhbn bhjg
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

        GrindingQuary.alwaysDump.addAll(
                MeldItems.dissonitre, MeldItems.clayMallows, MeldItems.glassMallows, MeldItems.likestoneSediments, MeldItems.meldShard
        );

        nectar.canStayOn.addAll(mercury, ichor, ooze);
        mercury.canStayOn.addAll(mercury, ichor, ooze, blood);
        blood.canStayOn.addAll(nectar, ichor, ooze);
        ooze.canStayOn.addAll(meld, nectar, ichor, ooze);
        ichor.canStayOn.addAll(nectar, ichor);

    }

    public static void loadAfter(){
        //SCREW YOU SHADOW THE HEDGE HOG
        //IS THAT AR EFRENCE TO HEDGE HEHJEYSON MEWLD REAL- help
        UnitTypes.alpha.shownPlanets.add(Planets.serpulo);
        UnitTypes.alpha.shownPlanets.remove(MeldPlanets.ikaru);

        //I CAST THEEEEEEEEEEEEEEEEEEEEEEEEEEE
        //HAHAHAHAHAAAAAAAAAAAAAAAAAAAAAAHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        UnitTypes.alpha.databaseTabs.clear();
        UnitTypes.alpha.postInit();

        //TODO: Move to separate class?
        Seq<StatusEffect> bioImmunities = Seq.with(MeldStatusEffects.aspectBurn);
        Seq<StatusEffect> armorImmunities = Seq.with(MeldStatusEffects.lacerated, MeldStatusEffects.impaled);
        Seq<StatusEffect> mechanicalImmunities = Seq.with(MeldStatusEffects.lacerated, MeldStatusEffects.impaled);

        Seq<UnitType> bio = Seq.with(
                MeldUnits.blob, MeldUnits.craig, MeldUnits.braig,
                MeldUnits.jilla, MeldUnits.scorcher
        );
        Seq<UnitType> armor = Seq.with(
                MeldUnits.afraig, MeldUnits.globkin,
                MeldUnits.kathid
        );
        Seq<UnitType> mechanical = Seq.with(
                MeldUnits.bulbhead, MeldUnits.shark
        );

        bio.each(b -> b.immunities.addAll(bioImmunities));
        armor.each(b -> b.immunities.addAll(armorImmunities));
        mechanical.each(b -> b.immunities.addAll(mechanicalImmunities));
    }
}
