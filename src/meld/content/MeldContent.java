package meld.content;

import arc.graphics.Color;
import arc.math.Interp;
import meld.*;
import meld.entities.bullet.OutflowBulletType;
import meld.entities.bullet.TransitionBulletType;
import meld.graphics.*;
import meld.world.blocks.*;
import meld.world.meta.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
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
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BeamDrill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;

import static mindustry.type.ItemStack.with;

public class MeldContent {

    public static Attribute aetherAttr;

    //Items that meld mostly uses
    public static Item stonyParticulate, larvalPlating;

    public static Liquid aether, aspect, meld;

    //Strata blocks first
    public static Block chute, chuteRouter, chuteBridge, chuteJunction, chuteOverflow;

    public static Block sonarSpire, movementAnchor, nullifier;

    public static Block coreRaft, aetherAccumulator, elementalBlaster, earthboundInfuser, sharkFactory;

    public static Block channelNode, channelFace, aspectOutlet, aspectPipe;

    public static Block sunder, molotov, vivisection;

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

        aetherAttr = Attribute.add("aether");

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
        }};

        stonyParticulate = item("stony-particulate");
        larvalPlating = item("larval-plating");

        channelFace = new LiquidJunction("channel-face"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 2
            ));
            health = 120;
            solid = false;
            placeableLiquid = true;
        }};

        channelNode = new LiquidRouter("channel-node"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 5
            ));
            health = 120;

            liquidCapacity = 100;
            solid = false;
            placeableLiquid = true;
        }};

        aspectOutlet = new GenericCrafter("aspect-outlet"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 7
            ));

            health = 200;

            placeableLiquid = true;

            liquidOutputDirections = new int[]{0};
            rotate = true;
            quickRotate = true;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(),
                    new DrawRegion(),
                    new DrawSideRegion()
            );

            consume(new ConsumeLiquid(
                    aether, outletRate/10f
            ));

            outputLiquid = new LiquidStack(aspect, outletRate);
        }};

        aspectPipe = new AspectPipe("aspect-pipe"){{
            requirements(Category.liquid, with(
                    MeldItems.debris, 2,
                    MeldItems.silver, 2
            ));
            leaks = false;
            health = 120;

            placeableLiquid = true;

            liquidCapacity = 80;
            size = 1;
            botColor = Color.white;
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

            ammoTypes.put(
                    MeldItems.silver,
                    new OutflowBulletType(){{
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
                    }}
            );

            consume(new ConsumeLiquid(aspect, outletRate));
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

            attribute = aetherAttr;
            baseEfficiency = 0;
            minEfficiency = 8.9f;
            maxBoost = 2;
            boostScale = 1/9f;
            liquidCapacity = 300;

            outputLiquid = new LiquidStack(aether, 1);
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

            consume(new ConsumeLiquid(
                    aspect, outletRate
            ));

            consume(new ConsumeLiquid(
                    meld, 1
            ){{
                optional = true;
                booster = true;
            }});
        }};

        earthboundInfuser = new AttributeCrafter("earthbound-infuser"){{
            requirements(Category.crafting, with(
                    MeldItems.debris, 40,
                    MeldItems.silver, 60
            ));
            size = 3;

            attribute = Attribute.steam;
            baseEfficiency = 0;
            minEfficiency = 9;
            boostScale = 1f/9f;
            displayEfficiencyScale = 9;
            craftTime = 60/5f;

            consume(
                new ConsumeLiquid(
                    aspect, 2 * outletRate
                )
            );

            consumeItem(MeldItems.debris, 1);
            outputItem = new ItemStack(MeldItems.carbolith, 1);
        }};

        sharkFactory = new UnitFactory("shark-factory"){{
            requirements(Category.units, with(MeldItems.debris, 500, MeldItems.carbolith, 350, MeldItems.silver, 450));
            size = 5;
            health = 2500;

            consume(new ConsumeLiquid(aspect, outletRate * 12));
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

            liquidCapacity = 8 * outletRate * 60;

            consume(
                    new ConsumeLiquid(
                            aspect, 2 * outletRate
                    )
            );
        }};

        movementAnchor = new MovementAnchor("movement-anchor"){{
            requirements(Category.effect, with(
                    MeldItems.debris, 80,
                    MeldItems.carbolith, 120
            ));
            size = 3;
            health = 1200;
            range = 22 * Vars.tilesize;

            consume(
                    new ConsumeLiquid(
                            aspect, 3 * outletRate
                    )
            );
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

        chuteOverflow = new OverflowDuct("chute-overflow"){{
            requirements(Category.distribution, with(MeldItems.debris, 6));
            health = 90;
            speed = 4f;
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
            requirements(Category.effect, with(larvalPlating, 2000));
            size = 5;
            health = 400000;
            armor = 5000;
            liquidCapacity = 300;

            statusDuration = 60 * 60;

            range = 300;

            status = MeldStatusEffects.amplified;
            ringColor = MeldPal.flamePink;

            consume(new ConsumeLiquid(meld, 1));
        }};

        meldCapsule = new ForceProjector("meld-capsule"){{
            requirements(Category.effect, with(larvalPlating, 350));
            size = 3;
            health = 1500;
            armor = 5;
            liquidCapacity = 120;

            consume(new ConsumeLiquid(meld, 1));
        }};

        carbonicBarrier = new Wall("stone-blocker"){{
            requirements(Category.defense, with(stonyParticulate, 15));
            health = 400;
            armor = 85;

            customShadow = true;
            squareSprite = false;
            floating = true;
        }};

        carbonicBarrierLarge = new Wall("stone-blocker-large"){{
            requirements(Category.defense, with(stonyParticulate, 60));
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
            requirements(Category.liquid, with(larvalPlating, 1));
            underBullets = false;
            liquidCapacity = 100;
            liquidPressure = 4;
        }};

        pipelineCrossing = new LiquidJunction("pipeline-crossing"){{
            requirements(Category.liquid, with(larvalPlating, 3));
            liquidCapacity = 100;
            liquidPressure = 4;

        }};

        pipelineRouter = new LiquidRouter("pipeline-router"){{
            requirements(Category.liquid, with(larvalPlating, 5));
            liquidCapacity = 200;
            liquidPressure = 4;

        }};

        pipelineBridge = new DirectionLiquidBridge("pipeline-overpass"){{
            requirements(Category.liquid, with(larvalPlating, 12));
            liquidCapacity = 100;
            liquidPressure = 4;
            range = 8;
        }};

        meldCultivator = new AttributeCrafter("meld-cultivator"){{
            requirements(Category.production, with(
                    larvalPlating, 120
            ));

            size = 3;

            attribute = Attribute.steam;
            baseEfficiency = 0;
            minEfficiency = 9;
            boostScale = 1f/9f;
            displayEfficiencyScale = 9;

            outputLiquid = new LiquidStack(meld, 3);
        }};

        meldCannon = new LiquidTurret("meld-cannon"){{
            requirements(Category.turret, with(stonyParticulate, 60));
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
                    meld,
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

            consume(new ConsumeLiquid(meld, 1));
        }};

        meldMortar = new LiquidTurret("meld-mortar"){{
            requirements(Category.turret, with(stonyParticulate, 60));
            size = 3;
            reload = 120;
            range = 240;

            health = 2000;

            drawer = new DrawTurret(){{
                parts.addAll();
            }};

            ammoTypes.put(
                    meld,
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

            consume(new ConsumeLiquid(meld, 1));
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
                    meld,
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
                    meld,
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
