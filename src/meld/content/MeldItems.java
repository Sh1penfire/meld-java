package meld.content;

import arc.struct.Seq;
import mindustry.type.Item;

public class MeldItems {
    public static Item
    debris, carbolith,

    //The badlands hall of shame
    silver, clayMallows, electrumSheet,

    //The storm planes hall of shame...
    resonarum, dissonitre, vitricMesh,

            meldShard;

    //ItemLogic that meld mostly uses
    public static Item stonyParticulate, larvalPlating;

    //Heavy industry content
    public static Item
    tenbris, motis, shadesteel, elnarDust,
    annealedSilver, glassMallows, iampsi, quartzStrata, likestoneSediments,
    cruciblePlating, aspectPipe;

    public static void load(){

        debris = new Item("debris"){{
            cost = 1;
        }};
        carbolith = new Item("carbolith"){{
            cost = 1.2f;
        }};
        silver = new Item("silver"){{
            cost = 0.3f;
        }};
        clayMallows = new Item("clay-mallow"){{
            cost = 0.2f;
        }};

        electrumSheet = new Item("electrum-sheet"){{
            cost = 0.2f;
        }};

        resonarum = new Item("resonarum");

        dissonitre = new Item("dissonitre"){{
            cost = 0.05f;
        }};

        vitricMesh = new Item("vitric-mesh"){{
            cost = 0.25f;
        }};

        meldShard = new Item("meld-shard");

        stonyParticulate = new Item("stony-particulate"){{

        }};

        larvalPlating = new Item("larval-plating"){{

        }};

        //Heavy Industry
        tenbris = new Item("tenbris"){{

        }};

        motis = new Item("motis"){{

        }};

        shadesteel = new Item("shadesteel"){{

        }};

        elnarDust = new Item("elnar-dust"){{

        }};

        annealedSilver = new Item("annealed-silver"){{

        }};

        glassMallows = new Item("glass-mallow");

        iampsi = new Item("iampsi"){{

        }};

        quartzStrata = new Item("quartz-strata"){{}};

        likestoneSediments = new Item("likestone-sediments"){{}};

        aspectPipe = new Item("aspect-pipe"){{

        }};

        cruciblePlating = new Item("crucible-plating"){{

        }};

        Seq<Item> heavyIndustry = Seq.with(tenbris, motis, shadesteel, elnarDust, annealedSilver, glassMallows, iampsi, quartzStrata, likestoneSediments, cruciblePlating, aspectPipe);
        heavyIndustry.each(i -> i.databaseTag = "heavy-industry");
    };
}
