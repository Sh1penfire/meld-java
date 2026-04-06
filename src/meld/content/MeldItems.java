package meld.content;

import arc.struct.Seq;
import mindustry.type.Item;

public class MeldItems {
    public static Item
    debris, carbolith, silver, resonarum, clayMallows,

            meldShard;

    //Items that meld mostly uses
    public static Item stonyParticulate, larvalPlating;

    //Heavy industry content
    public static Item
    tenbris, shadesteel, elnarDust, annealedSilver, glassMallows, cruciblePlating, aspectPipe;

    public static void load(){

        debris = new Item("debris"){{
            cost = 0.5f;
        }};
        carbolith = new Item("carbolith"){{
            cost = 0.8f;
        }};
        silver = new Item("silver"){{
            cost = 0.3f;
        }};
        clayMallows = new Item("clay-mallow"){{
            cost = 0.2f;
        }};

        resonarum = new Item("resonarum");

        stonyParticulate = new Item("stony-particulate"){{

        }};

        larvalPlating = new Item("larval-plating"){{

        }};

        //Heavy Industry
        tenbris = new Item("tenbris"){{

        }};

        shadesteel = new Item("shadesteel"){{

        }};

        elnarDust = new Item("elnar-dust"){{

        }};

        annealedSilver = new Item("annealed-silver"){{

        }};

        glassMallows = new Item("glass-mallow");

        aspectPipe = new Item("aspect-pipe"){{

        }};

        cruciblePlating = new Item("crucible-plating"){{

        }};

        Seq<Item> heavyIndustry = Seq.with(tenbris, shadesteel, elnarDust, annealedSilver, glassMallows, cruciblePlating, aspectPipe);
        heavyIndustry.each(i -> i.databaseTag = "heavy-industry");
    };
}
