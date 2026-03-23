package meld.content;

import arc.graphics.Color;
import mindustry.graphics.CacheLayer;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;

public class MeldEnvironment {

    //Ore deposits
    public static Block debrisDeposit, debrisDepositLarge, debrisNodule, carbolithDeposit, carbolithDepositDepleted, silverDeposit, resonarumDeposit, resonarumCrystal, resonarumOutcrop,

    //Marsh
    metalWeave, meldPlates, meldSwampland, meldTrenchland, meldCrystalScattered, meldCrystal, meldCrystalHard,
    //Badlands
    softSand, sandstone, pillowWall, sandstoneWall,
    //Barrens
    carbonicPlates, carbonicWall,
    //Storm Planes
    resonantStone
    ;

    public static Block aetherGrowth, metalWeaveAether;

    public static void load(){


        debrisNodule = new OreBlock("debris-nodule"){{
            variants = 3;
            itemDrop = MeldContent.debris;
            needsSurface = false;
        }};

        debrisDeposit = new StaticWall("debris-deposit"){{
            variants = 0;
            itemDrop = MeldContent.debris;
        }};

        debrisDepositLarge = new TallBlock("large-debris-deposit"){{
            variants = 2;
            itemDrop = MeldContent.debris;
        }};

        carbolithDeposit = new StaticWall("carbolith-deposit"){{
            variants = 3;
            itemDrop = MeldContent.carbolith;
        }};

        carbolithDepositDepleted = new StaticWall("carbolith-deposit-depleted"){{
            variants = 3;
        }};

        silverDeposit = new StaticWall("silver-deposit"){{
            variants = 3;
            itemDrop = MeldContent.silver;
        }};

        resonarumDeposit = new SeaBush("resonarum-deposit"){{
            variants = 0;
            itemDrop = MeldContent.resonarum;

            solid = true;
            breakable = false;
            alwaysReplace = false;

            lobesMin = 2;
            lobesMax = 4;
            sclMin = 60;
            sclMax = 120;

            emitLight = true;
            lightColor = Color.valueOf("9dcdad1b");
            lightRadius = 35;
        }};

        resonarumOutcrop = new SeaBush("resonarum-outcrop"){{
            variants = 0;
            itemDrop = MeldContent.resonarum;

            solid = true;
            breakable = false;
            alwaysReplace = false;

            lobesMin = 3;
            lobesMax = 4;
            sclMin = 85;
            sclMax = 135;

            emitLight = true;
            lightColor = Color.valueOf("9dcdad1b");
            lightRadius = 45;
        }};

        resonarumCrystal = new TallBlock("resonarum-crystal"){{
            variants = 0;
            itemDrop = MeldContent.resonarum;

            emitLight = true;
            lightColor = Color.valueOf("9dcdada3");
            lightRadius = 45;
        }};

        metalWeave = new Floor("metal-weave", 3);

        meldTrenchland = new Floor("meld-trenchland", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
        }};

        meldSwampland = new Floor("meld-swampland", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
        }};

        meldPlates = new Floor("meld-plates", 2){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
        }};

        meldCrystalScattered = new Floor("meld-crystal-scattered", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
        }};

        meldCrystal = new Floor("meld-crystal-floor", 3){{
        }};

        meldCrystalHard = new Floor("meld-hard-crystal-floor", 3){{
        }};

        carbonicPlates = new Floor("carbonic-plates", 3);
        carbonicWall = new StaticWall("carbonic-wall"){{
            variants = 3;
        }};

        softSand = new Floor("soft-sand", 3);
        sandstone = new Floor("sandstone", 3);

        pillowWall = new StaticWall("pillow-wall"){{
            variants = 3;
        }};

        sandstoneWall = new StaticWall("sandstone-wall"){{
            variants = 3;
        }};

        resonantStone = new Floor("resonant-stone", 3);

        aetherGrowth = new SteamVent("aether-growth"){{
            variants = 0;
            attributes.set(MeldContent.aetherAttr, 1);
            blendGroup = parent = softSand;
        }};

        metalWeaveAether = new SteamVent("metal-weave-aether"){{
            variants = 0;
            attributes.set(MeldContent.aetherAttr, 1);
            blendGroup = parent = metalWeave;
        }};
    }
}
