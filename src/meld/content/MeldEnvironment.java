package meld.content;

import mindustry.graphics.CacheLayer;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;

public class MeldEnvironment {
    public static Block meldSwampland, meldCrystalScattered, meldCrystal, meldCrystalHard;

    public static Block debrisDeposit, carbolithDeposit, silverDeposit, resonarumDeposit;


    public static void load(){

        debrisDeposit = new StaticWall("debris-deposit"){{
            variants = 3;
            itemDrop = MeldContent.debris;
        }};

        carbolithDeposit = new StaticWall("carbolith-deposit"){{
            variants = 3;
            itemDrop = MeldContent.carbolith;
        }};

        silverDeposit = new StaticWall("silver-deposit"){{
            variants = 3;
            itemDrop = MeldContent.silver;
        }};

        resonarumDeposit = new StaticWall("resonarum-deposit"){{
            variants = 3;
            itemDrop = MeldContent.resonarum;
        }};

        meldSwampland = new Floor("meld-swampland", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
        }};

        meldCrystalScattered = new Floor("meld-crystal-scattered", 3){{
        }};

        meldCrystal = new Floor("meld-crystal-floor", 3){{
        }};

        meldCrystalHard = new Floor("meld-hard-crystal-floor", 3){{
        }};
    }
}
