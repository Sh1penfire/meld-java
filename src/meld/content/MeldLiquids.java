package meld.content;

import arc.graphics.Color;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import meld.fluid.Aspect;
import meld.fluid.AspectGroup;
import meld.graphics.MeldPal;
import mindustry.entities.effect.ParticleEffect;
import mindustry.type.CellLiquid;
import mindustry.type.Liquid;

import static meld.fluid.AspectGroup.*;

public class MeldLiquids {

    public static Aspect aether, aspect, meld, fumes,
    pollutantMixture, boundAspect,
    thunderingAether, stormingAspect;

    public static CellLiquid
    nectar, mercury, blood, ooze, ichor;

    public static ObjectFloatMap<Liquid> outletEfficiencies = new ObjectFloatMap<>();
    public static ObjectFloatMap<Liquid> outletDensities = new ObjectFloatMap<>();

    public static ObjectMap<Liquid, Liquid> outletMapping = new ObjectMap<>();
    public float outletRatio = 10;

    public static void load(){
        aether = new Aspect("aether"){{
            gas = true;
            color = Color.valueOf("cb8650");
            lightColor = Color.valueOf("412a0c").a(0.5f);
            lightOpacity = 0.01f;
            temperature = 0.6f;
        }};

        pollutantMixture = new Aspect("pollutant-mixture"){{
            gas = true;
            temperature = 0.6f;

            color = Color.valueOf("6a634d");
            lightOpacity = 0.001f;
        }};

        thunderingAether = new Aspect("thundering-aether"){{
            gas = true;
            temperature = 0.6f;
            explosiveness = 0.1f;

            color = Color.valueOf("f35430");
            lightOpacity = 0.02f;
        }};

        aspect = new Aspect("aspect"){{
            gas = true;
            flammability = 0.35f;
            explosiveness = 1;
            temperature = 0.6f;

            color = Color.valueOf("cbdbfc");
            lightOpacity = 0.02f;
        }};

        stormingAspect = new Aspect("storming-aspect"){{
            gas = true;
            temperature = 0.6f;
            flammability = 1;
            explosiveness = 2;

            color = Color.valueOf("aaadfd");
            lightOpacity = 0.05f;
        }};

        boundAspect = new Aspect("bound-aspect"){{
            gas = true;
            color = Color.valueOf("d7a9ef");
            temperature = 0.6f;
        }};

        meld = new Aspect("meld"){{
            gas = true;
            color = Color.valueOf("e4aad5");
            lightColor = MeldPal.meldFloorGlow;
            temperature = 0.6f;
        }};

        fumes = new Aspect("fumes"){{
            gas = true;
            color = Color.valueOf("5b4739");
            temperature = 0.6f;
        }};

        nectar = new CellLiquid("nectar"){{
            viscosity = 0.95f;
            temperature = 0.15f;
            heatCapacity = 2;

            spreadTarget = MeldLiquids.mercury;
            capPuddles = false;

            maxSpread = 0.5f;
            spreadConversion = 0.85f;
            spreadDamage = 0;
            removeScaling = 0.5f;

            effect = MeldStatusEffects.refreshed;

            color = Color.valueOf("55391b");
            colorFrom = Color.valueOf("7d642c");;
            colorTo = Color.valueOf("b89f47");

        }};

        mercury = new CellLiquid("mercury"){{
            temperature = 0.2f;
            heatCapacity = 1;

            cells = 2;

            spreadTarget = MeldLiquids.ooze;
            capPuddles = false;

            maxSpread = 0.00025f;
            spreadConversion = 0;
            spreadDamage = 0;
            removeScaling = 0;

            effect = MeldStatusEffects.slippery;

            color = Color.valueOf("89828c");
            colorFrom = Color.valueOf("454545");
            colorTo = Color.valueOf("89828c");

            moveThroughBlocks = false;
            incinerable = false;

        }};

        blood = new CellLiquid("blood"){{
            explosiveness = 0.42f;
            viscosity = 1.01f;
            temperature = 153.885f;
            heatCapacity = 5;

            effect = MeldStatusEffects.infested;

            capPuddles = false;

            maxSpread = 0.1f;
            spreadConversion = 0.35f;
            spreadDamage = 0.45f;
            removeScaling = 1;

            spreadTarget = nectar;

            color = Color.valueOf("7d312f");
            colorFrom = Color.valueOf("602d2b");
            colorTo = Color.valueOf("d78b65");

            moveThroughBlocks = true;
            incinerable = false;
        }};

        ooze = new CellLiquid("ooze"){{
            explosiveness = 0.15f;
            flammability = 0.7f;
            temperature = 0;

            spreadTarget = blood;

            capPuddles = false;

            maxSpread = 0.12f;
            spreadConversion = 1;
            spreadDamage = 5;
            removeScaling = 1;

            effect = MeldStatusEffects.oozed;

            color = Color.valueOf("43201e");
            colorFrom = Color.valueOf("663931");
            colorTo = Color.valueOf("45283c");

            moveThroughBlocks = true;
            incinerable = false;

        }};

        ichor = new CellLiquid("ichor"){{
            gas = false;

            color = Color.valueOf("948a00");
            viscosity = 0.45f;
            temperature = 1.25f;
            heatCapacity = 0.85f;

            spreadTarget = null;
        }};

        put(aether, AspectGroup.aether, new AspectStats(1, 1));
        put(pollutantMixture, AspectGroup.aether, new AspectStats(0.5f, 0.5f));
        put(thunderingAether, AspectGroup.aether, new AspectStats(2, 0.5f));

        put(aspect, AspectGroup.aspect, new AspectStats(1, 1));
        put(boundAspect, AspectGroup.aspect, new AspectStats(1, 2));
        put(stormingAspect, AspectGroup.aspect, new AspectStats(2, 1));

        put(fumes, AspectGroup.fumes, new AspectStats(1, 1));
        put(pollutantMixture, AspectGroup.fumes, new AspectStats(1, 0.5f));

        put(meld, aqua, new AspectStats(1/3f, 0.25f));

        outletMapping.putAll(
                aether, aspect,
                pollutantMixture, boundAspect,
                thunderingAether, stormingAspect
        );


        aether.databaseTag = pollutantMixture.databaseTag = thunderingAether.databaseTag =
        aspect.databaseTag = boundAspect.databaseTag = stormingAspect.databaseTag = "aspect-powergen";
        meld.databaseTag = "aspect-omnipotence";
        fumes.databaseTag = "aspect-vita";

        mapOutlet(aether, aspect);
        mapOutlet(pollutantMixture, boundAspect);
        mapOutlet(thunderingAether, stormingAspect);
    }

    public static void mapOutlet(Liquid input, Liquid output){

        put(input, AspectGroup.outlet, new AspectStats(
                AspectGroup.aether.getEfficiency(input) * AspectGroup.aspect.getEfficiency(output),
                        AspectGroup.aether.getDensity(input) * AspectGroup.aspect.getDensity(output)
        ));

        outletMapping.putAll(input, output);

        outletEfficiencies.put(input, AspectGroup.aether.getEfficiency(input) * AspectGroup.aspect.getEfficiency(output));

        outletDensities.put(input, AspectGroup.aether.getDensity(input) * AspectGroup.aspect.getDensity(output));
    }
}
