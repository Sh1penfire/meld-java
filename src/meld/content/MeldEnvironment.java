package meld.content;

import arc.graphics.Color;
import arc.struct.Seq;
import meld.graphics.MeldPal;
import meld.world.blocks.AetherCluster;
import meld.world.blocks.defense.TreeWall;
import meld.world.blocks.env.ModdedOreBlock;
import meld.world.blocks.env.SupportPillar;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.graphics.CacheLayer;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;

import static meld.graphics.MeldPal.*;

public class MeldEnvironment {

    //Ore deposits & overlays
    public static Block

    //Marsh pt1
        debrisDeposit, debrisDepositLarge, debrisNodule,
        metalPebbles, metalPebblesDense, metalSheeting,
    //Barrens
        carbolithDeposit, carbolithDepositDepleted, tenbrisMix, tenbrisRidges, elnarSilt, embeddedElnar, embeddedElnarOverflowing,
    //Badlands
        silverDeposit, silverDepositDepleted, electrumDeposit, iampsiSpecks, quartzFlakes,
    //Storm Planes
        resonarumDeposit, resonarumCrystal, resonarumOutcrop,

    //Marsh
    meldWall, meldCrystalWall,
    metalMeshWall, metalMeshWallMeld, metalAetherWall,
    //Badlands
    pillowWall, sandstoneWall,
    softDune, sandstonePillar,
    mallowWall, crackstoneWall,
    likesandWall, likestoneWall, likesaltWall,

    polishedSandstoneWall,
    //Barrens
    carbonicWall, earthenWall
    //Storm Planes

    ;

    public static Floor
            metalWeave, metalWeaveHole, metalWeaveGlow,
    //Badlands
        polishedSandstoneTile,

        sandMeld, softSand, sandstone,
        redsandMeld, redSand, redSilt, aspectSoil, redSandWeave, crackstone,
        likesand, likestone, likesalt,
        goldSand, goldSlurry,

    //Barrens
        bedrock, bedrockMeld,
        slate, slateMeld,
        runicSlate, runicSlateMeld,
        literallyCarbonStoneFromMindustryButSlightlyDifferent, carbonicPlates, carbonicVent,
        earthenStone, mixtureStone,
    //Storm Planes
    dissonantShale, dissonantShaleStruck, dissonantShaleScorched, dissonantFragments,
        resonantStone,
    //Marsh
    meldPlates, meldHadaland, meldTrenchland, meldSwampland, meldCrystalScattered, meldCrystalFloor, meldCrystalHardFloor;

    public static SteamVent aetherGrowth, metalWeaveAether;

    public static Prop meldCluster, meldPools, meldProtrusion, meldMetalStick, meldCrystal, mixedCarbonicBoulder, iampsiGemstone, quartzSpikes, vitricWeave, mallowCluster, pillowGlintCluster, earthenResonarumCluster;
    public static TallBlock meldCrystalLarge, meldSupportFrame;
    public static AetherCluster meldClusterLarge, dissonitreCluster;

