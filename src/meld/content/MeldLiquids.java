package meld.content;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class MeldLiquids {

    public static Liquid aether, aspect, meld;

    public static void load(){
        aether = new Liquid("aether"){{
            gas = true;
            color = Color.valueOf("cb8650");
        }};

        aspect = new Liquid("aspect"){{
            gas = true;
            flammability = 1;
            explosiveness = 2;
            color = Color.valueOf("cbdbfc");
        }};

        meld = new Liquid("meld"){{
            gas = true;
            color = Color.valueOf("e4aad5");
            temperature = 0.6f;
        }};
    }
}
