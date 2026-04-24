package meld.content;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import meld.*;
import meld.entities.bullet.OutflowBulletType;
import meld.entities.bullet.TransitionBulletType;
import meld.fluid.AspectGroup;
import meld.graphics.*;
import meld.world.blocks.*;
import meld.world.blocks.consumers.ConsumeItemsBoost;
import meld.world.blocks.consumers.ConsumePowerRecipe;
import meld.world.blocks.consumers.StupidConsumeAspects;
import meld.world.blocks.crafting.ModularCrafter;
import meld.world.blocks.crafting.RecipeCrafter;
import meld.world.blocks.crafting.StorageIncinerator;
import meld.world.blocks.crafting.recipe.ItemRecipe;
import meld.world.blocks.crafting.recipe.SpoolRecipe;
import meld.world.blocks.crafting.modules.*;
import meld.world.blocks.crafting.recipe.TimedRecipe;
import meld.world.blocks.defense.FrictionPad;
import meld.world.blocks.defense.LakeRim;
import meld.world.blocks.fluid.*;
import meld.world.blocks.items.PriorityInputSplitter;
import meld.world.blocks.power.ConsumeThermal;
import meld.world.blocks.producer.ProduceItem;
import meld.world.blocks.producer.ProduceLiquid;
import meld.world.blocks.production.GrindingQuary;
import meld.world.blocks.production.SingleBeamDrill;
import meld.world.blocks.units.LaunchStation;
import meld.world.meta.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Statusc;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Door;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.power.Battery;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeItemList;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;

import static mindustry.type.ItemStack.with;

public class MeldBlocks {

    //Strata blocks first
    public static Block chute, chuteRouter, chuteBridge, chuteJunction, chuteOverflow, platedChute, unloadingHub;

    public static Block sonarSpire, movementAnchor, nullifier,
            //Bruisekit: Targets largest, highest hp blocks, functional blocks first, continuously heals

            //Gauze chainheals smallest, lowest hp blocks, low target prio blocks first.

            //Suture shoots healing needles in a cone at the closest damaged block. Impales enemies, causing them to take constant chip damage and be sedated.
            bruisekit, gauze, suture;

    //Core Blocks
    public static Block coreRaft, buffer;

    //Core Incinerator does what core incinerator does... it core incinerator

    public static ItemIncinerator aspectIncinerator;

    public static Block aetherAccumulator, crystalCracker, elementalBlaster, excavationQuarry, pneumaticPulsear,
            earthboundInfuser, fumehood, sharkFactory;

    public static LaunchStation launchStation;

    //Crafters
    public static RecipeCrafter gasKiln, metalworks, rotaryKiln, pneumaticExtruder;

    public static AttributeCrafter crushWeaver;
    public static RecipeCrafter stormIris;

    //power blocks
    public static Block conductivePile, substation, aspectDischarger;

    public static Block channelNode, channelHub, channelFace, aspectOutlet, aspectChannel, channelDirector, channelVent, manualValve, intakeValve, valveController, pipebox;

    public static Block sunder, shredstorm, molotov, vivisection, vinca, vivalo;

    public static Block silverHusk, shadesteelShingles, gateSpike, lakeRim, frictionPad;

    //Meld blocks
    public static Block pipeline, pipelineRouter, pipelineCrossing, pipelineBridge,

    //More for areas outside glacier
            meldCannon, meldMortar, meldNailgun,
            jillaCoffer, billaCoffer, craigCoffer, braigCoffer,
            meldCultivator, meldNode, meldSuppressor, meldSynapse,

    //Mostly for glacier
            meldAmplifier, meldCapsule;
    public static Block crystalBarrier, crystalBarrierLarge,
            carbonicBarrier, carbonicBarrierLarge
            ;

    public static float outletRate = 100f/60f;

    public static Item item(String name){
        return new Item(name);
    }

