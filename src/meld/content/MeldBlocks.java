package meld.content;

import arc.*;
import arc.graphics.Color;
import arc.math.Interp;
import meld.*;
import meld.entities.bullet.OutflowBulletType;
import meld.entities.bullet.TransitionBulletType;
import meld.graphics.*;
import meld.world.blocks.consumers.ConsumeAspects;
import meld.world.blocks.*;
import meld.world.blocks.crafting.ModularCrafter;
import meld.world.blocks.crafting.RecipeCrafter;
import meld.world.blocks.crafting.modules.rework.*;
import meld.world.blocks.crafting.recipe.ItemRecipe;
import meld.world.blocks.crafting.recipe.SpoolRecipe;
import meld.world.blocks.crafting.modules.*;
import meld.world.blocks.crafting.modules.GateModule.ConsumeCondition;
import meld.world.blocks.crafting.modules.GateModule.OutputCondition;
import meld.world.blocks.crafting.recipe.TimedRecipe;
import meld.world.blocks.fluid.*;
import meld.world.blocks.items.PriorityInputSplitter;
import meld.world.blocks.production.SingleBeamDrill;
import meld.world.meta.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.ArmoredConduit;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;

import static mindustry.type.ItemStack.with;

public class MeldBlocks {

    //Strata blocks first
    public static Block chute, chuteRouter, chuteBridge, chuteJunction, chuteOverflow, unloadingHub;

    public static Block sonarSpire, movementAnchor, nullifier,
            //Bruisekit: Targets largest, highest hp blocks, functional blocks first, continuously heals

            //Gauze chainheals smallest, lowest hp blocks, low target prio blocks first.

            //Suture shoots healing needles in a cone at the closest damaged block. Impales enemies, causing them to take constant chip damage and be sedated.
            bruisekit, gauze, suture;

    public static Block coreRaft, aetherAccumulator, elementalBlaster, pneumaticPulsear,
            earthboundInfuser, fumehood, sharkFactory;

    public static ModularCrafter gasKiln, rotaryKiln, pneumaticExtruder;

    public static Block channelNode, channelFace, aspectOutlet, aspectChannel, channelDirector, channelVent, manualValve, intakeValve, valveController, pipebox;

    public static Block sunder, molotov, vivisection;

    public static Block silverHusk, shadesteelShingles;

    //Meld blocks
    public static Block pipeline, pipelineRouter, pipelineCrossing, pipelineBridge,
            meldCannon, meldMortar,
            jillaCoffer, craigCoffer, braigCoffer,
            meldCultivator, meldNode, meldSuppressor, meldSynapse,
            meldAmplifier, meldCapsule;
    public static Block crystalBarrier, crystalBarrierLarge,
            carbonicBarrier, carbonicBarrierLarge
            ;

    public static float outletRate = 100f/60f;

    public static Item item(String name){
        return new Item(name);
    }

