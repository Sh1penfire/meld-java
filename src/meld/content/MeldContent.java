package meld.content;

import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import meld.AspectPipe;
import meld.MovementAnchor;
import meld.SonarSpire;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootSpread;
import mindustry.entities.units.StatusEntry;
import mindustry.game.FogControl;
import mindustry.gen.Unit;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidBridge;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BeamDrill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.Attribute;

import static mindustry.type.ItemStack.with;

public class MeldContent {

    public static Attribute aetherAttr;

    public static Item debris, carbolith, silver, resonarum;

    //Items that meld mostly uses
    public static Item stonyParticulate, larvalPlating;

    public static Liquid aether, aspect;

    //Strata blocks first
    public static Block chute, chuteRouter, chuteBridge, chuteJunction, chuteOverflow;

    public static Block sonarSpire, movementAnchor, nullifier;

    public static Block coreRaft, aetherAccumulator, elementalBlaster, earthboundInfuser;

    public static Block channelNode, channelFace, aspectOutlet, aspectPipe;

    public static Block sunder, molotov, vivisection;

    //Meld blocks
    public static Block pipeline, pipelineRouter, pipelineCrossing, pipelineBridge, meldCannon, meldCultivator, meldNode, meldSuppressor, meldSynapse;
    public static Block crystalBarrier, crystalBarrierLarge, carbonicBarrier, carbonicBarrierLarge;

    public static UnitType
            //Player units
            bulbhead, shark,

            //Meld units
            melee, armored, artillery;

    public static StatusEffect rally, anchored;

    public static float outletRate = 100f/60f;

    public static Item item(String name){
        return new Item(name);
    }

    public static Liquid liquid(String name){
        return new Liquid(name);
    }
    public static Liquid gas(String name){
        return new Liquid(name){{
            gas = true;
        }};
    }