    public static void load(){
        int channelHealth = 100;

        channelFace = new FlexibleSizeJunction("channel-face"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 2
            ));
            health = channelHealth;
            solid = false;
            placeableLiquid = true;
            usePassedOffset = false;
        }};

        channelNode = new LiquidRouter("channel-node"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 5
            ));
            health = channelHealth;

            liquidCapacity = 200;
            solid = false;
            placeableLiquid = true;
        }};

        aspectOutlet = new RecipeCrafter("aspect-outlet"){{
            outputDirections.putAll(MeldLiquids.aspect, new int[]{0}, MeldLiquids.boundAspect, new int[]{0}, MeldLiquids.stormingAspect, new int[]{0});

            requirements(Category.liquid, with(
                    MeldItems.debris, 7
            ));

            health = channelHealth * 2;

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
                    new DrawLiquidTile(){{
                        drawLiquid = MeldLiquids.stormingAspect;
                    }},
                    new DrawRegion(),
                    new DrawSideRegion()
            );

            liquidCapacity = 100;

            //Bit sloppy but it'll work
            Vars.content.liquids().each(l -> {
                if(AspectGroup.aether.stats.containsKey(l)) inputLiquids.add(l);
            });
            Vars.content.liquids().each(l -> {
                if(AspectGroup.aspect.stats.containsKey(l)) outputLiquids.add(l);
            });

            inputLiquidSlots = 1;
            outputLiquidSlots = 1;

            recipes.addAll(
                    new TimedRecipe(){{
                        float multi = AspectGroup.aether.getEfficiency(MeldLiquids.aether);
                        float density = AspectGroup.aether.getDensity(MeldLiquids.aether);
                        consumers.add(new ConsumeLiquid(MeldLiquids.aether, outletRate/density/10 * multi));
                        producers.add(new ProduceLiquid(MeldLiquids.aspect, outletRate * multi));
                    }},
                    new TimedRecipe(){{
                        float multi = AspectGroup.aether.getEfficiency(MeldLiquids.pollutantMixture);
                        float density = AspectGroup.aether.getDensity(MeldLiquids.pollutantMixture);
                        consumers.add(new ConsumeLiquid(MeldLiquids.pollutantMixture, outletRate/density/10 * multi));
                        producers.add(new ProduceLiquid(MeldLiquids.boundAspect, outletRate * multi));
                    }},
                    new TimedRecipe(){{
                        float multi = AspectGroup.aether.getEfficiency(MeldLiquids.thunderingAether);
                        float density = AspectGroup.aether.getDensity(MeldLiquids.thunderingAether);
                        consumers.add(new ConsumeLiquid(MeldLiquids.thunderingAether, outletRate/density/10 * multi));
                        producers.add(new ProduceLiquid(MeldLiquids.stormingAspect, outletRate * multi));
                    }}
            );
        }};

        aspectChannel = new VisualAspectPipe("aspect-channel"){{
            requirements(Category.liquid, with(
                    MeldItems.annealedSilver, 4,
                    MeldItems.glassMallows, 4
            ));
            underBullets = false;
            leaks = false;
            health = channelHealth * 2;
            armor = 2;
            insulated = true;

            placeableLiquid = true;

            liquidCapacity = 80;
            size = 1;
            botColor = Color.white;
            junctionReplacement = channelFace;
        }};

        channelHub = new LiquidRouter("channel-hub"){{
            requirements(Category.liquid, with(MeldItems.debris, 80, MeldItems.shadesteel, 120));
            size = 4;

            health = channelHealth * 16;
            armor = 8;
            liquidCapacity = 100 * 100;
        }};

        channelDirector = new ChannelDirector("channel-director"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 5
            ));

            health = channelHealth;

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
            health = channelHealth;
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

            health = channelHealth;

            solid = false;
            placeableLiquid = true;
        }};

        intakeValve = new ChannelValve("intake-valve"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 80,
                    MeldItems.shadesteel, 48
            ));
            size = 3;

            health = channelHealth * 10;
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

            health = channelHealth;

            solid = false;
            placeableLiquid = true;
        }};

        pipebox = new Pipebox("pipebox") {{
            requirements(Category.liquid, with(
                    MeldItems.debris, 2,
                    MeldItems.aspectPipe, 4
            ));
            size = 1;

            health = channelHealth;

            solid = false;
            placeableLiquid = true;
        }};

        sunder = new ItemTurret("sunder"){{
            requirements(Category.turret, with(
                    MeldItems.debris, 45,
                    MeldItems.carbolith, 60
            ));
            size = 2;
            health = 640;
            range = 200;
            fogRadiusMultiplier = 0.25f;

            buildTime = 45;

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

            ammoTypes.putAll(
                    MeldItems.debris,
                    MeldBullets.sunderDebris,
                    MeldItems.glassMallows,
                    MeldBullets.sunderGlass
            );
        }};

        shredstorm = new ItemTurret("shredstorm"){{
            requirements(Category.turret, with(
                    MeldItems.debris, 35,
                    MeldItems.silver, 50
            ));
            size = 2;
            health = 450;
            range = 200;

            buildTime = 30;

            fogRadiusMultiplier = 0.25f;
            reload = 120;
            shootEffect = Fx.shootBig;
            shootWarmupSpeed = 0.25f;
            minWarmup = 0.7f;

            rotateSpeed = 15;

            velocityRnd = 0.3f;
            recoil = 1.5f;
            shootCone = 25;

            shootSound = Sounds.shootFuse;

            rotate = quickRotate = false;

            shoot = new ShootBarrel(){{
                shotDelay = 0;
                shots = 7;

                float[] spreadCone = new float[]{0, 3, 12, 25};
                barrels = new float[]{
                        0, 0, spreadCone[0],
                        0, 0, -spreadCone[2],
                        0, 0, spreadCone[1],
                        0, 0, -spreadCone[1],
                        0, 0, spreadCone[3],
                        0, 0, spreadCone[2],
                        0, 0, -spreadCone[3]
                };
            }};

            inaccuracy = 2;

            shootY = 7;

            ammoPerShot = 4;
            maxAmmo = 16;

            drawer = new DrawTurret(){{
                parts.addAll(
                        new RegionPart("-plate"){{
                            progress = PartProgress.warmup;
                            mirror = true;
                            under = false;
                            moveX = 1;
                            moveY = -2.55f;
                            moveRot = -15;
                        }},
                        new RegionPart("-plate"){{
                            progress = PartProgress.warmup;
                            mirror = true;
                            under = false;
                            moveX = -1;
                            moveY = -3.55f;
                            moveRot = 15;
                        }},
                        new RegionPart("-barrel"){{
                            progress = PartProgress.warmup;
                            moveY = 1;
                            under = true;

                            moves.addAll(
                                    new PartMove(PartProgress.recoil, 0, -1.25f, 0)
                            );
                        }}
                );
            }};

            ammoTypes.putAll(
                    MeldItems.debris, MeldBullets.shredDebris,
                    MeldItems.silver, MeldBullets.shredSilver,
                    MeldItems.annealedSilver, MeldBullets.shredSilver

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
                layer = Layer.bullet + 1;
                //HHJKGHJGJK.
                collidesTiles = true;
                collides = true;
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

                trailLength = 8;
                speed = 3.5f;
                damage = 1;
                lifetime = 46 * 2;
                width = 18;
                height = 24;
                status = MeldStatusEffects.aspectBurn;
                statusDuration = 300;
                frontColor = Color.white;
                backColor = Color.valueOf("cbdbfc");

                splashDamage = 40;
                splashDamageRadius = 43;

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

                outflowBullet = new TransitionBulletType(){{
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

            consume(
                    new StupidConsumeAspects(outletRate, AspectGroup.aspect)
            );
        }};

        vinca = new ItemTurret("vinca"){{
            requirements(Category.turret, with(MeldItems.debris, 25, MeldItems.silver, 25, MeldItems.quartzStrata, 30));
            size = 2;
            health = 850;
            range = 120;

            liquidCapacity = outletRate * 60 * 4;
            fogRadiusMultiplier = 0.25f;
            reload = 30;

            shootEffect = Fx.shootBig;
            shootWarmupSpeed = 0.09f;
            minWarmup = 0.7f;

            velocityRnd = 0.2f;
            recoil = 1.5f;
            inaccuracy = 2;
            shootCone = 5;

            ammoPerShot = 2;
            shootY = 8;

            rotate = quickRotate = false;

            consume(new StupidConsumeAspects(outletRate * 1, AspectGroup.aspect));

            ammo(
                    MeldItems.quartzStrata, MeldBullets.vincaQuartz
            );
        }};

        vivalo = new ItemTurret("vivalo"){{
            requirements(Category.turret, with(MeldItems.debris, 20, MeldItems.electrumSheet, 45, MeldItems.vitricMesh, 12));
            size = 3;
            health = 520;
            range = 160;
            fogRadiusMultiplier = 0.25f;
            reload = 240;

            shootWarmupSpeed = 0.12f;
            minWarmup = 0.7f;

            recoil = 0;
            shootCone = 5;

            rotateSpeed = 12;

            shootSound = Sounds.loopSpray;
            moveWhileCharging = true;

            rotate = quickRotate = false;
            shoot = new ShootSpread(){{
                spread = 0;
                shots = 6;
                shotDelay = 8;
                firstShotDelay = 30;
            }};

            inaccuracy = 0;
            shootY = 9;

            ammoPerShot = 10;
            drawer = new DrawTurret(){{
                parts.addAll();
            }};

            /*
            ammo(
                    MeldItems.resonarum, 1,
                    MeldItems.dissonitre, 1,
                    MeldItems.vitricMesh, 1,
                    MeldItems.quartzStrata, 1,
                    MeldItems.iampsi, 1,
                    MeldItems.glassMallows, 1
            );

             */
        }};

        vivisection = new ItemTurret("vivisection"){{
            requirements(Category.turret, with(MeldItems.debris, 200, MeldItems.silver, 320, MeldItems.resonarum, 60));
            size = 4;
            health = 2640;
            range = 252;

            liquidCapacity = outletRate * 60 * 4;
            fogRadiusMultiplier = 0.25f;
            reload = 120;

            shootEffect = Fx.shootBig;
            shootWarmupSpeed = 0.09f;
            minWarmup = 0.7f;

            velocityRnd = 0.2f;
            recoil = 1.5f;
            inaccuracy = 2;
            shootCone = 5;

            ammoPerShot = 12;
            shootY = 8;

            rotate = quickRotate = false;

            shoot = new ShootSpread(){{
                shots = 1;
                firstShotDelay = 60;
            }};

            consume(new StupidConsumeAspects(outletRate * 3, AspectGroup.aspect));

            drawer = new DrawTurret(){{

                parts.addAll(
                    new RegionPart("-jaw"){{
                        mirror = true;
                        x = 4.5f;
                        y = 2;
                        moveRot = 180;
                        progress = PartProgress.charge.compress(0, 0.5f);
                        moves.add(
                                new PartMove(PartProgress.charge.compress(0.5f, 1).curve(Interp.pow2In),-0.75f, 0.5f, 0));
                        }}
                );

                //Define the centers for the center of rotation and starting point
                Vec2 center = new Vec2(4.5f, 2);

                //Start offset should be endpoint mirrored across the center
                Vec2 startOffset = new Vec2(4, -20).scl(0.25f);
                Vec2 endOffset = new Vec2(-18, 20).scl(0.25f);
                float toMove = Angles.backwardDistance(startOffset.angle(), endOffset.angle());

                float startRad = startOffset.len();
                float endRad = endOffset.len();

                //Trust me the constant has a reason for it
                DrawPart.PartProgress base = DrawPart.PartProgress.charge.compress(0, 0.5f);
                DrawPart.PartMove SPINNNNNNNNBABYYYSPINSPINBABYYEEEEEEEEEE =
                        new DrawPart.PartMove(){{
                            rot = 360 * 5;
                            progress = DrawPart.PartProgress.charge.compress(0.6f, 1).curve(Interp.pow5In);
                        }};
                for(int i = 0; i < 2; i++){
                    int sign = i == 0 ? 1 : -1;
                    parts.add(
                            new RegionPart(){{
                                mirror = false;
                                name = Meld.prefix("sawblade-large" + (sign == 1 ? "-r" : "-l"));
                                clampProgress = false;

                                //Starting pos for the sawblades
                                x = center.x * sign;
                                y = center.y;

                                moveX = moveY = 0;
                                growX = growY = 1;
                                xScl = yScl = 0;
                                growProgress = PartProgress.charge.compress(0, 0.5f).curve(Interp.pow2In);

                                moves.addAll(
                                        new PartMove(){{
                                            x = sign;
                                            progress = p -> {
                                                //Go from a clockwise starting position to a counterclockwise position
                                                Tmp.v1.trns(Angles.moveToward(startOffset.angle(), endOffset.angle(), -toMove * base.get(p)), Mathf.lerp(startRad, endRad, base.get(p)));
                                                return Tmp.v1.x;
                                            };
                                        }},
                                        new PartMove(){{
                                            y = 1;
                                            progress = p -> {
                                                //counterclockwise rotation from start till end
                                                Tmp.v1.trns(Angles.moveToward(startOffset.angle(), endOffset.angle(), -toMove * base.get(p)), Mathf.lerp(startRad, endRad, base.get(p)));
                                                return Tmp.v1.y;
                                            };
                                        }},
                                        new PartMove(){{
                                            rot = -180 * sign;
                                            progress = base.inv();
                                        }},
                                        SPINNNNNNNNBABYYYSPINSPINBABYYEEEEEEEEEE
                                );
                            }}
                    );
                }

                parts.addAll(
                        new RegionPart(){{
                            name = Meld.prefix("sawblade-large-glow");
                            x = 0;
                            y = 9;
                            blending = Blending.additive;
                            color = Color.white.cpy().a(0);
                            colorTo = Color.valueOf("6ed88e");
                            progress = PartProgress.charge.compress(0.5f, 1).curve(Interp.pow2Out).slope();
                            outline = false;
                            moves.addAll(
                                    SPINNNNNNNNBABYYYSPINSPINBABYYEEEEEEEEEE
                            );
                        }},
                        new RegionPart("-plate"){{
                            mirror = true;
                            moveX = 0.5f;
                            moveY = -0.5f;
                            moveRot = 10;
                            progress = PartProgress.warmup;

                            moves.addAll(
                                    new PartMove(PartProgress.recoil,1.25f, -1.25f, 15),
                                    new PartMove(PartProgress.charge.compress(0.5f, 1).curve(Interp.pow2In),-0.75f, 0, 25)
                            );
                        }},
                        new RegionPart("-body"){{
                            progress = PartProgress.charge.compress(0.75f, 1).curve(Interp.pow5In);
                            moveY = 0.75f;
                            children.addAll(
                                    new RegionPart("-body-glow"){{
                                        blending = Blending.additive;
                                        outline = false;
                                        color = Pal.turretHeat;
                                        colorTo = Color.valueOf("6ed88e");
                                        progress = PartProgress.charge.compress(0.5f, 1).curve(Interp.pow5In);
                                    }}
                            );
                        }}
                );
            }};

            ammoTypes.putAll(
                    MeldItems.resonarum,
                    new BasicBulletType(5, 160, Meld.prefix("sawblade-large")){{
                        frontColor = Color.white;
                        backColor = Color.clear;
                        pierce = true;
                        pierceCap = 5;
                        pierceDamageFactor = 0.05f;
                        lifetime = 52;
                        knockback = 120;
                        impact = true;
                        spin = 300;
                        width = height = 24 * 2;
                        hitShake = 10.75f;
                        shrinkInterp = Interp.pow10In;
                        shrinkX = shrinkY = 0;
                        ammoMultiplier = 1;

                        hitEffect = new ParticleEffect(){{
                            lifetime = 15;
                            line = true;
                            lenTo = 8;
                            strokeFrom = 2;
                            strokeTo = 0.5f;
                            interp = Interp.pow5Out;
                            sizeInterp = Interp.pow5Out;
                            baseRotation = 25;
                            cone = 35;
                            colorFrom = Color.valueOf("85c799");
                            colorTo = Color.valueOf("4bb66b");
                        }};
                        despawnEffect = Fx.none;
                        intervalBullet = new ExplosionBulletType(){{
                            killShooter = false;
                            fragBullets = 2;
                            fragRandomSpread = 15;
                            fragSpread = 35;
                            fragAngle = 180;
                            fragVelocityMin = 0.5f;
                            fragVelocityMax = 1;
                            fragBullet = new BulletType(){{
                                spawnBullets.addAll(
                                        new TransitionBulletType(){{
                                            fragVelocityMin = 1;
                                            fragBullets = 1;
                                            fragBullet = new BulletType(2.5f, 5) {{

                                                drag = 0.2f;
                                                hitEffect = despawnEffect = Fx.none;
                                                lifetime = 30;
                                                bulletInterval = 5;
                                                intervalBullets = 1;
                                                lightRadius = 0;
                                                parts.addAll(
                                                        new RegionPart() {{
                                                            name = Meld.prefix("crystalline-smog");
                                                            outline = false;
                                                            color = Color.white.cpy().a(0f);
                                                            colorTo = Color.white.cpy().a(0.1f);
                                                            progress = PartProgress.life.curve(Interp.slope);
                                                            moveRot = 360;
                                                            blending = Blending.additive;
                                                            layer = Layer.flyingUnitLow;

                                                            xScl = yScl = 0.5f;
                                                            growX = growY = 0.5f;
                                                            growProgress = PartProgress.life.compress(0, 1).curve(Interp.pow2In);
                                                        }}
                                                );

                                                intervalBullet = new TransitionBulletType() {{
                                                    fragBullets = 2;
                                                    fragVelocityMin = 0.5f;
                                                    fragVelocityMax = 1;

                                                    fragBullet = new BasicBulletType(4.2f, 2, Meld.prefix("dual-spike")) {
                                                        @Override
                                                        public void update(Bullet b){
                                                            if(b.stickyTarget instanceof Statusc s){
                                                                s.apply(MeldStatusEffects.lacerated, 60);
                                                            }
                                                        }
                                                        {
                                                        width = 6;
                                                        height = 2;
                                                        frontColor = MeldPal.resoShardFront;
                                                        backColor = MeldPal.resoShardBack;
                                                        drag = 0.15f;
                                                        lifetime = 30;

                                                        splashDamage = 5;
                                                        splashDamageRadius = 8;
                                                        hittable = false;
                                                        spin = 15;
                                                        hitEffect = despawnEffect = Fx.none;
                                                        status = MeldStatusEffects.lacerated;
                                                        statusDuration = 20;
                                                        sticky = true;
                                                        stickyExtraLifetime = 60;

                                                        lightRadius = 0;

                                                        float xScale = width / 20f;
                                                        float yScale = height / 20;

                                                        parts.addAll(
                                                                new RegionPart() {{
                                                                    name = Meld.prefix("dual-spike");
                                                                    outline = false;
                                                                    rotation = 60;
                                                                    xScl = xScale;
                                                                    yScl = yScale;
                                                                    moveRot = 15 * 60;
                                                                    progress = PartProgress.life;

                                                                    growX = -xScale;
                                                                    growY = -yScale;
                                                                    growProgress = PartProgress.life.curve(Interp.pow2In);
                                                                }},
                                                                new RegionPart() {{
                                                                    name = Meld.prefix("dual-spike");
                                                                    outline = false;
                                                                    rotation = 120;
                                                                    xScl = xScale;
                                                                    yScl = yScale;
                                                                    moveRot = 7.5f * 60;
                                                                    progress = PartProgress.life;
                                                                    color = MeldPal.resoShardBack;

                                                                    growX = -xScale;
                                                                    growY = -yScale;
                                                                    growProgress = PartProgress.life.curve(Interp.pow5In);
                                                                }}
                                                        );
                                                    }};
                                                }};
                                            }};
                                        }}
                                );
                                damage = 5;
                                speed = 1.5f;
                                drag = 0.15f;
                                lifetime = 120;
                                homingPower = 0.05f;
                                frontColor = Color.valueOf("85c799");
                                pierce = true;
                                status = MeldStatusEffects.lacerated;
                                statusDuration = 15;
                                hitEffect = Fx.none;
                                despawnEffect = Fx.none;
                                absorbable = hittable = reflectable = false;
                                parts.addAll(
                                        new RegionPart(){{
                                            name = Meld.prefix("crystalline-smog");
                                            outline = false;
                                            color = Color.clear;
                                            colorTo = Color.white.cpy().a(1f);
                                            progress = PartProgress.life.compress(0, 0.65f).curve(Interp.pow5Out).curve(Interp.slope);
                                            moveRot = 360;
                                            blending = Blending.additive;

                                            growX = growY = 2;
                                            growProgress = PartProgress.life.compress(0, 0.65f).curve(Interp.pow2In);
                                        }},
                                        //less glowey layer
                                        new RegionPart(){{
                                            name = Meld.prefix("crystalline-smog");
                                            outline = false;
                                            color = Color.clear;
                                            colorTo = Color.white.cpy().a(0.6f);
                                            progress = PartProgress.life.curve(Interp.pow5Out).curve(Interp.slope);
                                            moveRot = 180 * 1.5f;
                                            layer = Layer.flyingUnitLow;

                                            growX = growY = 4;
                                            growProgress = PartProgress.life.compress(0, 0.8f).curve(Interp.pow2Out);
                                        }}
                                );
                            }};
                        }};
                        bulletInterval = 4;
                        intervalBullets = 1;
                        intervalSpread = 120;
                        intervalRandomSpread = 15;
                    }}
            );
        }};

        silverHusk = new RegenProjector("silver-husk"){{
            requirements(Category.defense, with(MeldItems.debris, 15, MeldItems.annealedSilver, 16));
            health = 450;
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

            consume(new StupidConsumeAspects(outletRate/2, AspectGroup.aspect));
        }};

        shadesteelShingles = new Wall("shadesteel-shingles"){{
            requirements(Category.defense, with(MeldItems.shadesteel, 64));
            size = 2;
            health = 2000;
            buildCostMultiplier = 2f;
        }};

        gateSpike = new Door("gate-spike"){{
            requirements(Category.defense, with(MeldItems.debris, 4));
            size = 1;
            health = 120;
        }};

        lakeRim = new LakeRim("lake-rim"){{
            requirements(Category.defense, with(MeldItems.debris, 4));
            size = 1;
            health = 120;
        }};

        frictionPad = new FrictionPad("friction-pad"){{
            requirements(Category.units, with(MeldItems.quartzStrata, 1));
            size = 1;
            health = 40;
        }};


        coreRaft = new CoreRaft("core-raft"){{
            requirements(Category.effect, with(
                    MeldItems.debris, 900
            ));
            size = 3;
            health = 4000;

            lightRadius = 360;
            fogRadius = 40;

            itemCapacity = 900;

            unitCapModifier = 6;

            unitType = MeldUnits.bulbhead;
            solid = false;
        }};

        buffer = new StorageBlock("buffer"){{
            requirements(Category.effect, with(MeldItems.debris, 120, MeldItems.cruciblePlating, 200));
            size = 2;
            health = 800;
            armor = 15;

            itemCapacity = 150;
        }};

        aspectIncinerator = new StorageIncinerator("aspect-incinerator"){{
            requirements(Category.effect, with(MeldItems.debris, 40));
            health = 120;
            liquidCapacity = 10;
            itemCapacity = 10;

            consume(new StupidConsumeAspects(outletRate/10, AspectGroup.aspect));
        }};

        aetherAccumulator = new AttributeCrafter("aether-accumulator"){{
            requirements(Category.production, with(
                    MeldItems.debris, 40
            ));
            size = 3;

            health = 300;

            buildTime = 180;

            attribute = MeldAttributes.aetherAttr;
            baseEfficiency = 0;
            minEfficiency = 8.9f;
            maxBoost = 2;
            boostScale = 1/9f;
            liquidCapacity = 300;

            outputLiquid = new LiquidStack(MeldLiquids.aether, 1);
        }};

        crystalCracker = new AttributeCrafter("crystal-cracker"){{
            requirements(Category.production, with(
                    MeldItems.debris, 40,
                    MeldItems.dissonitre, 40
            ));
            size = 2;

            health = 600;

            attribute = MeldAttributes.soilAttr;
            baseEfficiency = 0;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(MeldLiquids.thunderingAether),
                    new DrawGlowRegion(),
                    new DrawRegion()
            );

            craftTime = 60;
            consumeItem(MeldItems.dissonitre, 1);
            consume(new ConsumeLiquid(MeldLiquids.thunderingAether, 0){{
                optional = true;
            }});
            outputLiquid = new LiquidStack(MeldLiquids.thunderingAether, 2/6f);
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

            buildTime = 90;

            liquidCapacity = outletRate * 60 * 4;

            optionalBoostIntensity = 2;

            sparkColor = Color.valueOf("8cc7ee");
            boostHeatColor = Color.valueOf("ecb6eb");

            sparks = 24;
            sparkLife = 15;
            sparkSpread = 25;

            drillMultipliers.put(
                    MeldItems.clayMallows, 0.5f
            );
            drillMultipliers.put(
                    MeldItems.resonarum, 0.5f
            );
            drillMultipliers.put(
                    MeldItems.electrumSheet, 2
            );
            drillMultipliers.put(
                    MeldItems.likestoneSediments, 0.5f
            );

            consume(new StupidConsumeAspects(
                    outletRate, AspectGroup.aspect
                    ){{
                        optional = booster = false;
                    }}
            );

            consume(new ConsumeLiquid(
                    MeldLiquids.meld, 1
            ){{
                optional = true;
                booster = true;
            }});
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
            hookAll(BlockEvent.Defaults.proximityUpdate,
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
            );

            modules.addAll(
                    new ProduceLiquidModule(new LiquidStack(MeldLiquids.pollutantMixture, 1), 0),
                    new ProduceLiquidModule(new LiquidStack(MeldLiquids.fumes, 1), 1)
            );
        }};

        excavationQuarry = new GrindingQuary("excavation-quarry"){{
            requirements(Category.production, with(MeldItems.debris, 250, MeldItems.quartzStrata, 120));
            size = 5;

            consume(new StupidConsumeAspects(outletRate * 4, AspectGroup.aspect));
            consume(new ConsumeItemList(){{
                optional = booster = true;
                setMultipliers(
                        MeldItems.quartzStrata, 1.5f,
                        MeldItems.iampsi, 3f
                );
            }}
            );
            consume(
                    new ConsumeItemsBoost(with(MeldItems.gunpowder, 10), 2){{
                        optional = booster = true;
                    }}
            );
            consume(
                    new StupidConsumeAspects(outletRate, AspectGroup.aqua, 5){{
                        optional = booster = true;
                    }}
            );
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
            selfDamage = 250;

            transformItems.putAll(
                    MeldItems.clayMallows, MeldItems.glassMallows,
                    MeldItems.tenbris, MeldItems.shadesteel
            );

            liquidCapacity = 50;


            consume(new StupidConsumeAspects(outletRate/2, AspectGroup.outlet));
        }};

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


            //Should make all the modules trigger in order
            hookAll(BlockEvent.Defaults.proximityUpdate,
                    new AttributeModule(){{
                        attribute = Attribute.steam;
                        baseEfficiency = 0;
                        minEfficiency = 1;
                        boostScale = 1f/9f;

                        efficiencyPin = 0;
                    }}
            );

            ItemRecipe carbolith = new ItemRecipe(with(MeldItems.debris, 1), with(MeldItems.carbolith, 1));

            modules.addAll(
                    new ProduceLiquidModule(new LiquidStack(MeldLiquids.fumes, 2f), 0),
                    new GateModule(1, new GateModule.RecipeCondition(carbolith)),
                    new ConsumeLiquidModule(LiquidStack.with(MeldLiquids.fumes, 1, MeldLiquids.aspect, outletRate * 2), 1, 2),
                    new RecipeCraftingModule(){{
                        efficiencyPin = 2;
                        progressPin = 5;
                        craftTime = 12;
                        recipe = carbolith;
                    }}
            );


            /*

            displayEfficiencyScale = 9;
            craftTime = 60/5f;
             */
        }};

        gasKiln = new RecipeCrafter("gas-kiln"){{
            requirements(Category.crafting, with(MeldItems.debris, 80));
            size = 3;

            hasItems = true;
            hasLiquids = true;
            liquidCapacity = outletRate * 60 * 2;

            itemCapacity = 10;

            inputLiquids.addAll(MeldLiquids.aspect, MeldLiquids.boundAspect, MeldLiquids.stormingAspect);
            inputItems.addAll(MeldItems.tenbris, MeldItems.clayMallows, MeldItems.carbolith, MeldItems.debris, MeldItems.shadesteel, MeldItems.silver, MeldItems.likestoneSediments, MeldItems.quartzStrata);
            outputItems.addAll(MeldItems.cruciblePlating, MeldItems.shadesteel, MeldItems.glassMallows, MeldItems.annealedSilver);
            outputLiquids.addAll(MeldLiquids.fumes);

            inputLiquidSlots = 1;
            outputLiquidSlots = 1;
            recipes = MeldRecipes.kilnRecipies(2, 1);
        }};
        /*
        metalworks = new RecipeCrafter("metalworks"){{
            requirements(Category.crafting, with(MeldItems.debris, 350, MeldItems.cruciblePlating, 150));
        }};

         */
        rotaryKiln = new RecipeCrafter("rotary-kiln"){{
            requirements(Category.crafting, with(MeldItems.debris, 350, MeldItems.cruciblePlating, 150));
            size = 5;

            hasItems = true;
            hasLiquids = true;
            liquidCapacity = outletRate * 60 * 2;

            itemCapacity = 48;
            inputLiquidSlots = 1;
            outputLiquidSlots = 1;

            inputLiquids.addAll(MeldLiquids.aspect, MeldLiquids.boundAspect, MeldLiquids.stormingAspect);
            inputItems.addAll(MeldItems.tenbris, MeldItems.clayMallows, MeldItems.carbolith, MeldItems.debris, MeldItems.shadesteel, MeldItems.glassMallows, MeldItems.silver, MeldItems.likestoneSediments, MeldItems.quartzStrata);
            outputItems.addAll(MeldItems.cruciblePlating, MeldItems.shadesteel, MeldItems.glassMallows, MeldItems.annealedSilver);
            outputLiquids.addAll(MeldLiquids.fumes);

            recipes = MeldRecipes.kilnRecipies(12, 8);
        }};

        pneumaticExtruder = new RecipeCrafter("pneumatic-extruder"){{
            requirements(Category.crafting, with(MeldItems.debris, 120));
            size = 3;

            hasItems = true;
            hasLiquids = true;
            liquidCapacity = outletRate * 60;

            itemCapacity = 10;

            inputLiquids.addAll(MeldLiquids.aspect, MeldLiquids.boundAspect, MeldLiquids.stormingAspect);
            inputItems.addAll(MeldItems.shadesteel, MeldItems.elnarDust, MeldItems.debris, MeldItems.silver, MeldItems.annealedSilver);
            outputItems.addAll(MeldItems.aspectPipe);

            inputLiquidSlots = 1;

            recipes.addAll(
                    new TimedRecipe(){{
                        consumers.addAll(
                                new ConsumeItems(with(MeldItems.annealedSilver, 2, MeldItems.debris, 2)),
                                new StupidConsumeAspects(outletRate * 2, AspectGroup.aspect)
                        );
                        producers.addAll(
                                new ProduceItem(new ItemStack(MeldItems.aspectPipe, 2))
                        );
                    }},
                    new TimedRecipe(){{
                        consumers.addAll(
                                new ConsumeItems(with(MeldItems.silver, 2, MeldItems.debris, 2)),
                                new StupidConsumeAspects(outletRate * 2, AspectGroup.aspect)
                        );
                        producers.addAll(
                                new ProduceItem(new ItemStack(MeldItems.aspectPipe, 2))
                        );
                    }},
                    new TimedRecipe(){{
                        consumers.addAll(
                                new ConsumeItems(with(MeldItems.elnarDust, 2, MeldItems.debris, 2)),
                                new StupidConsumeAspects(outletRate * 2, AspectGroup.aspect)
                        );
                        producers.addAll(
                                new ProduceItem(new ItemStack(MeldItems.aspectPipe, 2))
                        );
                    }}
            );
        }};

        crushWeaver = new AttributeCrafter("crush-weaver"){{
            requirements(Category.crafting, with(
                    MeldItems.debris, 40,
                    MeldItems.annealedSilver, 40,
                    MeldItems.dissonitre, 40
            ));
            size = 2;

            health = 600;

            attribute = MeldAttributes.soilAttr;
            baseEfficiency = 0;
            minEfficiency = 1;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawRegion("-grinder"){{
                        x = 2;
                        y = -3;
                        spinSprite = true;
                        rotateSpeed = 30;
                    }},
                    new DrawRegion("-grinder"){{
                        x = 2;
                        y = 3;
                        spinSprite = true;
                        rotateSpeed = -30;
                    }},
                    new DrawRegion("-grinder"){{
                        x = -2;
                        y = 3;
                        spinSprite = true;
                        rotateSpeed = 30;
                    }},
                    new DrawRegion("-grinder"){{
                        x = -2;
                        y = -3;
                        spinSprite = true;
                        rotateSpeed = -30;
                    }},
                    new DrawGlowRegion(),
                    new DrawRegion()
            );

            craftTime = 30;
            consumeItem(MeldItems.dissonitre, 1);
            consume(new StupidConsumeAspects(outletRate, AspectGroup.aspect));
            outputItems = with(MeldItems.vitricMesh, 1);
        }};

        crushWeaver = new AttributeCrafter("aether-vitrifier"){{
            requirements(Category.crafting, with(
                    MeldItems.debris, 40,
                    MeldItems.electrumSheet, 80,
                    MeldItems.dissonitre, 40
            ));
            size = 2;

            health = 600;

            attribute = MeldAttributes.soilAttr;
            baseEfficiency = 0;
            minEfficiency = 1;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawGlowRegion(),
                    new DrawRegion()
            );

            craftTime = 60;
            consume(new StupidConsumeAspects(outletRate/10f, AspectGroup.aether));
            consumePower(2);
            outputItems = with(MeldItems.dissonitre, 1);
        }};

        stormIris = new RecipeCrafter("storm-iris"){{
            requirements(Category.crafting, with(MeldItems.debris, 350, MeldItems.annealedSilver, 300, MeldItems.electrumSheet, 300, MeldItems.quartzStrata, 250));
            size = 5;
            hasItems = hasLiquids = hasPower = true;
            liquidCapacity = 120;

            inputLiquidSlots = 1;
            outputLiquidSlots = 1;

            inputLiquids.addAll(MeldLiquids.aether);
            outputLiquids.addAll(MeldLiquids.thunderingAether);

            consPower = new ConsumePowerRecipe();

            recipes.addAll(
                new TimedRecipe(){{
                    consumers.addAll(
                            new ConsumePower(10, 500, false),
                            new ConsumeLiquid(MeldLiquids.aether, 1)
                    );
                    producers.addAll(
                            new ProduceLiquid(MeldLiquids.thunderingAether, 2)
                    );
                }}
            );
        }};

        //#Region Power

        conductivePile = new Battery("conductive-pile"){{
            requirements(Category.power, with(MeldItems.electrumSheet, 12));
            size = 1;
            health = 800;
            armor = -80;
            solid = false;

            fogRadius = 0;
            consumePowerBuffered(1);

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawGlowRegion()
            );
        }};

        substation = new PowerNode("substation"){{
            requirements(Category.power, with(MeldItems.annealedSilver, 450, MeldItems.electrumSheet, 600, MeldItems.dissonitre, 150, MeldItems.vitricMesh, 300));
            size = 5;
            health = 2100;
            armor = 15;

            laserRange = 50;
            maxNodes = 3;
            fogRadius = 0;
            laserColor1 = Color.white;
            laserColor2 = Color.valueOf("646461");

            consumesPower = outputsPower = true;
            consumePowerBuffered(300);
        }};

        aspectDischarger = new ConsumeThermal("aspect-discharger"){{
            requirements(Category.power, with(MeldItems.debris, 35, MeldItems.electrumSheet, 120));
            size = 2;
            health = 650;
            solid = false;
            underBullets = true;

            minEfficiency = 1 - 0.00001f;
            liquidCapacity = 100;

            powerProduction = 1;

            consumePowerBuffered(1);
            consume(new StupidConsumeAspects(outletRate, AspectGroup.aspect));

            attribute = MeldAttributes.soilAttr;

            drawer = new DrawMulti(
                new DrawRegion("-matting"){{
                    layer = Layer.blockUnder;
                }},
                new DrawGlowRegion("-matting-glow"){{
                    layer = Layer.blockUnder;
                }},
                new DrawRegion("-mid"){{
                    layer = Layer.block;
                }},
                new DrawRegion("-mid-coils"){{
                    layer = Layer.block;
                }},
                new DrawGlowRegion("-mid-coils-glow"),
                new DrawRegion("-top"){{
                    layer = Layer.block + 1;
                }}
            );
        }};

        sharkFactory = new UnitFactory("shark-factory"){{
            requirements(Category.units, with(MeldItems.debris, 500, MeldItems.carbolith, 350, MeldItems.silver, 450));
            size = 5;
            health = 2500;

            consume(new StupidConsumeAspects(outletRate * 12, AspectGroup.aspect));
            plans.addAll(
                    new UnitPlan(MeldUnits.shark, 60 * 5, with(MeldItems.annealedSilver, 120, MeldItems.carbolith, 60))
            );
        }};

        launchStation = new LaunchStation("launch-station"){{
            requirements(Category.units, with(MeldItems.debris, 45, MeldItems.quartzStrata, 60));
            size = 3;
            health = 750;

            chargeRate = 1/60f/1.5f;

            consume(
                    new StupidConsumeAspects(outletRate * 2, AspectGroup.aspect)
            );
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
            clipSize = 220;

            liquidCapacity = 1 * outletRate * 60;
            hasLiquids = true;

            consume(new StupidConsumeAspects(outletRate * 2, AspectGroup.aspect));
        }};

        movementAnchor = new MovementAnchor("movement-anchor"){{
            requirements(Category.effect, with(
                    MeldItems.debris, 80,
                    MeldItems.carbolith, 120
            ));
            size = 3;
            health = 1200;
            range = 22 * Vars.tilesize;

            consume(new StupidConsumeAspects(outletRate * 3, AspectGroup.aspect));
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
                    new StupidConsumeAspects(0.5f, AspectGroup.fumes)
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

            consume(new StupidConsumeAspects(1f, AspectGroup.fumes));
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

        platedChute = new Duct("plated-chute"){{
            requirements(Category.distribution, with(MeldItems.cruciblePlating, 2));
            armored = true;
            health = 45;
            armor = 10;
            speed = 4f;
        }};

        unloadingHub = new Unloader("unloading-hub"){{
            requirements(Category.distribution, with(MeldItems.debris, 450));
            size = 3;
            health = 900;
            armor = 30;

            speed = 1f;

            solid = true;
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
            health = 650;
            armor = 85;

            customShadow = true;
            squareSprite = false;
            floating = true;
        }};

        carbonicBarrierLarge = new Wall("stone-blocker-large"){{
            requirements(Category.defense, with(MeldItems.stonyParticulate, 60));
            size = 2;
            health = 2600;
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

            ((Conduit) pipeline).rotBridgeReplacement = this;
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

                        buildingDamageMultiplier = 2;

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
            requirements(Category.defense, with(MeldItems.larvalPlating, 50, MeldItems.meldShard, 35));
            size = 2;
            health = 400;
            destroyBulletSameTeam = true;
            destroyBullet = new TransitionBulletType(){{
                hitEffect = despawnEffect = Fx.none;
                fragBullets = 3;
                fragBullet = new BulletType(){{
                    spawnUnit = MeldUnits.jilla;
                }};
            }};
        }};

        billaCoffer = new Wall("billa-coffer"){{
            requirements(Category.defense, with(MeldItems.larvalPlating, 50, MeldItems.meldShard, 35));
            size = 2;
            health = 400;
            armor = 80;
            destroyBulletSameTeam = true;

            //TODO: WTF is the purpose of this...
            destroyBullet = new TransitionBulletType(){{
                hitEffect = despawnEffect = Fx.none;
                fragBullets = 1;
                fragBullet = new BulletType(){{
                    spawnUnit = MeldUnits.billa;
                }};
            }};
        }};

        craigCoffer = new LiquidTurret("craig-coffer"){{
            requirements(Category.defense, with(MeldItems.larvalPlating, 50, MeldItems.meldShard, 35));
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
            requirements(Category.defense, with(MeldItems.larvalPlating, 200, MeldItems.meldShard, 120));
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
                        pierceBuilding = true;
                        pierceCap = 2;

                        splashDamage = 25;
                        splashDamageRadius = 25;

                        knockback = 4f;

                        hitEffect = despawnEffect = Fx.explosion;

                        impact = true;

                        fragBullets = 3;
                        fragRandomSpread = 45;

                        fragBullet = new BasicBulletType(4, 15, Meld.prefix("clump")){{
                            speed = 4;
                            lifetime = 10;
                            width = 3;
                            height = 8;

                            pierce = true;
                            pierceBuilding = true;
                            pierceCap = 2;

                            knockback = 4f;

                            impact = true;

                            hitEffect = despawnEffect = Fx.none;
                        }};
                    }}
            );
        }};
    }
}
