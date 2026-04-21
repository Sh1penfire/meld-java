package meld.content;

import arc.graphics.Color;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import meld.fluid.Aspect;
import meld.fluid.AspectGroup;
import mindustry.type.Liquid;

import static meld.fluid.AspectGroup.*;

public class MeldLiquids {

    public static Aspect aether, aspect, meld, fumes,
    pollutantMixture, boundAspect,
    thunderingAether, stormingAspect;

    public static ObjectFloatMap<Liquid> outletEfficiencies = new ObjectFloatMap<>();
    public static ObjectFloatMap<Liquid> outletDensities = new ObjectFloatMap<>();

    public static ObjectMap<Liquid, Liquid> outletMapping = new ObjectMap<>();
    public float outletRatio = 10;

    public static void load(){
        aether = new Aspect("aether"){{
            gas = true;
            color = Color.valueOf("cb8650");
            temperature = 0.6f;
        }};

        pollutantMixture = new Aspect("pollutant-mixture"){{
            gas = true;
            color = Color.valueOf("6a634d");
            temperature = 0.6f;
        }};

        thunderingAether = new Aspect("thundering-aether"){{
            gas = true;
            color = Color.valueOf("f99b76");
            temperature = 0.6f;
            explosiveness = 0.1f;
        }};

        aspect = new Aspect("aspect"){{
            gas = true;
            flammability = 0.35f;
            explosiveness = 1;
            color = Color.valueOf("cbdbfc");
            temperature = 0.6f;
        }};

        stormingAspect = new Aspect("storming-aspect"){{
            gas = true;
            color = Color.valueOf("aaadfd");
            temperature = 0.6f;
            flammability = 1;
            explosiveness = 2;

        }};

        boundAspect = new Aspect("bound-aspect"){{
            gas = true;
            color = Color.valueOf("d7a9ef");
            temperature = 0.6f;
        }};
        meld = new Aspect("meld"){{
            gas = true;
            color = Color.valueOf("e4aad5");
            temperature = 0.6f;
        }};

        fumes = new Aspect("fumes"){{
            gas = true;
            color = Color.valueOf("5b4739");
            temperature = 0.6f;
        }};

        put(aether, AspectGroup.aether, new AspectStats(1, 1));
        put(pollutantMixture, AspectGroup.aether, new AspectStats(0.5f, 0.5f));
        put(thunderingAether, AspectGroup.aether, new AspectStats(2, 0.5f));

        put(aspect, AspectGroup.aspect, new AspectStats(1, 1));
        put(boundAspect, AspectGroup.aspect, new AspectStats(1, 2));
        put(stormingAspect, AspectGroup.aspect, new AspectStats(2, 1));

        put(fumes, AspectGroup.fumes, new AspectStats(1, 1));
        put(pollutantMixture, AspectGroup.fumes, new AspectStats(1, 0.5f));

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