    public static void load(){

        metalPebbles = new OverlayFloor("metal-pebbles"){{
            variants = 3;
            needsSurface = false;
        }};
        metalSheeting = new OverlayFloor("metal-sheeting"){{
            variants = 3;
            needsSurface = false;
        }};
        metalPebblesDense = new OverlayFloor("metal-pebbles-dense"){{
            variants = 3;
            needsSurface = false;
        }};

        debrisNodule = new ModdedOreBlock("debris-nodule", MeldItems.debris){{
            variants = 3;
            mapColor = Color.valueOf("f4a462");
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

        tenbrisMix = new StaticWall("tenbris-mix"){{
            variants = 2;
            itemDrop = MeldItems.tenbris;
        }};

        tenbrisRidges = new StaticWall("tenbris-ridges"){{
            variants = 2;
            itemDrop = MeldItems.tenbris;
        }};

        embeddedElnar = new StaticWall("embedded-elnar"){{
            variants = 2;
            itemDrop = MeldItems.elnarDust;
        }};

        elnarSilt = new ModdedOreBlock("elnar-silt", MeldItems.elnarDust){{
            variants = 4;
            mapColor = Color.valueOf("96ed45");
            setDefaults = false;
            needsSurface = false;
        }};

        embeddedElnarOverflowing = new StaticWall("embedded-elnar-overflowing"){{
            variants = 2;
            itemDrop = MeldItems.elnarDust;
        }};

        carbolithDepositDepleted = new StaticWall("carbolith-deposit-depleted"){{
            variants = 3;
        }};

        silverDeposit = new StaticWall("silver-deposit"){{
            variants = 3;
            itemDrop = MeldItems.silver;
        }};

        silverDepositDepleted = new StaticWall("silver-deposit-depleted"){{
            variants = 3;
        }};

        electrumDeposit = new StaticWall("electrum-deposit"){{
            variants = 3;
            itemDrop = MeldItems.electrumSheet;
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

        //Barrens
        literallyCarbonStoneFromMindustryButSlightlyDifferent = new Floor("carbonic-stone", 4);

        bedrock = new Floor("bedrock", 3);
        slate = new Floor("slate", 0);

        earthenStone = new Floor("earthen-stone", 4);
        carbonicPlates = new Floor("carbonic-plates", 3);
        slate.blendGroup = bedrock;
        mixtureStone = new Floor("mixture-stone", 3);

        metalWeave = new Floor("metal-weave", 3);
        metalWeaveHole= new Floor("metal-weave-hole", 0);
        metalWeaveGlow = new Floor("metal-weave-glow", 0){{
            emitLight = true;
            lightRadius = 16;
            lightColor = Color.orange.cpy().a(0.35f);
        }};

        metalWeaveHole.blendGroup = metalWeaveGlow.blendGroup = metalWeave;

        meldHadaland = new Floor("meld-hadaland", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            drownTime = 90;

            status = MeldStatusEffects.drenched;
            statusDuration = 40;

            dragMultiplier = 2f;

            emitLight = true;
            lightColor = meldFloorGlowHadal;
            lightRadius = 15;
            attributes.set(MeldAttributes.meld, 0.3f);
        }};

        meldTrenchland = new Floor("meld-trenchland", 3){{
            isLiquid = true;
            drownTime = 120;

            status = MeldStatusEffects.drenched;
            statusDuration = 20;

            dragMultiplier = 1.5f;
            cacheLayer = CacheLayer.water;

            emitLight = true;
            lightColor = meldFloorGlowDeep;
            lightRadius = 15;
            attributes.set(MeldAttributes.meld, 0.2f);
        }};

        meldSwampland = new Floor("meld-swampland", 3){{
            isLiquid = true;

            cacheLayer = CacheLayer.water;
            supportsOverlay = true;

            emitLight = true;
            lightColor = meldFloorGlow;
            lightRadius = 20;
            attributes.set(MeldAttributes.meld, 0.1f);
        }};

        meldPlates = new Floor("meld-plates", 2){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            supportsOverlay = true;
            attributes.set(MeldAttributes.meld, 0.08f);
        }};

        meldCrystalScattered = new Floor("meld-crystal-scattered", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            supportsOverlay = true;
            attributes.set(MeldAttributes.meld, 0.06f);
        }};

        sandMeld = new Floor("sand-meld", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            supportsOverlay = true;
            attributes.set(MeldAttributes.meld, 0.05f);
        }};
        redsandMeld = new Floor("red-sand-meld", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            supportsOverlay = true;
            attributes.set(MeldAttributes.meld, 0.08f);
        }};
        slateMeld = new Floor("slate-meld", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            supportsOverlay = true;
            attributes.set(MeldAttributes.meld, 0.02f);
        }};

        bedrockMeld = new Floor("bedrock-meld", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;

            status = MeldStatusEffects.drenched;
            statusDuration = 25;
            supportsOverlay = true;
            attributes.set(MeldAttributes.meld, 0.05f);
        }};


        meldCrystalFloor = new Floor("meld-crystal-floor", 3){{
        }};

        meldCrystalHardFloor = new Floor("meld-hard-crystal-floor", 3){{
        }};

        carbonicVent = new SteamVent("carbonic-vent"){{
            variants = 0;
            blendGroup = parent = carbonicPlates;
            attributes.set(Attribute.steam, 1);
        }};

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
        earthenWall = new StaticWall("earthen-wall"){{
            variants = 2;
        }};

        //Start storm stuff before badlands since badlands should blend onto storm
        resonantStone = new Floor("resonant-stone", 3);
        dissonantShale = new Floor("dissonant-shale", 3);
        dissonantShaleStruck = new Floor("dissonant-shale-struck", 3);
        dissonantShaleScorched = new Floor("dissonant-shale-scorched", 3);
        dissonantFragments = new Floor("dissonant-fragments", 0);

        polishedSandstoneTile = new Floor("polished-sandstone-tile", 3);

        //Storm stuff
        likesand = new Floor("likesand", 3);
        likestone = new Floor("likestone", 3);
        likesalt = new Floor("likesalt", 3);

        redSilt = new Floor("red-silt", 3);
        aspectSoil = new Floor("aspect-soil", 3){{
            attributes.set(MeldAttributes.soilAttr, 0.25f);
        }};
        softSand = new Floor("soft-sand", 3);
        sandstone = new Floor("hard-sand", 3);
        redSand = new Floor("red-sand", 3){{
            itemDrop = MeldItems.clayMallows;
            playerUnmineable = true;
        }};
        redSandWeave = new Floor("red-sand-weave", 3);
        crackstone = new Floor("cracked-sand", 4);

        goldSand = new Floor("gold-sand", 3){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;

            liquidDrop = MeldLiquids.ichor;

            status = MeldStatusEffects.drenched;
            statusDuration = 25;
            supportsOverlay = true;
        }};
        goldSlurry = new Floor("gold-slurry", 4){{
            isLiquid = true;
            cacheLayer = CacheLayer.water;

            liquidDrop = MeldLiquids.ichor;

            status = MeldStatusEffects.drenched;
            statusDuration = 45;
            supportsOverlay = true;
        }};

        iampsiSpecks = new ModdedOreBlock("iampsi-specks", MeldItems.iampsi){{
            variants = 4;
            mapColor = Color.valueOf("a582f7");
            needsSurface = false;
        }};

        quartzFlakes = new ModdedOreBlock("quartz-flakes", MeldItems.quartzStrata){{
            variants = 4;
            mapColor = Color.valueOf("ef84fb");
            needsSurface = false;
        }};

        meldWall = new StaticWall("meld-wall"){{
            variants = 3;
        }};

        meldCrystalWall = new StaticWall("meld-crystal-wall"){{
            variants = 3;
        }};

        metalMeshWall = new StaticWall("metal-mesh-wall"){{
            variants = 3;
        }};

        metalMeshWallMeld = new StaticWall("metal-mesh-wall-meld"){{
            variants = 3;
        }};

        metalAetherWall = new StaticWall("metal-aether-wall"){{
            variants = 3;

            emitLight = true;
            lightRadius = 48;
            lightColor = Color.valueOf("e5932e").a(0.45f);
        }};


        pillowWall = new StaticWall("pillow-wall"){{
            variants = 2;
        }};

        sandstoneWall = new StaticWall("sandstone-wall"){{
            variants = 2;
        }};

        polishedSandstoneWall = new StaticWall("polished-sandstone-wall"){{
            variants = 2;
        }};

        softDune = new TreeWall("soft-dune"){{
            variants = 2;
            solid = false;
            hasShadow = false;
            mapColor = Color.valueOf("f3e1af");
        }};

        sandstonePillar = new SupportPillar("sandstone-pillar"){{
            requirements(Category.effect, ItemStack.with(MeldItems.debris, 500));
            mapColor = Color.black;
        }};

        mallowWall = new StaticWall("mallow-wall"){{
            variants = 2;
            itemDrop = MeldItems.clayMallows;
        }};

        crackstoneWall = new StaticWall("crackstone-wall"){{
            variants = 2;
        }};

        likesandWall = new StaticTree("likesand-wall"){{
            variants = 2;
            itemDrop = MeldItems.likestoneSediments;
        }};

        likestoneWall = new StaticTree("likestone-wall"){{
            variants = 2;
            itemDrop = MeldItems.likestoneSediments;
        }};

        likesaltWall= new StaticTree("likesalt-wall"){{
            variants = 2;
        }};

        aetherGrowth = new SteamVent("aether-growth"){{
            variants = 0;
            attributes.set(MeldAttributes.aetherAttr, 1);
            blendGroup = parent = softSand;
        }};

        metalWeaveAether = new SteamVent("metal-weave-aether"){{
            variants = 0;
            attributes.set(MeldAttributes.aetherAttr, 1);
            blendGroup = parent = metalWeave;
        }};

        meldCluster = new WobbleProp("meld-cluster-small"){{
            requirements(Category.effect, ItemStack.with(MeldItems.debris, 50));

            buildVisibility = BuildVisibility.sandboxOnly;

            instantDeconstruct = false;
            buildTime = 10;
            variants = 2;
        }};
        meldMetalStick = new WobbleProp("meld-metal-stick"){{
            requirements(Category.effect, ItemStack.with(MeldItems.debris, 15));

            buildVisibility = BuildVisibility.sandboxOnly;

            instantDeconstruct = false;
            buildTime = 10;
            variants = 3;
        }};
        meldProtrusion = new Prop("meld-protrusion"){{
            requirements(Category.effect, ItemStack.with(MeldItems.debris, 35));

            buildVisibility = BuildVisibility.sandboxOnly;

            solid = true;
            instantDeconstruct = false;
            buildTime = 10;
            variants = 2;
        }};
        meldPools = new Prop("meld-pools"){{
            requirements(Category.effect, ItemStack.with(MeldItems.debris, 65));

            buildVisibility = BuildVisibility.sandboxOnly;

            solid = true;
            customShadow = true;
            instantDeconstruct = false;
            buildTime = 45;
            variants = 3;
        }};

        meldClusterLarge = new AetherCluster("meld-cluster-large"){{
            requirements(Category.effect, ItemStack.with(MeldItems.debris, 300));
            size = 3;

            buildVisibility = BuildVisibility.sandboxOnly;

            buildTime = 120;
        }};

        dissonitreCluster = new AetherCluster("dissonitre-cluster"){{
            requirements(Category.effect, ItemStack.with(MeldItems.dissonitre, 300));
            size = 1;

            buildVisibility = BuildVisibility.sandboxOnly;
            instantDeconstruct = false;
            buildTime = 10;
        }};

        meldCrystal = new WobbleProp("meld-crystal"){{
            requirements(Category.effect, ItemStack.with(MeldItems.meldShard, 25));

            buildVisibility = BuildVisibility.sandboxOnly;

            instantDeconstruct = false;
            buildTime = 10;
            variants = 3;
        }};
        mixedCarbonicBoulder = new Prop("mixed-carbonic-boulder"){{
            requirements(Category.effect, ItemStack.with(MeldItems.carbolith, 35, MeldItems.stonyParticulate, 45));
            solid = true;
            alwaysReplace = false;

            buildVisibility = BuildVisibility.sandboxOnly;

            instantDeconstruct = false;
            buildTime = 60;
            variants = 3;
        }};

        pillowGlintCluster = new Prop("pillow-glint-cluster"){{
            requirements(Category.effect, ItemStack.with(MeldItems.silver, 25, MeldItems.annealedSilver, 40));
            solid = true;
            alwaysReplace = false;

            buildVisibility = BuildVisibility.sandboxOnly;

            instantDeconstruct = false;
            buildTime = 45;
            variants = 2;
        }};

        iampsiGemstone = new Prop("iampsi-gemstone"){{
            requirements(Category.effect, ItemStack.with(MeldItems.silver, 25, MeldItems.iampsi, 45, MeldItems.quartzStrata, 25));
            solid = true;
            alwaysReplace = false;

            buildVisibility = BuildVisibility.sandboxOnly;

            instantDeconstruct = false;
            buildTime = 40;
            variants = 4;
        }};

        quartzSpikes = new Prop("quartz-spikes"){{
            requirements(Category.effect, ItemStack.with(MeldItems.quartzStrata, 15));
            solid = false;
            alwaysReplace = false;

            buildVisibility = BuildVisibility.sandboxOnly;

            customShadow = true;
            instantDeconstruct = false;
            buildTime = 5;
            variants = 5;
        }};

        vitricWeave = new Prop("vitric-weave"){{
            requirements(Category.effect, ItemStack.with(MeldItems.dissonitre, 5, MeldItems.vitricMesh, 24));
            solid = false;
            alwaysReplace = false;

            variants = 2;
            buildTime = 5;
            instantDeconstruct = false;
        }};

        mallowCluster = new Prop("mallow-cluster"){{

        }};

        earthenResonarumCluster = new Prop("earthen-resonarum-cluster"){{
            requirements(Category.effect, ItemStack.with(MeldItems.resonarum, 40));
            solid = true;
            alwaysReplace = false;

            buildVisibility = BuildVisibility.sandboxOnly;

            instantDeconstruct = false;
            buildTime = 40;
            variants = 2;
        }};


        meldCrystalLarge = new TallBlock("meld-crystal-large"){{
            variants = 1;
            customShadow = true;
        }};
        meldSupportFrame = new TallBlock("meld-support-frame"){{
            variants = 2;
            customShadow = true;
        }};

        Seq<Floor> meldFloors = Seq.with(meldSwampland, meldTrenchland, meldHadaland,
                meldCrystalScattered,
                sandMeld, redsandMeld,
                slateMeld, bedrockMeld);

        meldFloors.each(m -> m.liquidDrop = MeldLiquids.meld);
    }
}