    public static void load(){

        aetherAttr = Attribute.add("aether");

        rally = new StatusEffect("rally"){{
            speedMultiplier = 1.25f;
            reloadMultiplier = 2;

        }};

        anchored = new StatusEffect("anchored"){

            @Override
            public void update(Unit unit, StatusEntry entry) {

                unit.speedMultiplier /= speedMultiplier;
                //Start the falloff after 90 secconds, continue falloff for the remaining 150 secconds
                unit.speedMultiplier *= Mathf.lerp(
                        1, speedMultiplier,

                        Interp.pow2.apply(
                                Mathf.clamp(Math.min(entry.time + 90, 150)/(150))
                        )
                );
            }
            {

            speedMultiplier = 0.3f;
            dragMultiplier = 2;
        }};


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

        debris = item("debris");
        carbolith = item("carbolith");
        silver = item("silver");
        resonarum = item("resonarum");
        stonyParticulate = item("stony-particulate");
        larvalPlating = item("larval-plating");

        channelFace = new LiquidJunction("channel-face"){{
            requirements(Category.liquid, with(
                    debris, 2
            ));
            health = 120;
            solid = false;
        }};

        channelNode = new LiquidRouter("channel-node"){{
            requirements(Category.liquid, with(
                    debris, 5
            ));
            health = 120;

            liquidCapacity = 100;
            solid = false;
        }};

        aspectOutlet = new GenericCrafter("aspect-outlet"){{
            requirements(Category.liquid, with(
                    debris, 7
            ));

            health = 200;

            liquidOutputDirections = new int[]{0};
            rotate = true;
            quickRotate = true;

            consume(new ConsumeLiquid(
                    aether, outletRate/10f
            ));

            outputLiquid = new LiquidStack(aspect, outletRate);
        }};

        aspectPipe = new AspectPipe("aspect-pipe"){{
            requirements(Category.liquid, with(
                    debris, 2,
                    silver, 1
            ));
            liquidCapacity = 80;
            size = 1;
            health = 320;

        }};

        coreRaft = new CoreBlock("core-raft"){{
            requirements(Category.effect, with(
                    debris, 600,
                    carbolith, 350
            ));
            size = 3;

            health = 4000;

            unitType = UnitTypes.oxynoe;
            solid = false;
        }};

        aetherAccumulator = new AttributeCrafter("aether-accumulator"){{
            requirements(Category.production, with(
                    debris, 40
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
                    debris, 40
            ));
            size = 3;

            drillTime = 90;

            health = 420;

            consume(new ConsumeLiquid(
                    aspect, outletRate
            ));
        }};

        earthboundInfuser = new AttributeCrafter("earthbound-infuser"){{
            requirements(Category.crafting, with(
                    debris, 40,
                    silver, 60
            ));
            size = 3;

            attribute = Attribute.steam;
            baseEfficiency = 0;
            minEfficiency = 9;
            boostScale = 1f/9f;
            displayEfficiencyScale = 9;
            craftTime = 60/5;

            consume(
                new ConsumeLiquid(
                    aspect, 2 * outletRate
                )
            );

            consumeItem(debris, 1);
            outputItem = new ItemStack(carbolith, 1);
        }};

        sonarSpire = new SonarSpire("sonar-spire"){{
            requirements(Category.effect, with(
                    debris, 60,
                    silver, 80
            ));
            size = 2;
            health = 300;

            liquidCapacity = 8 * outletRate * 60;

            consume(
                    new ConsumeLiquid(
                            aspect, 2 * outletRate
                    )
            );
        }};

        movementAnchor = new MovementAnchor("movement-anchor"){{
            requirements(Category.effect, with(
                    debris, 80,
                    carbolith, 120
            ));
            size = 3;
            health = 1200;
            range = 25 * Vars.tilesize;

            consume(
                    new ConsumeLiquid(
                            aspect, 2 * outletRate
                    )
            );
        }};

        nullifier = new Nullifier("nullifier"){{
            requirements(Category.effect, with(debris, 60));
            size = 5;
            health = 300;
        }};

        chute = new Duct("chute"){{
            requirements(Category.distribution, with(debris, 1));
            health = 90;
            speed = 4f;
            bridgeReplacement = chuteBridge;
            junctionReplacement = chuteJunction;
        }};

        chuteRouter = new DuctRouter("chute-router"){{
            requirements(Category.distribution, with(debris, 4));
            health = 90;
            speed = 4f;
            solid = false;
        }};

        chuteBridge = new DuctBridge("chute-bridge"){{
            requirements(Category.distribution, with(debris, 6));
            health = 90;
            speed = 4f;
            solid = false;
        }};

        chuteOverflow = new OverflowDuct("chute-overflow"){{
            requirements(Category.distribution, with(debris, 6));
            health = 90;
            speed = 4f;
            solid = false;
        }};

        chuteJunction = new DuctJunction("chute-crossing"){{
            requirements(Category.distribution, with(debris, 7));
            health = 90;
            speed = 4f;
            solid = false;
        }};

        //Meld blocks
        meldNode = new MeldNode("meld-node"){{
            size = 3;
        }};

        meldSuppressor = new MeldNode("meld-suppressor"){{
            size = 3;
        }};
        meldSynapse = new CoreBlock("meld-synapse"){{
            size = 5;
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

        pipeline = new Conduit("pipeline"){{
            requirements(Category.liquid, with(larvalPlating, 1));
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

        pipelineBridge = new LiquidBridge("pipeline-overpass"){{
            requirements(Category.liquid, with(larvalPlating, 12));
            liquidCapacity = 100;
            liquidPressure = 4;
            range = 8;
        }};

        meldCultivator = new AttributeCrafter("meld-cultivator"){{

        }};

        meldCannon = new LiquidTurret("meld-cannon"){{
            requirements(Category.turret, with(stonyParticulate, 60));
            size = 3;
            reload = 15;
            range = 6 * 48;
            minWarmup = 0.6f;

            health = 2000;

            drawer = new DrawTurret(){{
                parts.add(
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
                    aether,
                    new BasicBulletType(){{
                        speed = 6;

                        lifetime = 48;

                        damage = 15;
                        splashDamage = 25;
                        splashDamageRadius = 16;

                        knockback = 2;

                        scaleLife = true;

                        sprite = "shell";
                        width = 6;
                        height = 8;
                        despawnEffect = hitEffect = Fx.explosion;
                    }}
            );
        }};
    }
}
