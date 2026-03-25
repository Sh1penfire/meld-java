package meld.content;

import arc.graphics.Color;
import meld.world.blocks.AetherCluster;
import mindustry.content.Fx;
import mindustry.graphics.CacheLayer;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.BuildVisibility;

public class MeldEnvironment {

    //Ore deposits
    public static Block debrisDeposit, debrisDepositLarge, debrisNodule, carbolithDeposit, carbolithDepositDepleted, silverDeposit, resonarumDeposit, resonarumCrystal, resonarumOutcrop,

    //Marsh
    meldWall, meldCrystalWall,
    //Badlands
    pillowWall, sandstoneWall,
    //Barrens
    carbonicWall
    //Storm Planes

    ;

    public static Floor
    //Marsh
            metalWeave, meldPlates, meldHadaland, meldTrenchland, meldSwampland, meldCrystalScattered, meldCrystal, meldCrystalHard,
    //Badlands
            sandMeld, softSand, sandstone,
    //Barrens
            bedrock, bedrockMeld,
            slate, slateMeld,
            runicSlate, runicSlateMeld,
            literallyCarbonStoneFromMindustryButSlightlyDifferent, carbonicPlates,
            resonantStone;

    public static Block aetherGrowth, metalWeaveAether;

    public static Prop meldCluster;
    public static AetherCluster meldClusterLarge;

    public static void load(){


        debrisNodule = new OreBlock("debris-nodule"){{
            variants = 3;
            itemDrop = MeldItems.debris;
            needsSurface = false;
        }};

        debrisDeposit = new StaticWall("debris-deposit"){{
            variants = 0;
            itemDrop = MeldItems.debris;
        }};

        debrisDepositLarge = new TallBlock("large-debris-deposit"){{
            variants = 2;
            itemDrop = MeldItems.debris;
        }};

        carbolithDeposit = new StaticWall("carbolith-deposit"){{
            variants = 3;
            itemDrop = MeldItems.carbolith;
        }};

        carbolithDepositDepleted = new StaticWall("carbolith-deposit-depleted"){{
            variants = 3;
        }};

        silverDeposit = new StaticWall("silver-deposit"){{
            variants = 3;
            itemDrop = MeldItems.silver;
        }};

        resonarumDeposit = new SeaBush("resonarum-deposit"){{
            variants = 0;
            itemDrop = MeldItems.resonarum;

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
            itemDrop = MeldItems.resonarum;

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
            itemDrop = MeldItems.resonarum;

            emitLight = true;
            lightColor = Color.valueOf("9dcdada3");
            lightRadius = 45;
        }};

        metalWeave = new Floor("metal-weave", 3);


        meldHadaland = new Floor("meld-hadaland", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            drownTime = 90;

            status = MeldStatusEffects.drenched;
            statusDuration = 40;

            dragMultiplier = 2f;
        }};

        meldTrenchland = new Floor("meld-trenchland", 3){{
            isLiquid = true;
            drownTime = 120;

            status = MeldStatusEffects.drenched;
            statusDuration = 20;

            dragMultiplier = 1.5f;
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

        sandMeld = new Floor("sand-meld", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
        }};

        slateMeld = new Floor("slate-meld", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
        }};

        bedrockMeld = new Floor("bedrock-meld", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;

            status = MeldStatusEffects.drenched;
            statusDuration = 25;
        }};


        meldCrystal = new Floor("meld-crystal-floor", 3){{
        }};

        meldCrystalHard = new Floor("meld-hard-crystal-floor", 3){{
        }};

        bedrock = new Floor("bedrock", 3);
        slate = new Floor("slate", 0);
        literallyCarbonStoneFromMindustryButSlightlyDifferent = new Floor("carbonic-stone", 4);
        carbonicPlates = new Floor("carbonic-plates", 3);

        runicSlate = new SteamVent("runic-slate"){{
            variants = 0;
            blendGroup = parent = slate;
            allowCorePlacement = true;
            effect = Fx.none;

        }};
        runicSlateMeld = new SteamVent("runic-slate-meld"){{
            isLiquid = true;
            variants = 0;
            blendGroup = parent = slateMeld;
            allowCorePlacement = true;
            effect = Fx.none;

            cacheLayer = CacheLayer.water;
        }};

        carbonicWall = new StaticWall("carbonic-wall"){{
            variants = 2;
        }};

        softSand = new Floor("soft-sand", 3);
        sandstone = new Floor("hard-sand", 3);

        meldWall = new StaticWall("meld-wall"){{
            variants = 3;
        }};

        meldCrystalWall = new StaticWall("meld-crystal-wall"){{
            variants = 3;
        }};

        pillowWall = new StaticWall("pillow-wall"){{
            variants = 2;
        }};

        sandstoneWall = new StaticWall("sandstone-wall"){{
            variants = 2;
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

        meldCluster = new WobbleProp("meld-cluster-small"){{
            requirements(Category.effect, ItemStack.with(MeldItems.debris, 40));

            buildVisibility = BuildVisibility.sandboxOnly;

            instantDeconstruct = false;
            buildTime = 10;
            variants = 2;
        }};

        meldClusterLarge = new AetherCluster("meld-cluster-large"){{
            requirements(Category.effect, ItemStack.with(MeldItems.debris, 150));
            size = 3;

            buildVisibility = BuildVisibility.sandboxOnly;

        }};
    }
}