    public static void load(){

        channelFace = new FlexibleSizeJunction("channel-face"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 2
            ));
            health = 120;
            solid = false;
            placeableLiquid = true;
            usePassedOffset = false;
        }};

        channelNode = new LiquidRouter("channel-node"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 5
            ));
            health = 120;

            liquidCapacity = 200;
            solid = false;
            placeableLiquid = true;
        }};

        aspectOutlet = new RecipeCrafter("aspect-outlet"){{
            outputDirections.putAll(MeldLiquids.aspect, new int[]{0}, MeldLiquids.boundAspect, new int[]{0});

            requirements(Category.liquid, with(
                    MeldItems.debris, 7
            ));

            health = 200;

            solid = false;
            hasLiquids = true;

            placeableLiquid = true;

            //TODO: WE DON'T TALK ABOUT THAT WE DON'T TALK ABOUT THAT WE DON'T TALK ABOUT THAT WE DON'T TAAAAAAAAAAAA
            //liquidOutputDirections = new int[]{0};
            rotate = true;
            quickRotate = true;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(){{
                        drawLiquid = MeldLiquids.aspect;
                    }},
                    new DrawLiquidTile(){{
                        drawLiquid = MeldLiquids.boundAspect;
                    }},
                    new DrawRegion(),
                    new DrawSideRegion()
            );

            //consumeLiquid(MeldLiquids.aether, outletRate/10);

            liquidCapacity = 100;

            inputLiquidSlots = 1;
            outputLiquidSlots = 1;
            recipes.addAll(
                    new TimedRecipe(){{
                        craftTime = 10;
                        float multi = MeldLiquids.aetherEfficiencies.get(MeldLiquids.aether, 1);
                        float density = MeldLiquids.aetherDensities.get(MeldLiquids.aether, 1);
                        inputLiquids = LiquidStack.with(MeldLiquids.aether, outletRate/density);
                        outputLiquids = LiquidStack.with(MeldLiquids.aspect, outletRate * multi * 10);
                    }},
                    new TimedRecipe(){{
                        craftTime = 10;
                        float multi = MeldLiquids.aetherEfficiencies.get(MeldLiquids.pollutantMixture, 1);
                        float density = MeldLiquids.aetherDensities.get(MeldLiquids.pollutantMixture, 1);
                        inputLiquids = LiquidStack.with(MeldLiquids.pollutantMixture, outletRate/density);
                        outputLiquids = LiquidStack.with(MeldLiquids.boundAspect, outletRate * multi * 10);
                    }}
            );
        }};

        aspectChannel = new VisualAspectPipe("aspect-channel"){{
            requirements(Category.liquid, with(
                    MeldItems.annealedSilver, 5, MeldItems.glassMallows, 2
            ));
            leaks = false;
            health = 200;
            armor = 2;
            insulated = true;

            placeableLiquid = true;

            liquidCapacity = 80;
            size = 1;
            botColor = Color.white;
            junctionReplacement = channelFace;
        }};

        channelDirector = new ChannelDirector("channel-director"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 5
            ));

            health = 200;

            placeableLiquid = true;
            liquidPressure = 0.5f;

            liquidCapacity = 80;
            size = 1;
        }};

        //TODO: Lock players out of using in badlands/storm plains route if waste becomes too big of an issue
        channelVent = new ChannelVent("pressure-vent"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 8
            ));
            health = 180;
            armor = 1;

            minPressure = -0.25f;
            ventRate = 0.5f;

            liquidCapacity = 100;
            solid = false;
            placeableLiquid = true;
        }};

        manualValve = new ChannelValve("manual-valve"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 8
            ));

            health = 120;

            solid = false;
            placeableLiquid = true;
        }};

        intakeValve = new ChannelValve("intake-valve"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 80,
                    MeldItems.shadesteel, 48
            ));
            size = 3;

            health = 800;
            armor = 8;

            solid = false;
            placeableLiquid = true;
        }};

        valveController = new ValveController("valve-controller"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 12,
                    MeldItems.aspectPipe, 6
            ));
            size = 1;

            health = 120;

            solid = false;
            placeableLiquid = true;
        }};

        pipebox = new Pipebox("pipebox") {{
            requirements(Category.liquid, with(
                    MeldItems.debris, 2,
                    MeldItems.aspectPipe, 4
            ));
            size = 1;

            health = 120;

            solid = false;
            placeableLiquid = true;
        }};

        sunder = new ItemTurret("sunder"){{
            requirements(Category.turret, with(
                    MeldItems.debris, 45,
                    MeldItems.carbolith, 60
            ));
            size = 2;
            health = 840;
            range = 200;
            fogRadiusMultiplier = 0.25f;

            reload = 30;
            shootEffect = Fx.shootBig;
            shootWarmupSpeed = 0.09f;
            minWarmup = 0.7f;

            recoil = 0.25f;
            velocityRnd = 0.2f;
            inaccuracy = 8;
            shootCone = 5;
            shootY = 7;

            rotate = false;
            quickRotate = false;

            drawer = new DrawTurret(){{
                parts.addAll(
                        new RegionPart("-plate"){{
                            progress = PartProgress.warmup;
                            mirror = true;
                            under = false;
                            moveX = 1;
                            moveY = -1.55f;
                            moveRot = -15;
                        }},
                        new RegionPart("-barrel"){{
                            progress = PartProgress.warmup;
                            y = -1.5f;
                            moveY = 1;
                            mirror = false;
                            under = true;

                            moves.addAll(new PartMove(){{
                                progress = PartProgress.recoil;
                                y = -2.25f;
                            }});
                        }}
                );
            }};

            shoot = new ShootAlternate() {{
                spread = 4;
                shots = 2;
            }};

            ammoTypes.put(
                    MeldItems.debris,
                    new FlakBulletType(){{
                        collidesGround = true;

                        scaleLife = true;

                        speed = 6;
                        damage = 2;
                        lifetime = 30;
                        width = 3;
                        height = 8;
                        splashDamage = 10;
                        splashDamageRadius = 24;

                        knockback = 1;

                        despawnEffect = Fx.none;
                        hitEffect = new MultiEffect(
                                new ParticleEffect(){{
                                    lifetime = 13;
                                    particles = 3;
                                    length = 15;
                                    sizeFrom = 3;
                                    sizeTo = 0;
                                    sizeInterp = Interp.pow2In;
                                    colorFrom = colorTo = MeldPal.shockwaveGray;
                                }},
                                new ParticleEffect(){{
                                    lifetime = 9;
                                    line = true;
                                    particles = 2;
                                    length = 24;
                                    lenFrom = 4;
                                    lenTo = 0;
                                    strokeFrom = strokeTo = 2;

                                    interp = Interp.pow2Out;
                                    sizeInterp = Interp.pow2In;
                                    colorFrom = colorTo = MeldPal.sparkOrange;
                                }}
                        );

                        fragOnAbsorb = false;
                        shrinkY = 0.2f;
                        fragRandomSpread = 25;

                        fragBullets = 5;
                        fragBullet = new BasicBulletType(){{
                            lightRadius = 0;
                            speed = 8;
                            lifetime = 20;

                            shrinkY = 1;
                            shrinkX = 1;
                            shrinkInterp = Interp.pow5In;

                            sticky = true;
                            stickyExtraLifetime = 60;
                            pierce = true;
                            pierceCap = 2;
                            damage = 3;
                            splashDamage = 1;
                            splashDamageRadius = 2;

                            width = 3;
                            height = 8;

                            collidesGround = true;
                            hitEffect = despawnEffect = Fx.none;
                        }};
                    }}
            );
        }};

        molotov = new ItemTurret("molotov"){{
            requirements(Category.turret, with(
                    MeldItems.carbolith, 40,
                    MeldItems.silver, 70
            ));

            size = 2;
            health = 840;

            liquidCapacity = 50;

            range = 272;
            fogRadiusMultiplier = 0.25f;

            reload = 120;
            shootSound = Sounds.explosion;

            rotate = false;
            quickRotate = false;
            ammoPerShot = 4;

            targetAir = false;
            recoil = 1.5f;
            inaccuracy = 2;
            shootCone = 5;

            BulletType molBullet = new OutflowBulletType(){{
                //HHJKGHJGJK.
                collidesTiles = false;
                collides = false;
                collidesAir = false;
                scaleLife = true;
                hitShake = 1.0F;
                hitSound = Sounds.explosionArtillery;
                hitEffect = Fx.flakExplosion;
                shootEffect = Fx.shootBig;
                shrinkX = 0.25f;
                shrinkY = 0.8f;
                shrinkInterp = Interp.slope;
                trailEffect = Fx.artilleryTrail;
                trailChance = 0.35f;

                speed = 7;
                damage = 1;
                lifetime = 46;
                width = 18;
                height = 24;
                status = MeldStatusEffects.aspectBurn;
                statusDuration = 300;
                frontColor = Color.white;
                backColor = Color.valueOf("cbdbfc");

                splashDamage = 4;
                splashDamageRadius = 8;

                collidesTiles = false;
                makeFire = true;
                hitShake = 2.5f;

                despawnHit = true;
                fragOnAbsorb = false;
                trailColor = Color.valueOf("cbdbfc");
                ammoMultiplier = 1;
                fragBullets = 24;
                fragVelocityMin = 0.8f;
                fragVelocityMax = 1;
                fragLifeMin = 0.6f;

                fragRandomSpread = 360;

                outflowBullet = new ExplosionBulletType(){{
                    hitEffect = despawnEffect = Fx.none;
                    damage = splashDamage = 0;
                    fragBullets = 3;
                    fragRandomSpread = 15;
                    fragLifeMin = 0.5f;
                    killShooter = false;

                    fragBullet = new BasicBulletType() {{
                        lightRadius = 0;
                        speed = 8.5f;
                        drag = 0.03f;
                        damage = 2;
                        lifetime = 25;
                        pierce = true;
                        pierceCap = 4;
                        removeAfterPierce = true;
                        pierceArmor = true;
                        absorbable = false;


                        width = 7;
                        height = 16;
                        shrinkX = 1;
                        shrinkY = 1;
                        frontColor = Color.white;
                        backColor = Color.valueOf("cbdbfc");
                        splashDamage = 3;
                        splashDamageRadius = 8;
                        makeFire = true;
                        collidesAir = false;
                        despawnHit = false;
                        hitEffect = despawnEffect = Fx.none;

                        knockback = 0.35f;
                        impact = true;
                    }};
                }};

                fragBullet = new BasicBulletType(){{
                    lightRadius = 0;
                    speed = 4.5f;
                    drag = 0.04f;
                    damage = 2;
                    lifetime = 20;
                    pierce = true;
                    pierceCap = 2;
                    pierceArmor = true;
                    absorbable = false;


                    width = 9;
                    height = 13;
                    shrinkX = 1;
                    shrinkY = 1;
                    status = StatusEffects.burning;
                    statusDuration = 120;
                    frontColor = Color.white;
                    backColor = Color.valueOf("cbdbfc");
                    splashDamage = 5;
                    splashDamageRadius = 8;
                    makeFire = true;
                    collidesAir = false;
                    despawnHit = false;
                    hitEffect = despawnEffect = Fx.none;

                    knockback = 0.35f;
                    impact = true;

                    fragRandomSpread = 0;
                    fragBullets = 1;
                    fragBullet = new BasicBulletType(){{
                        width = height = 2;
                        speed = 6;
                        lifetime = 8;

                        shrinkX = shrinkY = 1;
                        shrinkInterp = Interp.pow2In;

                        damage = 1;
                        pierce = true;
                        sticky = true;
                        stickyExtraLifetime = 60;

                        frontColor = Color.white;
                        backColor = Color.valueOf("cbdbfc");

                        hitEffect = despawnEffect = Fx.none;
                    }};
                }};
            }};
            ammoTypes.putAll(
                    MeldItems.silver,
                    molBullet,
                    MeldItems.annealedSilver,
                    molBullet
            );

            consume(new ConsumeAspects(outletRate, MeldLiquids.aspectEfficiencies, MeldLiquids.aspectDensities));
        }};

        shadesteelShingles = new Wall("shadesteel-shingles"){{
            requirements(Category.defense, with(MeldItems.shadesteel, 64));
            size = 2;
            health = 2400;
        }};

        silverHusk = new RegenProjector("silver-husk"){{
            requirements(Category.defense, with(MeldItems.debris, 15, MeldItems.annealedSilver, 8));
            health = 600;
            armor = 3;
            range = 1;
            healPercent = 0.05f;
            effectChance = 0.05f;
            baseColor = MeldPal.aspect;

            insulated = true;

            liquidCapacity = 20;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"){{
                    layer = Layer.block - 1;
                }},
                    new DrawLiquidTile(MeldLiquids.aspect),
                    new DrawLiquidTile(MeldLiquids.boundAspect),
                new DrawDefault()
            );

            consume(new ConsumeAspects(outletRate/2, MeldLiquids.aspectEfficiencies, MeldLiquids.aspectDensities));
        }};

        coreRaft = new CoreRaft("core-raft"){
            {
            requirements(Category.effect, with(
                    MeldItems.debris, 600,
                    MeldItems.carbolith, 350
            ));
            size = 3;
            health = 4000;

            itemCapacity = 900;

            unitCapModifier = 6;

            unitType = MeldUnits.bulbhead;
            solid = false;
        }};

        aetherAccumulator = new AttributeCrafter("aether-accumulator"){{
            requirements(Category.production, with(
                    MeldItems.debris, 40
            ));
            size = 3;

            health = 300;

            attribute = MeldAttributes.aetherAttr;
            baseEfficiency = 0;
            minEfficiency = 8.9f;
            maxBoost = 2;
            boostScale = 1/9f;
            liquidCapacity = 300;

            outputLiquid = new LiquidStack(MeldLiquids.aether, 1);
        }};

        fumehood = new ModularCrafter("fumehood"){{
            requirements(Category.production, with(
                    MeldItems.debris, 80, MeldItems.shadesteel, 40
            ));
            size = 3;
            health = 500;
            armor = 2;
            hasLiquids = true;
            replaceBars = false;

            squareSprite = false;

            dumpedLiquids.addAll(MeldLiquids.pollutantMixture, MeldLiquids.fumes);

            //Should make all the modules trigger in order
            /*hookAll(BlockEvent.Defaults.proximityUpdate,
                    new AttributeModule(){{
                        attribute = MeldAttributes.aetherAttr;
                        baseEfficiency = 0;
                        minEfficiency = 1;
                        boostScale = 1f/9f;

                        efficiencyPin = 0;
                    }},
                    new AttributeModule(){{
                        attribute = Attribute.steam;
                        baseEfficiency = 0;
                        minEfficiency = 1;
                        boostScale = 1f/9f;

                        efficiencyPin = 1;
                    }}
            );*/

            /*modules.addAll(
                    new ProduceLiquidModule(new LiquidStack(MeldLiquids.pollutantMixture, 1), 0),
                    new ProduceLiquidModule(new LiquidStack(MeldLiquids.fumes, 1), 1)
            );*/
        }};

        elementalBlaster = new BeamDrill("elemental-blaster"){{
            requirements(Category.production, with(
                    MeldItems.debris, 40
            ));
            size = 3;
            health = 420;
            placeableLiquid = true;

            drillTime = 90;
            tier = 2;

            liquidCapacity = outletRate * 60 * 4;

            optionalBoostIntensity = 2;

            consume(new ConsumeAspects(
                    outletRate, MeldLiquids.aspectEfficiencies, MeldLiquids.aspectDensities
            ));

            consume(new ConsumeLiquid(
                    MeldLiquids.meld, 1
            ){{
                optional = true;
                booster = true;
            }});
        }};

        pneumaticPulsear = new SingleBeamDrill("pneumatic-pulsar"){{
            requirements(Category.production, with(MeldItems.debris, 500, MeldItems.carbolith, 250, MeldItems.shadesteel, 350, MeldItems.aspectPipe, 300));
            health = 2400;
            armor = 40;

            size = 5;
            itemCapacity = 100;
            baseProductivity = 50;
            drillTime = 360;
            range = 8;

            transformItems.putAll(
                    MeldItems.clayMallows, MeldItems.glassMallows,
                    MeldItems.tenbris, MeldItems.shadesteel
            );

            liquidCapacity = 50;


            consume(new ConsumeAspects(outletRate/2, MeldLiquids.outletEfficiencies, MeldLiquids.outletDensities));
        }};

        new ModularCrafter("stupid-crafter"){{
            requirements(Category.crafting, with(MeldItems.debris, 40));
            size = 3;

            modules.addAll(
                new ProduceHeatModule(0){{
                    heatOutput = 10;
                }},
                new AttributeModule(0){{
                    attribute = Attribute.heat;
                    baseEfficiency = 0f;
                    maxBoost = 2f;
                    storagePin = 1;
                }},
                //This is assigned to the same pin as the attribute, and updates after,
                //which means it will only "top off" the efficiency that the attributes don't reach.
                new ConsumeItemModule(0){{
                    items = ItemStack.with(Items.pyratite, 1);
                    efficiencyIncrease = 4f;
                    time = 20f;
                    progressPin = 2;
                }},
                //Same as above, but will also cover for the pyratite consumer.
                new ConsumeItemModule(0){{
                    items = ItemStack.with(Items.coal, 1);
                    time = 40f;
                    progressPin = 3;
                }}
            );
        }};

        new ModularCrafter("stupid-silicon-smelter"){
            //I refuse to add sprites to the files.
            @Override
            public void load(){
                super.load();
                region = Core.atlas.find("silicon-smelter");
                fullIcon = Core.atlas.find("silicon-smelter");
                uiIcon = Core.atlas.find("silicon-smelter");
                if(drawer instanceof DrawMulti m && m.drawers[1] instanceof DrawHeatInput f) f.heat = Core.atlas.find("phase-heater-heat");
                if(drawer instanceof DrawMulti m && m.drawers[2] instanceof DrawRegion r) r.region = Core.atlas.find("pneumatic-drill-rotator");
                if(drawer instanceof DrawMulti m && m.drawers[3] instanceof DrawFlame f) f.top = Core.atlas.find("silicon-smelter-top");
            }

            {
                requirements(Category.crafting, with(MeldItems.debris, 40));
                size = 2;

                drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawHeatInput(),
                    new DrawRegion("", 3f, true),
                    new DrawFlame(Color.valueOf("ffef99"))
                );

                modules.addAll(
                    new ProduceItemModule( 1, 2){{
                        items = ItemStack.with(Items.silicon, 1);
                        time = 60f;
                        progressPin = 0;
                    }},
                        new ConsumeItemModule(1){{
                            items = ItemStack.with(Items.sand, 1);
                            time = 20f;
                            progressPin = -1;
                        }},
                        new ConsumeHeatModule(2),
                        new AttributeModule(2){{
                            attribute = Attribute.heat;
                            baseEfficiency = 0f;
                            maxBoost = 2f;
                            storagePin = -2;
                        }},
                        //This is assigned to the same pin as the attribute, and updates after,
                        //which means it will only "top off" the efficiency that the attributes don't reach.
                        new ConsumeItemModule(2){{
                            items = ItemStack.with(Items.pyratite, 1);
                            efficiencyIncrease = 2.5f;
                            time = 20f;
                            progressPin = -3;
                        }},
                        //Same as above, but will also cover for the pyratite consumer.
                        new ConsumeItemModule(2){{
                            items = ItemStack.with(Items.coal, 1);
                            time = 40f;
                            progressPin = -4;
                        }}
                );
            }
        };

        earthboundInfuser = new ModularCrafter("earthbound-infuser"){{
            requirements(Category.crafting, with(
                    MeldItems.debris, 40,
                    MeldItems.silver, 60
            ));
            size = 3;

            hasItems = true;
            hasLiquids = true;
            liquidCapacity = outletRate * 60 * 2;

            acceptedLiquids.add(MeldLiquids.fumes, MeldLiquids.aspect);
            acceptedItems.add(MeldItems.debris);
            dumpedItems.add(MeldItems.carbolith);


            /*//Should make all the modules trigger in order
            hookAll(BlockEvent.Defaults.proximityUpdate,
                    new AttributeModule(){{
                        attribute = Attribute.steam;
                        baseEfficiency = 0;
                        minEfficiency = 1;
                        boostScale = 1f/9f;

                        efficiencyPin = 0;
                    }}
            );*/

            ItemRecipe carbolith = new ItemRecipe(with(MeldItems.debris, 1), with(MeldItems.carbolith, 1));

            /*modules.addAll(
                    new ProduceLiquidModule(new LiquidStack(MeldLiquids.fumes, 2f), 0),
                    new GateModule(1, new GateModule.RecipeCondition(carbolith)),
                    new ConsumeLiquidModule(LiquidStack.with(MeldLiquids.fumes, 1, MeldLiquids.aspect, outletRate * 2), 1, 2),
                    new RecipeCraftingModule(){{
                        efficiencyPin = 2;
                        progressPin = 5;
                        craftTime = 12;
                        recipe = carbolith;
                    }}
            );*/


            /*

            displayEfficiencyScale = 9;
            craftTime = 60/5f;
             */
        }};

        gasKiln = new ModularCrafter("gas-kiln"){{
            requirements(Category.crafting, with(MeldItems.debris, 80));
            size = 3;

            hasItems = true;
            hasLiquids = true;
            liquidCapacity = outletRate * 60 * 2;

            itemCapacity = 10;

            acceptedLiquids.addAll(MeldLiquids.aspect, MeldLiquids.boundAspect);
            acceptedItems.addAll(MeldItems.tenbris, MeldItems.clayMallows, MeldItems.carbolith, MeldItems.debris, MeldItems.shadesteel, MeldItems.glassMallows, MeldItems.silver);
            dumpedItems.addAll(MeldItems.cruciblePlating, MeldItems.shadesteel, MeldItems.glassMallows, MeldItems.annealedSilver);
            dumpedLiquids.addAll(MeldLiquids.fumes);

            ItemRecipe
            shadesteel = new ItemRecipe(with(MeldItems.tenbris, 2, MeldItems.carbolith, 2), with(MeldItems.shadesteel, 2)).output(LiquidStack.with(MeldLiquids.fumes, 60)),
            glass = new ItemRecipe(with(MeldItems.clayMallows, 2), with(MeldItems.glassMallows, 2)),
            silver = new ItemRecipe(with(MeldItems.silver, 1), with(MeldItems.annealedSilver, 2)),
            platings1 = new ItemRecipe(with(MeldItems.shadesteel, 4, MeldItems.debris, 2), with(MeldItems.cruciblePlating, 4)),
            platings2 = new ItemRecipe(with(MeldItems.glassMallows, 4, MeldItems.debris, 2), with(MeldItems.cruciblePlating, 4));

            float produceTime = 60;

            modules.addAll(
                    new GateModule(
                            ModOUT.ZERO, new GateModule.RecipeCondition(shadesteel)
                    ),
                    new GateModule(
                            ModOUT.ONE, new GateModule.RecipeCondition(glass)
                    ),
                    new GateModule(
                            ModOUT.TWO, new GateModule.RecipeCondition(silver)
                    ),
                    new GateModule(
                            ModOUT.THREE, new GateModule.RecipeCondition(platings1)
                    ),
                    new GateModule(
                            ModOUT.FOUR, new GateModule.RecipeCondition(platings2)
                    ),
                    new LambdaModule(){{
                        //Set every pin after the first one found as 1 to zero
                        updater = b -> {
                            int a = 0;
                            for(int i = 0; i < 5; i++){
                                b.setPin(i, Math.max(b.getPin(i) - a, 0));
                                a = (int)Math.max(a, b.getPin(i));
                            }
                            b.setPin(ModOUT.FIVE, a);
                        };
                    }},
                    new ConsumeAspectModule(outletRate * 2, MeldLiquids.aspectEfficiencies, MeldLiquids.aspectDensities, ModIN.FIVE, ModOUT.SIX),
                    new RecipeCraftingModule(){{
                        recipe = shadesteel;
                        efficiencyPins = new int[]{ModIN.SIX, ModIN.ZERO};
                        progressPin = ModOUT.SEVEN;
                        craftTime = produceTime;
                    }},
                    new RecipeCraftingModule(){{
                        recipe = glass;
                        efficiencyPins = new int[]{ModIN.SIX, ModIN.ONE};
                        progressPin = ModOUT.EIGHT;
                        craftTime = produceTime;
                    }},
                    new RecipeCraftingModule(){{
                        recipe = silver;
                        efficiencyPins = new int[]{ModIN.SIX, ModIN.TWO};
                        progressPin = ModOUT.NINE;
                        craftTime = produceTime;
                    }},
                    new RecipeCraftingModule(){{
                        recipe = platings1;
                        efficiencyPins = new int[]{ModIN.SIX, ModIN.THREE};
                        progressPin = ModOUT.TEN;
                        craftTime = produceTime;
                    }},
                    new RecipeCraftingModule(){{
                        recipe = platings2;
                        efficiencyPins = new int[]{ModIN.SIX, ModIN.FOUR};
                        progressPin = ModOUT.ELEVEN;
                        craftTime = produceTime;
                    }}
            );
        }};

        rotaryKiln = new ModularCrafter("rotary-kiln"){{
            requirements(Category.crafting, with(MeldItems.debris, 250, MeldItems.cruciblePlating, 150));
            size = 5;

            hasItems = true;
            hasLiquids = true;
            liquidCapacity = outletRate * 60 * 2;

            itemCapacity = 48;

            acceptedLiquids.addAll(MeldLiquids.aspect, MeldLiquids.boundAspect);
            acceptedItems.addAll(MeldItems.tenbris, MeldItems.clayMallows, MeldItems.carbolith, MeldItems.debris, MeldItems.shadesteel, MeldItems.glassMallows, MeldItems.silver);
            dumpedItems.addAll(MeldItems.cruciblePlating, MeldItems.shadesteel, MeldItems.glassMallows, MeldItems.annealedSilver);

            defaultData.put(0, 1);

            ItemRecipe shadesteel = new ItemRecipe(with(MeldItems.tenbris, 12), with(MeldItems.shadesteel, 12));
            ItemRecipe glass = new ItemRecipe(with(MeldItems.clayMallows, 12), with(MeldItems.glassMallows, 12));
            ItemRecipe silver = new ItemRecipe(with(MeldItems.silver, 1), with(MeldItems.annealedSilver, 2));
            ItemRecipe platings1 = new ItemRecipe(with(MeldItems.shadesteel, 4, MeldItems.debris, 2), with(MeldItems.cruciblePlating, 4));
            ItemRecipe platings2 = new ItemRecipe(with(MeldItems.glassMallows, 4, MeldItems.debris, 2), with(MeldItems.cruciblePlating, 4));

            //The max crafting speed when boosted
            float craftingTime = 120;

            modules.addAll(
                    //Set the base rate based on aspect intake
                    new ConsumeAspectModule(outletRate * 8, MeldLiquids.aspectEfficiencies, MeldLiquids.aspectDensities, 0, 1),
                    //Setup the flags for possible recipies
                    new GateModule(
                            ModOUT.TWO,
                            new ConsumeCondition(shadesteel.inputItems),
                            new OutputCondition(shadesteel.outputItems)
                    ),
                    new GateModule(
                            ModOUT.THREE,
                            new ConsumeCondition(glass.inputItems),
                            new OutputCondition(glass.outputItems)
                    ),
                    new GateModule(
                            ModOUT.FOUR,
                            new ConsumeCondition(platings1.inputItems),
                            new OutputCondition(platings1.outputItems)
                    ),
                    new GateModule(
                            ModOUT.FIVE,
                            new ConsumeCondition(platings2.inputItems),
                            new OutputCondition(platings2.outputItems)
                    ),
                    new GateModule(
                            ModOUT.SIX,
                            new ConsumeCondition(silver.inputItems),
                            new OutputCondition(silver.outputItems)
                    ),
                    //Take the average efficiency
                    new LambdaModule(){{
                        int[] gatePins = new int[]{2, 3, 4, 5, 6};
                        float drums = 3;
                        int outPin = 7;

                        updater = build -> {
                            float total = 0;
                            for(int i: gatePins){
                                total += build.data.get(i);
                            }
                            float out = drums/Math.max(total, 1);
                            build.setPin(outPin, out);
                        };
                    }},
                    //Recipies
                    new RecipeCraftingModule(){{
                        recipe = shadesteel;
                        efficiencyPins = new int[]{1, 2, 7};
                        progressPin = 8;
                        craftTime = craftingTime;
                    }},
                    new RecipeCraftingModule(){{
                        recipe = glass;
                        efficiencyPins = new int[]{1, 3, 7};
                        progressPin = 9;
                        craftTime = craftingTime;
                    }},
                    new RecipeCraftingModule(){{
                        recipe = platings1;
                        efficiencyPins = new int[]{1, 4, 7};
                        progressPin = 10;
                        craftTime = craftingTime;
                    }},
                    new RecipeCraftingModule(){{
                        recipe = platings2;
                        efficiencyPins = new int[]{1, 5, 7};
                        progressPin = 11;
                        craftTime = craftingTime;
                    }},
                    new RecipeCraftingModule(){{
                        recipe = silver;
                        efficiencyPins = new int[]{1, 6, 7};
                        progressPin = 12;
                        craftTime = craftingTime/8f;
                    }}
            );
        }};

        pneumaticExtruder = new ModularCrafter("pneumatic-extruder"){{
            requirements(Category.crafting, with(MeldItems.debris, 60, MeldItems.cruciblePlating, 40));
            size = 3;

            hasItems = true;
            hasLiquids = true;
            liquidCapacity = outletRate * 60;

            itemCapacity = 10;

            acceptedLiquids.addAll(MeldLiquids.aspect, MeldLiquids.boundAspect);
            acceptedItems.addAll(MeldItems.shadesteel, MeldItems.elnarDust, MeldItems.debris, MeldItems.annealedSilver);
            dumpedItems.addAll(MeldItems.aspectPipe);

            ItemRecipe
                    aspectPipe1 = new ItemRecipe(with(MeldItems.shadesteel, 2, MeldItems.elnarDust, 2), with(MeldItems.aspectPipe, 4)),
                    aspectPipe2 = new ItemRecipe(with(MeldItems.debris, 1, MeldItems.annealedSilver, 2), with(MeldItems.aspectPipe, 4));

            float produceTime = 30;

            /*modules.addAll(
                    new GateModule(
                            ModOUT.ZERO, new GateModule.RecipeCondition(aspectPipe1)
                    ),
                    new GateModule(
                            ModOUT.ONE, new GateModule.RecipeCondition(aspectPipe2)
                    ),
                    new LambdaModule(){{
                        //Set every ping after the first one found as 1 to zero
                        updater = b -> {
                            int a = 0;
                            for(int i = 0; i < 2; i++){
                                b.setPin(i, Math.max(b.getPin(i) - a, 0));
                                a = (int)Math.max(a, b.getPin(i));
                            }
                            b.setPin(ModOUT.TWO, a);
                        };
                    }},
                    new ConsumeLiquidModule(LiquidStack.with(MeldLiquids.aspect, outletRate * 2), ModIN.TWO, ModOUT.THREE),
                    new RecipeCraftingModule(){{
                        recipe = aspectPipe1;
                        efficiencyPins = new int[]{ModIN.THREE, ModIN.ZERO};
                        progressPin = ModOUT.FOUR;
                        craftTime = produceTime;
                    }},
                    new RecipeCraftingModule(){{
                        recipe = aspectPipe2;
                        efficiencyPins = new int[]{ModIN.THREE, ModIN.ONE};
                        progressPin = ModOUT.FIVE;
                        craftTime = produceTime;
                    }}
            );*/
        }};

        sharkFactory = new UnitFactory("shark-factory"){{
            requirements(Category.units, with(MeldItems.debris, 500, MeldItems.carbolith, 350, MeldItems.silver, 450));
            size = 5;
            health = 2500;

            consume(new ConsumeLiquid(MeldLiquids.aspect, outletRate * 12));
            plans.addAll(new UnitPlan(MeldUnits.shark, 60 * 5, with(MeldItems.silver, 80, MeldItems.carbolith, 60)));
        }};

        sonarSpire = new SonarSpire("sonar-spire"){{
            requirements(Category.effect, with(
                    MeldItems.debris, 60,
                    MeldItems.silver, 80
            ));
            size = 2;
            health = 300;

            priority = BuildingPriority.radar;

            pulseDuration = 360;
            graceDuration = 480;
            shrinkSpeed = 16/60f;

            range = 220;

            liquidCapacity = 1 * outletRate * 60;
            hasLiquids = true;

            consume(new ConsumeAspects(outletRate * 2, MeldLiquids.aspectEfficiencies, MeldLiquids.aspectDensities));
        }};

        movementAnchor = new MovementAnchor("movement-anchor"){{
            requirements(Category.effect, with(
                    MeldItems.debris, 80,
                    MeldItems.carbolith, 120
            ));
            size = 3;
            health = 1200;
            range = 22 * Vars.tilesize;

            consume(new ConsumeAspects(outletRate * 3, MeldLiquids.aspectEfficiencies, MeldLiquids.aspectDensities));
        }};

        nullifier = new Nullifier("nullifier"){{
            requirements(Category.effect, with(
                    MeldItems.debris, 350,
                    MeldItems.carbolith, 450,
                    MeldItems.silver, 350,
                    MeldItems.resonarum, 450
            ));
            size = 5;
            health = 300;
        }};

        bruisekit = new Bruisekit("bruisekit"){{
            requirements(Category.effect, with(
                    MeldItems.debris, 60,
                    MeldItems.aspectPipe, 45,
                    MeldItems.resonarum, 80
            ));
            size = 2;

            consume(
                    new ConsumeLiquid(MeldLiquids.fumes, 0.5f)
            );
        }};

        gauze = new Gauze("gauze"){{
            requirements(Category.effect, with(
                    MeldItems.debris, 60,
                    MeldItems.aspectPipe, 45,
                    MeldItems.resonarum, 80
            ));
            size = 3;

            health = 640;
            propagateMaxRange = 16;

            recipies.addAll(
                    new SpoolRecipe(new ItemStack(MeldItems.resonarum, 2), 200)
            );

            consumeLiquid(MeldLiquids.fumes, 1f);
            consumeItem(MeldItems.resonarum, 2);
        }};

        chute = new Duct("chute"){{
            requirements(Category.distribution, with(MeldItems.debris, 1));
            health = 90;
            speed = 4f;
        }};

        chuteRouter = new DuctRouter("chute-router"){{
            requirements(Category.distribution, with(MeldItems.debris, 4));
            health = 90;
            speed = 4f;
            solid = false;
        }};
        chuteBridge = new DuctBridge("chute-bridge"){{
            requirements(Category.distribution, with(MeldItems.debris, 8));
            range = 5;
            health = 90;
            speed = 4f;
            solid = false;
            ((Duct) chute).bridgeReplacement = this;
        }};

        chuteOverflow = new PriorityInputSplitter("chute-overflow"){{
            requirements(Category.distribution, with(MeldItems.debris, 4));
            health = 90;
            speed = 4f;
            solid = false;
        }};

        unloadingHub = new Unloader("unloading-hub"){{
            requirements(Category.distribution, with(MeldItems.debris, 450));
            size = 3;
            health = 900;
            speed = 1f;

            solid = false;
        }};

        chuteJunction = new DuctJunction("chute-crossing"){{
            requirements(Category.distribution, with(MeldItems.debris, 7));
            health = 90;
            speed = 4f;
            solid = false;
            ((Duct) chute).junctionReplacement = this;
        }};

        //Meld blocks
        meldNode = new MeldNode("meld-node"){{
            health = 4000;
            size = 3;
        }};

        meldSuppressor = new MeldNode("meld-suppressor"){{
            health = 2500;
            armor = 50;

            size = 3;
        }};

        //Not as unkillable as it used to be but still strong asf
        meldSynapse = new MeldSynapse("meld-synapse"){{
            health = 400000;
            armor = 5000;

            priority = BuildingPriority.synapse;
            size = 5;
        }};

        meldAmplifier = new SonarSpire("meld-amplifier"){{
            requirements(Category.effect, with(MeldItems.larvalPlating, 2000));
            size = 5;
            health = 400000;
            armor = 5000;
            liquidCapacity = 300;

            statusDuration = 60 * 60;

            range = 300;

            status = MeldStatusEffects.amplified;
            ringColor = MeldPal.flamePink;

            consume(new ConsumeLiquid(MeldLiquids.meld, 1));
        }};

        meldCapsule = new ForceProjector("meld-capsule"){{
            requirements(Category.effect, with(MeldItems.larvalPlating, 350));
            size = 3;
            health = 1500;
            armor = 5;
            liquidCapacity = 120;

            consume(new ConsumeLiquid(MeldLiquids.meld, 1));
        }};

        carbonicBarrier = new Wall("stone-blocker"){{
            requirements(Category.defense, with(MeldItems.stonyParticulate, 15));
            health = 400;
            armor = 85;

            customShadow = true;
            squareSprite = false;
            floating = true;
        }};

        carbonicBarrierLarge = new Wall("stone-blocker-large"){{
            requirements(Category.defense, with(MeldItems.stonyParticulate, 60));
            size = 2;
            health = 400;
            armor = 85;

            customShadow = true;
            squareSprite = false;
            floating = true;
        }};

        crystalBarrier = new CrystalBarrier("blocker"){{
            health = 5000;
            requirements(Category.defense, with(MeldItems.meldShard, 50));
        }};

        crystalBarrierLarge = new CrystalBarrier("blocker-large"){{
            health = 20000;
            requirements(Category.defense, with(MeldItems.meldShard, 50));
            size = 2;
        }};

        pipeline = new Conduit("pipeline"){{
            requirements(Category.liquid, with(MeldItems.larvalPlating, 1));
            underBullets = false;
            liquidCapacity = 100;
            liquidPressure = 4;
        }};

        pipelineCrossing = new LiquidJunction("pipeline-crossing"){{
            requirements(Category.liquid, with(MeldItems.larvalPlating, 3));
            liquidCapacity = 100;
            liquidPressure = 4;

        }};

        pipelineRouter = new LiquidRouter("pipeline-router"){{
            requirements(Category.liquid, with(MeldItems.larvalPlating, 5));
            liquidCapacity = 200;
            liquidPressure = 4;

        }};

        pipelineBridge = new DirectionLiquidBridge("pipeline-overpass"){{
            requirements(Category.liquid, with(MeldItems.larvalPlating, 12));
            liquidCapacity = 100;
            liquidPressure = 4;
            range = 8;
        }};

        meldCultivator = new AttributeCrafter("meld-cultivator"){{
            requirements(Category.production, with(
                    MeldItems.larvalPlating, 120
            ));

            size = 3;

            attribute = Attribute.steam;
            baseEfficiency = 0;
            minEfficiency = 9;
            boostScale = 1f/9f;
            displayEfficiencyScale = 9;

            outputLiquid = new LiquidStack(MeldLiquids.meld, 3);
        }};

        meldCannon = new LiquidTurret("meld-cannon"){{
            requirements(Category.turret, with(MeldItems.stonyParticulate, 60));
            size = 3;
            reload = 20;
            range = 5 * 46;
            minWarmup = 0.6f;
            warmupMaintainTime = 60;

            health = 2000;

            cooldownTime = 360;

            destroyBulletSameTeam = true;
            destroyBullet = new ExplosionBulletType(){{
                hitEffect = despawnEffect = Fx.none;
                fragBullets = 5;
                fragBullet = new BulletType(){{
                    spawnUnit = MeldUnits.blob;
                }};
                spawnBullets.add(
                        new TransitionBulletType(){{
                            spawnUnit = MeldUnits.cannonOverseer;
                        }}
                );
            }};

            drawer = new DrawTurret(){{
                parts.addAll(
                        new RegionPart("-plunge"){{
                            progress = PartProgress.recoil;
                            mirror = false;
                            moveY = -4;
                        }},
                        new RegionPart("-barrel"){{
                            progress = PartProgress.warmup;
                            mirror = false;
                            moveY = 4;

                            moves.add(new PartMove(){{
                                    progress = PartProgress.reload.inv().curve(Interp.pow2Out);
                                    y = -2;
                                  }}
                            );

                            children.addAll(
                                    //Iris
                                    new HaloPart(){{
                                        progress = PartProgress.warmup.curve(Interp.pow2In);
                                        tri = true;

                                        haloRadius = 5;
                                        radius = 8;
                                        radiusTo = 0;
                                        triLength = 4;
                                        triLengthTo = 0;
                                        shapes = 4;

                                        color = MeldPal.flamePink.cpy().a(0);
                                        colorTo = MeldPal.flamePink;

                                        haloRotateSpeed = 2;
                                        layer = Layer.effect;
                                    }},

                                    //Pupil
                                    new ShapePart(){{
                                        progress = PartProgress.warmup;
                                        hollow = true;
                                        circle = true;
                                        x = 0;
                                        y = -3;

                                        radius = 0;
                                        radiusTo = 1;
                                        moveY = 2;
                                        layer = Layer.effect;

                                        color = MeldPal.flamePink;
                                    }},
                                    //Eepy eye
                                    new RegionPart(){{
                                        name = Meld.prefix("square");
                                        progress = PartProgress.warmup;
                                        growProgress = PartProgress.heat.inv().compress(0.5f, 1).curve(Interp.pow2In);
                                        mirror = false;
                                        growX = 1f;

                                        x = 0;
                                        y = -3;

                                        xScl = 0.25f;
                                        yScl = 0.25f;

                                        layer = Layer.effect;

                                        color = MeldPal.flamePink;
                                        colorTo = MeldPal.flamePink.cpy().a(0);

                                        moves.add(new PartMove(){{
                                            progress = PartProgress.warmup;
                                            y = 2;
                                        }});
                                    }}
                            );
                        }},
                        new RegionPart(){{
                            progress = PartProgress.warmup;
                            suffix = "-wing";
                            mirror = true;

                            moveRot = 25;
                            moveY = -4;
                            moveX = -2;
                        }}
                );
            }};

            ammoTypes.put(
                    MeldLiquids.meld,
                    new BasicBulletType(){{
                        speed = 5;

                        sprite = Meld.prefix("glob");
                        width = 12;
                        height = 16;

                        lifetime = 48;

                        damage = 15;
                        splashDamage = 25;
                        splashDamageRadius = 16;

                        status = MeldStatusEffects.interference;
                        statusDuration = 90;

                        knockback = 3;

                        despawnEffect = hitEffect = Fx.explosion;
                    }}
            );

            consume(new ConsumeLiquid(MeldLiquids.meld, 1));
        }};

        meldMortar = new LiquidTurret("meld-mortar"){{
            requirements(Category.turret, with(MeldItems.stonyParticulate, 60));
            size = 3;
            reload = 120;
            range = 240;

            health = 2000;

            drawer = new DrawTurret(){{
                parts.addAll();
            }};

            ammoTypes.put(
                    MeldLiquids.meld,
                    new BasicBulletType(){{
                        speed = 2;

                        lifetime = 120;

                        damage = 15;
                        splashDamage = 120;
                        splashDamageRadius = 36;

                        knockback = 2;

                        scaleLife = true;

                        sprite = "shell";
                        width = 6;
                        height = 8;
                        despawnEffect = hitEffect = Fx.explosion;
                    }}
            );

            consume(new ConsumeLiquid(MeldLiquids.meld, 1));
        }};

        jillaCoffer = new Wall("jilla-coffer"){{
            size = 2;
            health = 400;
            destroyBulletSameTeam = true;
            destroyBullet = new ExplosionBulletType(){{
                hitEffect = despawnEffect = Fx.none;
                fragBullets = 3;
                fragBullet = new BulletType(){{
                    spawnUnit = MeldUnits.jilla;
                }};
            }};
        }};

        craigCoffer = new LiquidTurret("craig-coffer"){{
            size = 2;
            health = 400;
            destroyBulletSameTeam = true;
            destroyBullet = new ExplosionBulletType(){{
                hitEffect = despawnEffect = Fx.none;
                fragBullets = 3;
                fragBullet = new BulletType(){{
                    spawnUnit = MeldUnits.craig;
                }};
            }};

            loopSound = Sounds.none;
            //shootSound = Sounds.pew;

            reload = 5;
            recoil = 1.5f;
            rotateSpeed = 6;
            range = 80;
            shootY = 5;
            shootEffect = Fx.shootSmall;

            ammoTypes.put(
                    MeldLiquids.meld,
                    new BasicBulletType(){{
                        sprite = Meld.prefix("glob");
                        speed = 4;
                        lifetime = 20;
                        width = 6;
                        height = 9;
                        damage = 5;
                        knockback = 1.5f;
                        hitEffect = despawnEffect = Fx.none;
                        impact = true;
                    }}
            );
        }};

        braigCoffer = new LiquidTurret("braig-coffer"){{
            size = 3;
            health = 1200;
            destroyBulletSameTeam = true;
            destroyBullet = new ExplosionBulletType(){{
                hitEffect = despawnEffect = Fx.none;
                fragBullets = 2;
                fragBullet = new BulletType(){{
                    spawnUnit = MeldUnits.braig;
                }};
            }};

            loopSound = Sounds.none;
            //shootSound = Sounds.pew;

            reload = 300;
            recoil = 3;
            rotateSpeed = 5;
            range = 220;
            shootY = 5;
            shootEffect = Fx.shootSmall;

            moveWhileCharging = false;
            predictTarget = false;

            shoot = new ShootSpread(){{
                firstShotDelay = 35;
                shotDelay = 5;
                shots = 8;
                spread = 0;
            }};

            ammoTypes.put(
                    MeldLiquids.meld,
                    new BasicBulletType(){{
                        sprite = Meld.prefix("clump");
                        speed = 4;
                        lifetime = 55;
                        width = 5;
                        height = 14;

                        pierce = true;
                        pierceCap = 2;

                        damage = 10;
                        splashDamageRadius = 25;

                        knockback = 4f;

                        hitEffect = despawnEffect = Fx.explosion;

                        impact = true;
                    }}
            );
        }};
    }
}
