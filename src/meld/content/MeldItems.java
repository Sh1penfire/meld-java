package meld.content;

import arc.graphics.Color;
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
    annealedSilver, glassMallows,
    aspectBomb,
    iampsi, quartzStrata, likestoneSediments,
    gunpowder,
    cruciblePlating, aspectPipe;

    public static void load(){

        debris = new Item("debris", Color.valueOf("e0b28d")){{
            cost = 1;
        }};
        carbolith = new Item("carbolith", Color.valueOf("95abd9")){{
            cost = 1.2f;
        }};
        silver = new Item("silver", Color.valueOf("f0f5fe")){{
            cost = 0.3f;
        }};
        clayMallows = new Item("clay-mallow", Color.valueOf("d08043")){{
            cost = 0.2f;
        }};

        electrumSheet = new Item("electrum-sheet", Color.valueOf("f4ec5d")){{
            cost = 0.2f;
        }};

        resonarum = new Item("resonarum", Color.valueOf("4bb66b"));

        dissonitre = new Item("dissonitre", Color.valueOf("e89964")){{
            cost = 0.05f;
        }};

        vitricMesh = new Item("vitric-mesh", Color.valueOf("fddfc9")){{
            cost = 0.25f;
        }};

        meldShard = new Item("meld-shard", Color.valueOf("e5aed7"));

        stonyParticulate = new Item("stony-particulate", Color.valueOf("3c4448")){{

        }};

        larvalPlating = new Item("larval-plating", Color.valueOf("946657")){{

        }};

        //Heavy Industry
        tenbris = new Item("tenbris", Color.valueOf("604343")){{

        }};

        motis = new Item("motis", Color.valueOf("c27e7a")){{

        }};

        shadesteel = new Item("shadesteel", Color.valueOf("f06060")){{

        }};

        elnarDust = new Item("elnar-dust", Color.valueOf("6cbcaa")){{

        }};

        annealedSilver = new Item("annealed-silver", Color.valueOf("5a6696")){{

        }};

        glassMallows = new Item("glass-mallow", Color.valueOf("ede0d4"));

        aspectBomb = new Item("aspect-bomb"){{
            explosiveness = 3;
            flammability = 0;
        }};

        iampsi = new Item("iampsi", Color.valueOf("b39ddd")){{

        }};

        quartzStrata = new Item("quartz-strata", Color.valueOf("f2cdfe")){{}};

        likestoneSediments = new Item("likestone-sediments", Color.valueOf("5b704c")){{}};

        gunpowder = new Item("gunpowder"){{
            hidden = true;
        }};

        aspectPipe = new Item("aspect-pipe", Color.valueOf("716a56")){{

        }};

        cruciblePlating = new Item("crucible-plating", Color.valueOf("4a322b")){{

        }};

        Seq<Item> heavyIndustry = Seq.with(tenbris, motis, shadesteel, elnarDust, annealedSilver, glassMallows, iampsi, quartzStrata, likestoneSediments, cruciblePlating, aspectPipe);
        heavyIndustry.each(i -> i.databaseTag = "heavy-industry");
        Seq<Item> arsenal = Seq.with(aspectBomb, gunpowder);
        arsenal.each(i -> i.databaseTag = "arsenal");
        Seq<Item> meldological = Seq.with(meldShard, larvalPlating, stonyParticulate);
        meldological.each(i -> i.databaseTag = "meldy");
    };
}
