package meld.content;

import mindustry.type.Item;

public class MeldItems {
    public static Item
    debris, carbolith, silver, resonarum,

            meldShard;

    public static void load(){

        debris = new Item("debris"){{
            cost = 0.1f;
        }};
        carbolith = new Item("carbolith"){{
            cost = 0.5f;
        }};
        silver = new Item("silver"){{
            cost = 0.4f;
        }};
        resonarum = new Item("resonarum");
    };
}
