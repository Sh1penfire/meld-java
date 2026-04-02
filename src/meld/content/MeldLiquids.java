package meld.content;

import arc.graphics.Color;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.type.Liquid;

public class MeldLiquids {

    public static Liquid aether, aspect, meld, fumes,
    pollutantMixture, boundAspect;

    public static ObjectFloatMap<Liquid> aetherEfficiencies = new ObjectFloatMap<>();
    public static ObjectFloatMap<Liquid> aetherDensities = new ObjectFloatMap<>();

    public static ObjectFloatMap<Liquid> aspectEfficiencies = new ObjectFloatMap<>();
    public static ObjectFloatMap<Liquid> aspectDensities = new ObjectFloatMap<>();

    public static ObjectFloatMap<Liquid> outletEfficiencies = new ObjectFloatMap<>();
    public static ObjectFloatMap<Liquid> outletDensities = new ObjectFloatMap<>();

    public static ObjectMap<Liquid, Liquid> outletMapping = new ObjectMap<>();
    public float outletRatio = 10;

    public static void load(){
        aether = new Liquid("aether"){{
            gas = true;
            color = Color.valueOf("cb8650");
            temperature = 0.6f;
        }};

        aspect = new Liquid("aspect"){{
            gas = true;
            flammability = 1;
            explosiveness = 2;
            color = Color.valueOf("cbdbfc");
            temperature = 0.6f;
        }};

        meld = new Liquid("meld"){{
            gas = true;
            color = Color.valueOf("e4aad5");
            temperature = 0.6f;
        }};

        fumes = new Liquid("fumes"){{
            gas = true;
            color = Color.valueOf("5b4739");
            temperature = 0.6f;
        }};

        pollutantMixture = new Liquid("pollutant-mixture"){{
            gas = true;
            color = Color.valueOf("6a634d");
            temperature = 0.6f;
        }};

        boundAspect = new Liquid("bound-aspect"){{
            gas = true;
            color = Color.valueOf("d7a9ef");
            temperature = 0.6f;
        }};

        aetherEfficiencies.put(aether, 1);
        aetherEfficiencies.put(pollutantMixture, 1);

        aetherDensities.put(aether, 1);
        aetherDensities.put(pollutantMixture, 1/5f);

        aspectEfficiencies.put(aspect, 1);
        aspectEfficiencies.put(boundAspect, 1);

        aspectDensities.put(aspect, 1);
        aspectDensities.put(boundAspect, 2.5f);

        outletMapping.putAll(
                aether, aspect,
                pollutantMixture, boundAspect
        );

        Seq<Liquid> outletLiquids = Seq.with(aether, pollutantMixture);

        outletLiquids.each(liquid -> {
            Liquid aspecti = outletMapping.get(liquid);
            outletEfficiencies.put(liquid, aetherEfficiencies.get(liquid, 1) * aspectEfficiencies.get(aspecti, 1));
            outletDensities.put(liquid, aetherDensities.get(liquid, 1) * aspectDensities.get(aspecti, 1));
        });

    }
}
