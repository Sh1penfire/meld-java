package meld.content;

import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import meld.MovementAnchor;
import meld.SonarSpire;
import mindustry.content.UnitTypes;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Unit;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BeamDrill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.meta.Attribute;

import static mindustry.type.ItemStack.with;

public class MeldContent {
    public static Item debris, carbolith, silver, resonarum;
    public static Liquid aether, aspect;

    public static Block chute, chuteRouter, chuteBridge, chuteJunction, chuteOverflow;

    public static Block sonarSpire, movementAnchor;

    public static Block coreRaft, elementalBlaster, earthboundInfuser;

    public static Block channelNode, channelFace, aspectOutlet;

    public static Block sunder, molotov, vivisection;

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

        channelFace = new LiquidJunction("channel-junction"){{
            requirements(Category.liquid, with(
                    debris, 2
            ));
        }};

        channelNode = new LiquidRouter("channel-node"){{
            requirements(Category.liquid, with(
                    debris, 5
            ));
        }};

        aspectOutlet = new GenericCrafter("aspect-outlet"){{
            requirements(Category.liquid, with(
                    debris, 7
            ));
            liquidOutputDirections = new int[]{0};
            rotate = true;
            quickRotate = true;

            consume(new ConsumeLiquid(
                    aether, outletRate/10f
            ));

            outputLiquid = new LiquidStack(aspect, outletRate);
        }};

        coreRaft = new CoreBlock("core-raft"){{
            requirements(Category.effect, with(
                    debris, 600,
                    carbolith, 350
            ));
            size = 3;
            unitType = UnitTypes.oxynoe;
            solid = false;
        }};

        elementalBlaster = new BeamDrill("elemental-blaster"){{
            requirements(Category.production, with(
                    debris, 40
            ));
            size = 3;

            consume(new ConsumeLiquid(
                    aspect, outletRate
            ));
        }};

        earthboundInfuser = new AttributeCrafter("earthbound-infuser"){{
            requirements(Category.production, with(
                    debris, 40
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
            requirements(Category.effect, with(debris, 60));
            size = 2;
            health = 300;

            consume(
                    new ConsumeLiquid(
                            aspect, 2 * outletRate
                    )
            );
        }};

        movementAnchor = new MovementAnchor("movement-anchor"){{
            requirements(Category.effect, with(debris, 60));
            size = 3;
            health = 300;

            consume(
                    new ConsumeLiquid(
                            aspect, 2 * outletRate
                    )
            );
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
    }
}
