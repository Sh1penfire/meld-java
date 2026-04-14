package meld.content;

import arc.func.Prov;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.ObjectIntMap;
import arc.util.Log;
import arc.util.Tmp;
import meld.*;
import meld.entities.bullet.TransitionBulletType;
import meld.entities.unit.abilities.*;
import meld.entities.unit.weapons.template.BraigWeapon;
import meld.entities.unit.weapons.template.PointagoWeapon;
import meld.graphics.part.*;
import meld.entities.unit.*;
import meld.entities.unit.type.*;
import meld.graphics.*;
import meld.entities.unit.weapons.DeathWeapon;
import meld.entities.unit.weapons.ShadowVisualWeapon;
import mindustry.Vars;
import mindustry.ai.types.HugAI;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.abilities.RegenAbility;
import mindustry.entities.abilities.StatusFieldAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.*;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;
import arc.struct.ObjectMap.Entry;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class MeldUnits {

    private static Entry<Class<? extends Entityc>, Prov<? extends Entityc>>[] types = new Entry[]{
            prov(BulbheadEntity.class, BulbheadEntity::new)
    };

    private static ObjectIntMap<Class<? extends Entityc>> idMap = new ObjectIntMap<>();

    /**
     * Internal function to flatmap {@code Class -> Prov} into an {@link Entry}.
     * @author GlennFolker
     */

    private static <T extends Entityc> Entry<Class<T>, Prov<T>> prov(Class<T> type, Prov<T> prov){
        Entry<Class<T>, Prov<T>> entry = new Entry<>();
        entry.key = type;
        entry.value = prov;
        return entry;
    }

    /**
     * Setups all entity IDs and maps them into {@link EntityMapping}.
     * Find all free ids to map to, then put the Entry(s) from types into the idMap. Starts searching after the last known index of a vanilla Entry
     */

    private static void setupID(){
        int start = 33;
        int[] free = new int[types.length];
        for (int i = start, j = 0; i < EntityMapping.idMap.length; i++) {
            if(EntityMapping.idMap[i] == null) free[j++] = i;
            if(j > free.length - 1) break;
        }

        Log.info("setting up map");
        for (int i = 0; i < free.length; i++) {
            idMap.put(types[i].key, free[i]);
            EntityMapping.idMap[free[i]] = types[i].value;
        }
    }

    public static <T extends Entityc> int classID(Class<T> type){
        return idMap.get(type, -1);
    }

    //player units
    public static UnitType
    bulbhead, shark,

    bulbheadOverseer,

    //enemy units
    cannonOverseer, blob,
    afraig, craig, braig, pointago, globkin,
    jilla, billa, scorcher, kathid;

    public static void load(){
        setupID();

        bulbheadOverseer = new UnitType("bulbhead-overseer"){{
            float IR = 80;

            health = 240;
            armor = -40;
            lifetime = 360;
            speed = 0;
            flying = true;

            isEnemy = false;
            useUnitCap = false;
            createWreck = false;
            createScorch = false;
            allowedInPayloads = false;
            physics = false;
            bounded = false;
            hidden = true;
            playerControllable = false;
            canDrown = false;

            hoverable = true;
            hovering = true;

            faceTarget = false;
            targetable = true;
            drawMinimap = false;

            fogRadius = 10;
            lightRadius = 80;
            lightOpacity = 1;
            deathSound = Sounds.none;
            deathExplosionEffect = Fx.none;


            engineSize = 0;
            shadowElevation = 0;
            drawCell = drawBody = false;
            drawSoftShadow = false;

            weapons.addAll(
                    new RepairBeamWeapon(){{
                        mirror = false;
                        rotate = true;
                        rotateSpeed = 9;
                        x = 0;
                        y = 0;
                        shootY = 2.5f;
                        beamWidth = 0.5f;

                        repairSpeed = 2;
                        targetInterval = 1;
                        targetSwitchInterval = 1;

                        reload = 20;
                        targetUnits = false;
                        targetBuildings = true;
                        controllable = false;
                        laserColor = healColor = Pal.accent;
                        bullet = new BulletType(){{
                            maxRange = IR;
                        }};
                    }}
            );

            parts.addAll(
                    new ShapePart(){{
                        x = y = 0;
                        circle = true;
                        radius = 4;
                        radiusTo = 0;
                        layer = Layer.effect;
                        color = Pal.accent;
                        colorTo = MeldPal.accentClear;
                        progress = PartProgress.life.delay(0.8f).curve(Interp.pow5);
                    }},
                    new HaloPart(){{
                        tri = true;
                        haloRadius = 8;
                        radius = 4;
                        radiusTo = 0;
                        triLength = 8;
                        triLengthTo = 0;
                        shapes = 3;
                        color = Pal.accent;
                        haloRotateSpeed = 2;
                        layer = Layer.effect;
                        progress = PartProgress.life.compress(0.5f, 0.8f).curve(Interp.pow5In);
                    }},
                    new HaloPart(){{
                        tri = true;
                        haloRadius = 14;
                        radius = 4;
                        radiusTo = 0;
                        triLength = 6;
                        triLengthTo = 0;
                        shapes = 3;
                        color = Pal.accent;
                        haloRotateSpeed = 1;
                        layer = Layer.effect;
                        progress = PartProgress.life.delay(0.8f).curve(Interp.pow5In);
                    }},
                    //Sonar
                    new ShapePart(){{
                        x = y = 0;
                        progress = PartProgress.life;
                        circle = true;
                        hollow = true;
                        radius = 0;
                        radiusTo = IR;
                        stroke = 6;
                        strokeTo = 0;
                        layer = Layer.buildBeam;
                        color = Pal.accent;
                    }},

                    new ShapePart(){{
                        x = y = 0;
                        progress = PartProgress.life.curve(Interp.pow10Out);
                        circle = true;
                        hollow = true;
                        radius = 0;
                        radiusTo = IR;
                        stroke = 8;
                        strokeTo = 0;
                        layer = Layer.buildBeam;
                        color = Pal.accent;
                    }},

                    new ShapePart(){{
                        x = y = 0;
                        progress = PartProgress.life.curve(Interp.pow10In);
                        circle = true;
                        hollow = true;
                        radius = IR;
                        radiusTo = IR;
                        stroke = 4;
                        strokeTo = 0;
                        layer = Layer.buildBeam;
                        color = Pal.accent;
                    }}
            );

            abilities.addAll(
                    new RegenAbility(){{
                        amount = 0.5f;
                    }},
                    new StatusFieldAbility(MeldStatusEffects.rally, IR, 5, IR){{
                        activeEffect = Fx.none;
                    }}
            );
            constructor = TimedKillUnit::create;
        }};

        bulbhead = new UnitType("bulbhead"){{
            float IR = 120;

            health = 290;

            speed = 2.55f;
            drag = 0.15f;
            accel = 0.8f;

            rotateSpeed = 9;
            trailLength = 18;
            trailScl = 1.2f;

            hitSize = 10;

            aimDst = 10;
            range = 100;


            lightRadius = IR;
            lightOpacity = 1;
            buildSpeed = 1;
            mineTier = 2;
            mineSpeed = 8;

            buildBeamOffset = 9;
            rotateToBuilding = false;
            buildRange = IR;
            mineRange = IR;
            fogRadius = IR/ Vars.tilesize;

            weapons.add(
                    new RepairBeamWeapon("meld-bulbhead-healer"){{
                        mirror = false;
                        rotate = true;
                        rotateSpeed = 9;
                        x = 0;
                        y = -4;
                        shootY = 2.5f;
                        beamWidth = 0.5f;

                        repairSpeed = 2;
                        targetInterval = 1;
                        targetSwitchInterval = 1;

                        reload = 20;
                        targetUnits = false;
                        targetBuildings = true;
                        controllable = false;
                        laserColor = healColor = Pal.accent;
                        bullet = new BulletType(){{
                            maxRange = 120;
                        }};
                    }},
                    new Weapon(){{
                        mirror = false;
                        rotate = true;
                        rotateSpeed = 180;
                        shootCone = 180;
                        x = y = recoil = 0;
                        reload = 120;
                        shootSound = Sounds.none;
                        minWarmup = 1;
                        linearWarmup = true;
                        shootWarmupSpeed = 5/90f;
                        DrawPart.PartProgress mixedProg = DrawPart.PartProgress.reload.curve(Interp.reverse).mul(DrawPart.PartProgress.warmup);
                        parts.addAll(
                                new ShapePart(){{
                                    circle = hollow = true;
                                    radius = 120;
                                    radiusTo = 0;
                                    layer = Layer.effect;
                                    stroke = 6;
                                    strokeTo = 10;
                                    color = MeldPal.accentClear;
                                    colorTo = Pal.accent;
                                    progress = mixedProg.compress(0.1f, 0.9f).curve(Interp.pow2In);
                                }},

                                new ShapePart(){{
                                    circle = hollow = true;
                                    radius = 45;
                                    radiusTo = 0;
                                    layer = Layer.effect;
                                    stroke = 6;
                                    strokeTo = 10;
                                    color = MeldPal.accentClear;
                                    colorTo = Pal.accent;
                                    progress = mixedProg.compress(0.2f, 0.8f).curve(Interp.pow5In);
                                }},

                                new ShapePart(){{
                                    circle = hollow = true;
                                    radius = 45;
                                    radiusTo = 0;
                                    layer = Layer.effect;
                                    stroke = 6;
                                    strokeTo = 12;
                                    color = MeldPal.accentClear;
                                    colorTo = Pal.accent;
                                    progress = mixedProg.curve(Interp.pow10In);
                                }}
                        );
                        bullet = new BulletType(){{
                            speed = 4;
                            damage = 0;
                            lifetime = 48;
                            keepVelocity = false;
                            absorbable = hidden = reflectable = collides = false;
                            scaleLife = true;
                            rangeOverride = 192;
                            hitSound = Sounds.none;
                            trailChance = 1;

                            parts.addAll(
                                    new ShapePart(){{
                                        circle = hollow = true;
                                        radius = 8;
                                        radiusTo = 0;
                                        layer = Layer.effect;
                                        stroke = 6;
                                        strokeTo = 2;
                                        color = Pal.accent;
                                        colorTo = MeldPal.accentClear;
                                        progress = PartProgress.life.compress(0.2f, 0.9f).curve(Interp.pow5In);
                                    }},

                                    new ShapePart(){{
                                        circle = hollow = true;
                                        radius = 8;
                                        radiusTo = 0;
                                        layer = 170;
                                        stroke = 6;
                                        strokeTo = 2;
                                        color = Pal.accent;
                                        colorTo = MeldPal.accentClear;
                                        progress = PartProgress.life.compress(0.2f, 0.9f).curve(Interp.pow5In);
                                    }}
                            );

                            fragBullets = 1;
                            fragBullet = new BulletType(){{
                                spawnUnit = bulbheadOverseer;
                            }};
                        }};
                    }}
            );

            parts.addAll(
                    new HoverPart(){{
                        x = y = 0;
                        phase = 420;
                        sides = 60;
                        mirror = false;
                        radius = IR;
                        stroke = 3;
                        circles = 2;
                        minStroke = 0.5f;
                        layer = Layer.buildBeam;
                        color = Pal.accent;
                    }},
                    new ShapePart(){{
                        x = y = 0;
                        circle = true;
                        hollow = true;
                        stroke = 4;
                        radius = IR - 2;
                        layer = Layer.buildBeam;
                        color = Pal.accent;
                    }}
            );
            constructor = BulbheadEntity::new;

            abilities.addAll(
                    new RegenAbility(){{
                        amount = 0.5f;
                    }},
                    new StatusFieldAbility(MeldStatusEffects.rally, 120, 5, IR){{
                        activeEffect = Fx.none;
                    }},
                    new SlipstreamHullAbility()
            );
            //can't rally yourself goober >w<
            immunities.addAll(
                    MeldStatusEffects.rally,
                    MeldStatusEffects.drenched
            );
        }};

        shark = new UnitType("shark"){{
            hovering = true;
            outlineColor = Color.clear;

            speed = 2.5f;
            health = 420;
            drag = 0.08f;
            accel = 0.1f;

            hitSize = 16;
            fogRadius = 24;

            lightRadius = 85;
            lightOpacity = 0.35f;
            range = 145;
            rotateSpeed = 6;

            useEngineElevation = false;
            faceTarget = false;
            canDrown = false;
            deathExplosionEffect = Fx.none;

            trailLength = 5;

            weapons.addAll(
                    new Weapon(Meld.prefix("shark-mount")){{
                        mirror = false;
                        rotate = true;
                        x = 0;
                        y = -2;
                        reload = 90;
                        rotateSpeed = 3.5f;
                        recoil = 1.25f;
                        inaccuracy = 15;
                        velocityRnd = 0.05f;
                        shoot.shots = 3;
                        shoot.shotDelay = 5;

                        bullet = new MissileBulletType(){{
                            sprite = "missile-large";
                            speed = 4.5f;
                            drag = -0.02f;
                            rangeOverride = 24 * Vars.tilesize;

                            keepVelocity = false;
                            scaleLife = false;

                            lifetime = 30;
                            damage = 1;
                            splashDamage = 15;
                            splashDamageRadius = 25;

                            homingDelay = 10;
                            homingPower = 0.085f;
                            weaveScale = 5;
                            weaveMag = 1.5f;

                            width = 3;
                            height = 8;
                            shrinkX = 0;
                            shrinkY = 0.2f;
                            hitShake = 2.5f;
                            frontColor = Color.white;
                            backColor = trailColor = MeldPal.shark;
                            trailChance = 0.15f;
                            trailLength = 5;

                            despawnEffect = Fx.none;
                            hitEffect = new Effect(12, e -> {
                                e.scaled(5, e1 -> {
                                    Draw.color(MeldPal.shark, MeldPal.darkShark, e.fin());
                                    Draw.alpha(e.foutpow() * 0.5f);
                                    Lines.stroke(1  + 2 * e.fout());
                                    Lines.circle(e1.x, e1.y, 5 + e1.fin() * 10);
                                });

                                Draw.color(MeldPal.shark, MeldPal.darkShark, e.fin());
                                Angles.randLenVectors(e.id, 3, 10, 15, (x, y) -> {
                                    Lines.lineAngle(e.x + x * e.fin(), e.y + y * e.fin(), Angles.angle(x, y), e.fout() * 4);
                                });
                            });
                            setDefaults = false;
                            despawnHit = false;
                            fragBullets = 1;
                            fragSpread = 15;
                            fragRandomSpread = 0;
                            fragBullet = new SapBulletType(){{
                                damage = 5;
                                sapStrength = 1;
                                length = 15;
                                pierce = true;
                                keepVelocity = false;
                                knockback = -1;
                                impact = true;
                                status = StatusEffects.none;
                                absorbable = hittable = reflectable = false;
                                color = MeldPal.shark;
                            }};
                        }};

                        parts.add(
                                new RegionPart("-shadow"){{
                                    mirror = false;
                                    under = true;
                                }}
                        );
                    }},
                    new ShadowVisualWeapon(){{
                        shadowElevation = 0.1f;
                        shadowElevationTo = 0.2f;
                        layerOffset = -0.001f;
                        reload = 15;
                        parts.addAll(
                            new AdjustableHoverPart(){{
                                x = 4;
                                y = 5;
                                sides = 4;
                                mirror = true;
                                radius = 4;
                                radiusTo = 7;
                                phase = 100;
                                stroke = 2;
                                circles = 3;
                                layerOffset = -0.002f;
                                minStroke = 0.5f;
                                color = MeldPal.shark;
                            }},
                            new AdjustableHoverPart(){{
                                x = 5;
                                y = -4;
                                sides = 4;
                                mirror = true;
                                radius = 4;
                                radiusTo = 7;
                                phase = 100;
                                stroke = 2;
                                circles = 3;
                                layerOffset = -0.002f;
                                minStroke = 0.5f;
                                color = MeldPal.shark;
                            }},
                            new RegionPart(){{
                                name = Meld.prefix("shark-shadow");
                                mirror = false;
                                under = true;
                            }}
                        );
                    }}
            );

            //actually im gona make sharks faster on fluids cause like... fluids
            abilities.addAll(
                    new SolidSpeedAbility(){{
                        warmupSpeed = 2/60f;
                        speedMultiplier = 1/1.5f;
                    }},
                    new SlipstreamHullAbility()
            );

            engines.add(
                    new MeldUnitType.ActivationEngine(8, -7.75f, 2.2f, 315, 0, 1, 1f, 4),
                    new MeldUnitType.ActivationEngine(-8, -7.75f, 2.2f, 225, 0, 1, 1f, 4)
            );

            immunities.addAll(MeldStatusEffects.drenched);

            constructor = ElevationMoveUnit::create;
        }};

        cannonOverseer = new MeldUnitType("cannon-overseer"){{
            float IR = 120;

            health = 800;
            lifetime = 360 * 6;
            speed = 0;
            flying = true;

            isEnemy = false;
            useUnitCap = false;
            createWreck = false;
            createScorch = false;
            allowedInPayloads = false;
            physics = false;
            bounded = false;
            hidden = true;
            playerControllable = false;
            canDrown = false;

            hoverable = true;
            hovering = true;

            faceTarget = false;
            targetable = true;
            drawMinimap = false;

            fogRadius = 10;
            lightRadius = 80;
            lightOpacity = 1;
            deathSound = Sounds.none;
            deathExplosionEffect = Fx.none;


            engineSize = 0;
            shadowElevation = 0;
            drawCell = drawBody = false;
            drawSoftShadow = false;

            weapons.addAll(
                    new RepairBeamWeapon(){{
                        mirror = false;
                        rotate = true;
                        rotateSpeed = 9;
                        x = 0;
                        y = 0;
                        shootY = 2.5f;
                        beamWidth = 0.5f;

                        repairSpeed = 2;
                        targetInterval = 1;
                        targetSwitchInterval = 1;

                        reload = 20;
                        targetUnits = true;
                        targetBuildings = true;
                        controllable = false;
                        laserColor = healColor = MeldPal.blobPink;
                        bullet = new BulletType(){{
                            maxRange = IR;
                        }};
                    }}
            );

            parts.addAll(
                    new ShapePart(){{
                        x = y = 0;
                        circle = true;
                        radius = 4;
                        radiusTo = 0;
                        layer = Layer.effect;
                        color = MeldPal.blobPink;
                        colorTo = MeldPal.accentClear;
                        progress = PartProgress.life.delay(0.8f).curve(Interp.pow5);
                    }},
                    new HaloPart(){{
                        tri = true;
                        haloRadius = 8;
                        radius = 4;
                        radiusTo = 0;
                        triLength = 8;
                        triLengthTo = 0;
                        shapes = 3;
                        color = MeldPal.blobPink;
                        haloRotateSpeed = 2;
                        layer = Layer.effect;
                        progress = PartProgress.life.compress(0.5f, 0.8f).curve(Interp.pow5In);
                    }},
                    new HaloPart(){{
                        tri = true;
                        haloRadius = 14;
                        radius = 4;
                        radiusTo = 0;
                        triLength = 6;
                        triLengthTo = 0;
                        shapes = 3;
                        color = MeldPal.blobPink;
                        haloRotateSpeed = 1;
                        layer = Layer.effect;
                        progress = PartProgress.life.delay(0.8f).curve(Interp.pow5In);
                    }},
                    //Sonar
                    new ShapePart(){{
                        x = y = 0;
                        progress = PartProgress.life;
                        circle = true;
                        hollow = true;
                        radius = 0;
                        radiusTo = IR;
                        stroke = 6;
                        strokeTo = 0;
                        layer = Layer.buildBeam;
                        color = MeldPal.blobPink;
                    }},

                    new ShapePart(){{
                        x = y = 0;
                        progress = PartProgress.life.curve(Interp.pow10Out);
                        circle = true;
                        hollow = true;
                        radius = 0;
                        radiusTo = IR;
                        stroke = 8;
                        strokeTo = 0;
                        layer = Layer.buildBeam;
                        color = MeldPal.blobPink;
                    }},

                    new ShapePart(){{
                        x = y = 0;
                        progress = PartProgress.life.curve(Interp.pow10In);
                        circle = true;
                        hollow = true;
                        radius = IR;
                        radiusTo = IR;
                        stroke = 4;
                        strokeTo = 0;
                        layer = Layer.buildBeam;
                        color = MeldPal.blobPink;
                    }}
            );

            abilities.addAll(
                    new RegenAbility(){{
                        amount = 0.5f;
                    }},
                    new StatusFieldAbility(MeldStatusEffects.rally, IR, 5, IR){{
                        activeEffect = Fx.none;
                    }}
            );
            constructor = TimedKillUnit::create;
        }};

        blob = new MeldUnitType("blob"){{
            health = 200;
            armor = -1;
            segments = 0;

            drag = 0.2f;
            accel = 0.12f;
            speed = 1.2f;
            drownTimeMultiplier = 5;

            drawCell = false;

            weapons.add(
                    new Weapon(){{
                        x = y = 0;
                        hitSize = 6;

                        shootWarmupSpeed = 1/6f/60f;
                        linearWarmup = true;
                        minWarmup = 1;
                        shootCone = 360;
                        mirror = false;
                        rotate = true;
                        rotateSpeed = 0;

                        bullet = new ExplosionBulletType(){{
                            float radius = 48;
                            splashDamageRadius = radius;

                            splashDamage = 500;

                            shootEffect = new MultiEffect(
                                    new Effect(30, e -> {
                                        Fill.light(e.x, e.y, 10, radius * e.fin(), Tmp.c1.set(MeldPal.flamePink).a(e.foutpowdown()), Tmp.c2.set(MeldPal.flamePink).a(0));

                                        e.scaled(12, e1 -> {
                                            Draw.color(Color.white);
                                            Draw.alpha(0.8f * e1.fout());
                                            Lines.stroke(1 + e1.fin() * 6.5f);
                                            Lines.circle(e.x, e.y, e1.fin() * 48 + 14);
                                        });
                                    }),
                                    new ParticleEffect(){{
                                        length = 35;
                                        particles = 3;
                                        lifetime = 60;
                                        sizeFrom = 5;
                                        sizeTo = 0;

                                        interp = Interp.pow2Out;
                                        sizeInterp = Interp.pow5In;
                                        colorFrom = MeldPal.flamePink;
                                    }}
                            );
                        }};

                        parts.add(
                                new RegionPart(){{
                                    name = Meld.prefix("blob-glow");
                                    outline = false;
                                    progress = PartProgress.warmup.curve(Interp.sine);
                                    growProgress = PartProgress.warmup.compress(0.8f, 1).curve(Interp.pow5In);
                                    growX = 1;
                                    growY = 1;
                                    blending = Blending.additive;
                                    color = Color.clear;
                                    colorTo = Color.white;
                                }}
                        );
                    }}
            );

            constructor = CrawlUnit::create;
        }};

        afraig = new UnitType("afraig"){{
            outlineColor = Color.clear;
            speed = 1.4f;
            health = 200;
            armor = 45;

            drownTimeMultiplier = 0.6f;
            drag = 0.14f;
            accel = 0.24f;

            hitSize = 14;
            rotateSpeed = 6;
            faceTarget = false;

            drawCell = false;

            legCount = 4;
            legLength = 16;
            legForwardScl = 1.25f;
            legMaxLength = 2.2f;
            legBaseOffset = 2.5f;
            baseLegStraightness = 0.35f;
            legStraightLength = 0.15f;
            legContinuousMove = true;
            lockLegBase = true;
            legGroupSize = 2;

            legMoveSpace = 1.5f;
            allowLegStep = true;
            legPhysicsLayer = false;

            deathExplosionEffect = new ParticleEffect(){{
                lifetime = 35;
                baseLength = 10;
                length = 55;
                particles = 1;
                sizeFrom = 1;
                sizeTo = 2.5f;
                sizeInterp = Interp.pow5Out;
                colorFrom = MeldPal.blobPink;
                colorTo = MeldPal.blobPinkClear;
            }};

            weapons.addAll(afraigWeapon(1.25f, -4.5f),
                    new DeathWeapon(){{
                        bullet = new ExplosionBulletType(){{
                            shootEffect = new MultiEffect(
                                    new ParticleEffect(){{
                                        lifetime = 125;
                                        baseLength = 10;
                                        length = 65;
                                        particles = 8;
                                        sizeFrom = 3;
                                        sizeTo = 0;
                                        interp = Interp.pow5Out;
                                        sizeInterp = Interp.pow5In;
                                        colorFrom = MeldPal.blobPink;
                                        colorTo = MeldPal.blobPinkClear;
                                    }},
                                    new ParticleEffect(){{
                                        lifetime = 35;
                                        baseLength = 10;
                                        length = 55;
                                        particles = 2;
                                        sizeFrom = 1;
                                        sizeTo = 2.5f;
                                        sizeInterp = Interp.pow5In;
                                        colorFrom = MeldPal.blobPink;
                                        colorTo = MeldPal.blobPinkClear;
                                    }},
                                    new WaveEffect(){{
                                        lifetime = 35;
                                        sizeFrom = 2;
                                        sizeTo = 12;
                                        strokeFrom = 1;
                                        strokeTo = 3;
                                        colorFrom = MeldPal.blobPink.cpy().a(0.54f);
                                        colorTo = Color.white.cpy().a(0);
                                    }},
                                    new WaveEffect(){{
                                        lifetime = 25;
                                        sizeFrom = 3;
                                        sizeTo = 25;
                                        strokeFrom = 2;
                                        strokeTo = 1.2f;
                                        colorFrom = MeldPal.blobPink;
                                        colorTo = MeldPal.blobPinkClear;
                                    }}
                            );
                        }};
                    }});

            constructor = LegsUnit::create;
        }};

        craig = new MeldUnitType("craig"){{
            speed = 0.9f;
            health = 320;
            drag = 0.12f;
            accel = 0.2f;
            hitSize = 16;
            rotateSpeed = 6;
            faceTarget = false;
            drawCell = false;

            legCount = 4;
            legLength = 10;
            lockLegBase = true;
            legContinuousMove = true;
            legGroupSize = 1;

            legMoveSpace = 2;
            allowLegStep = true;
            legPhysicsLayer = false;

            deathExplosionEffect = new MultiEffect(
                    new ParticleEffect(){{
                        lifetime = 35;
                        baseLength = 10;
                        length = 35;
                        particles = 1;
                        sizeFrom = 1;
                        sizeTo = 2.5f;
                        sizeInterp = Interp.pow5Out;
                        colorFrom = MeldPal.blobPink;
                        colorTo = MeldPal.blobPinkClear;
                    }},
                    new ParticleEffect(){{
                        lifetime = 25;
                        baseLength = 10;
                        particles = 2;
                        length = 27;
                        sizeFrom = 2.5f;
                        sizeTo = 0;
                        colorFrom = MeldPal.blobPink;
                        colorTo = MeldPal.blobPinkClear;
                    }}
            );

            immunities.addAll(MeldStatusEffects.aspectBurn);

            weapons.addAll(craigWeapon(-3, 0));

            constructor = LegsUnit::create;
        }};

        braig = new MeldUnitType("braig"){{
            outlineColor = Color.clear;
            speed = 0.8f;

            health = 1200;
            drag = 0.12f;
            accel = 0.2f;

            hitSize = 26;
            rotateSpeed = 6;
            faceTarget = false;

            drawCell = false;

            legCount = 8;
            legLength = 20;
            lockLegBase = true;
            legContinuousMove = true;
            legGroupSize = 2;
            legExtension = 4;
            legLengthScl = 0.9f;
            legPairOffset = 5;

            legMoveSpace = 3;
            allowLegStep = true;
            legPhysicsLayer = false;

            weapons.addAll(
                    craigWeapon(-6, 2),
                    new BraigWeapon(6, -3),
                    new DeathWeapon(){{
                        bullet = new ExplosionBulletType(250, 35){{
                            shootEffect = new MultiEffect(
                                    new ParticleEffect(){{
                                        lifetime = 125;
                                        baseLength = 10;
                                        length = 65;
                                        particles = 8;
                                        sizeFrom = 3;
                                        sizeTo = 0;
                                        interp = Interp.pow5Out;
                                        sizeInterp = Interp.pow5In;
                                        colorFrom = MeldPal.blobPink;
                                        colorTo = MeldPal.blobPinkClear;
                                    }},
                                    new ParticleEffect(){{
                                        lifetime = 45;
                                        baseLength = 10;
                                        particles = 12;
                                        length = 85;
                                        sizeFrom = 1.5f;
                                        sizeTo = 0;
                                        colorFrom = MeldPal.blobPink;
                                        colorTo = MeldPal.blobPinkClear;
                                    }},
                                    new ParticleEffect(){{
                                        lifetime = 85;
                                        baseLength = 10;
                                        particles = 9;
                                        line = true;
                                        strokeFrom = 1.2f;
                                        strokeTo = 0.5f;
                                        lenFrom = 7;
                                        lenTo = 4;
                                        length = 45;
                                        colorFrom = MeldPal.blobPink;
                                        colorTo = MeldPal.blobPinkClear;
                                    }},
                                    new WaveEffect(){{
                                        lifetime = 15;
                                        sizeFrom = 15;
                                        sizeTo = 35;
                                        strokeFrom = 2;
                                        strokeTo = 1.2f;
                                        colorFrom = MeldPal.blobPink;
                                        colorTo = MeldPal.blobPink;
                                    }}
                            );
                        }};
                    }}
            );
            abilities.addAll(
                    new DeathBirthAbility(){{
                        unit = craig;
                        amount = 2;
                    }}
            );

            deathExplosionEffect = new MultiEffect(
                    new ParticleEffect(){{
                        lifetime = 75;
                        baseLength = 10;
                        length = 35;
                        particles = 1;
                        sizeFrom = 1;
                        sizeTo = 1.5f;
                        sizeInterp = Interp.pow5Out;
                        colorFrom = MeldPal.blobPink;
                        colorTo = MeldPal.blobPinkClear;
                    }},
                    new ParticleEffect(){{
                        lifetime = 45;
                        baseLength = 10;
                        length = 14;
                        particles = 2;
                        sizeFrom = 1.5f;
                        sizeTo = 0;
                        interp = Interp.pow5Out;
                        sizeInterp = Interp.pow2In;
                        colorFrom = MeldPal.blobPink;
                        colorTo = MeldPal.blobPinkClear;
                    }},
                    new WaveEffect(){{
                        lifetime = 9;
                        sizeFrom = 1;
                        sizeTo = 8;
                        strokeFrom = 2;
                        strokeTo = 1.2f;
                        colorFrom = MeldPal.blobPink;
                        colorTo = MeldPal.blobPink;
                    }}
            );

            immunities.addAll(MeldStatusEffects.aspectBurn);

            constructor = LegsUnit::create;
        }};

        pointago = new MeldUnitType("pointago"){{
            speed = 0.8f;

            health = 1600;

            drag = 0.12f;
            accel = 0.2f;
            range = 40;

            hitSize = 26;
            rotateSpeed = 6;
            faceTarget = true;

            drawCell = false;

            legCount = 6;
            legLength = 24;

            legBaseOffset = 12;

            lockLegBase = true;
            legContinuousMove = true;

            legGroupSize = 2;
            legExtension = 4;
            legLengthScl = 0.9f;
            legPairOffset = 5;

            legMoveSpace = 3;
            allowLegStep = true;
            legPhysicsLayer = false;

            weapons.addAll(
                    new PointagoWeapon(30f/4, 2){{
                        rotateSpeed = 0.5f;
                        rotationLimit = 5;
                        shootCone = 180;
                    }},
                    new PointagoWeapon(-36f/4, -11f/4){{
                        rotateSpeed = 0.5f;
                        rotationLimit = 5;
                        shootCone = 180;
                    }}
            );

            abilities.addAll(
                    new BezerkAbility(){{
                        deathBomb = MeldBullets.pulsarBlast;
                        bezerkTime = 30;
                    }}
            );

            immunities.addAll(MeldStatusEffects.aspectBurn);
            constructor = LegsUnit::create;
        }};

        globkin = new UnitType("globkin"){{
            outlineColor = Color.clear;
            speed = 0.7f;

            health = 2400;
            armor = 8;

            drag = 0.12f;
            accel = 0.2f;

            hitSize = 22;
            rotateSpeed = 5.25f;
            faceTarget = true;

            drawCell = false;

            legCount = 6;
            legLength = 30;
            legGroupSize = 2;
            legLengthScl = 0.9f;
            legBaseOffset = 12;

            legMoveSpace = 1.2f;
            allowLegStep = true;
            legContinuousMove = true;
            legPhysicsLayer = false;

            weapons.add(
                    new BraigWeapon(0.25f, -11){{
                        shootStatus = StatusEffects.none;
                        reload /= 2f;
                        controllable = aiControllable = false;
                        autoTarget = true;
                        targetSwitchInterval = 20;
                        targetInterval = 15;
                    }},
                    new Weapon(""){{
                        x = 16.25f;
                        y = 4.25f;
                        shootY = 11;
                        reload = 160;
                        shootCone = 15;

                        rotate = true;
                        rotateSpeed = 1.5f;
                        rotationLimit = 60;
                        recoil = 6.5f;

                        mirror = true;
                        alternate = false;
                        parentizeEffects = false;

                        shootStatus = MeldStatusEffects.stunned;

                        shootStatusDuration = 45;

                        layerOffset = -0.01f;
                        predictTarget = false;
                        shoot = new ShootSpread() {{
                            firstShotDelay = 35;
                        }};

                        bullet = new TransitionBulletType() {{
                            fragBullets = 6;
                            fragRandomSpread = 45;
                            recoil = 4.5f;
                            rangeOverride = 160;
                            spawnBullets.addAll(
                                    new TransitionBulletType(){{
                                        fragRandomSpread = 45;
                                        recoil = 4.5f;
                                        fragBullets = 12;

                                        fragBullet = new BasicBulletType(8.5f, 1.5f, "meld-clump"){{
                                            width = 5;
                                            height = 9;
                                            shrinkX = shrinkY = 1;

                                            splashDamage = 12;
                                            splashDamageRadius = 25;

                                            status = MeldStatusEffects.stunned;
                                            statusDuration = 35;

                                            pierce = true;
                                            pierceCap = 2;
                                            speed = 8.5f;
                                            drag = 0.01f;

                                            lifetime = 45;

                                            knockback = 5.5f;

                                            keepVelocity = false;
                                        }};
                                    }}
                            );

                            fragBullet = new ArtilleryBulletType(){{
                                width = 16;
                                height = 19;

                                damage = 0;
                                collides = true;
                                speed = 4.7f;
                                lifetime = 52;
                                knockback = 12;

                                fragBullets = 1;
                                fragBullet = new BulletType(){{
                                    spawnUnit = MeldUnits.blob;
                                }};
                                keepVelocity = false;
                            }};
                        }};

                        parts.addAll(
                                new RegionPart(){{
                                    name = Meld.prefix("globkin-gun-blob");
                                    x = 0;
                                    y = 42/4f;
                                    growX = -0.5f;
                                    growY = 0.25f;
                                    moveY = -42/4f;
                                    //lock the progress until after charge is done
                                    growProgress = p -> Mathf.zero(PartProgress.charge.getClamp(p)) ? PartProgress.reload.curve(Interp.pow5).getClamp(p) : 0;
                                    progress = p -> Mathf.zero(PartProgress.charge.getClamp(p)) ? PartProgress.reload.curve(Interp.pow2In).getClamp(p) : 0;

                                    moves.addAll(
                                            new PartMove(){{
                                                gx = -0.25f;
                                                gy = 0.5f;
                                                progress = PartProgress.charge.curve(Interp.pow5In);
                                            }},
                                            new PartMove(){{
                                                y = -25;
                                                gx = -0.25f;
                                                gy = 0.25f;
                                                progress = PartProgress.charge.curve(Interp.pow10In);
                                            }}
                                    );

                                }},
                                new RegionPart(){{
                                    name = Meld.prefix("globkin-gun");
                                    moves.addAll(
                                            new PartMove(){{
                                                gx = -0.25f;
                                                gy = 0.5f;
                                                progress = PartProgress.charge.curve(Interp.pow5In);
                                            }},
                                            new PartMove(){{
                                                gy = -0.25f;
                                                //lock the progress until after charge is done
                                                progress = p -> Mathf.zero(PartProgress.charge.getClamp(p)) ? PartProgress.reload.curve(Interp.pow2In).get(p) : 0;
                                            }}
                                    );
                                }}
                        );
                    }}
            );

            immunities.addAll(MeldStatusEffects.lacerated);
            constructor = LegsUnit::create;
        }};

        jilla = new UnitType("jilla"){{
            outlineColor = Color.clear;
            speed = 1.2f;

            health = 200;

            drag = 0.12f;
            accel = 0.2f;
            range = 40;

            hitSize = 16;
            rotateSpeed = 6;
            faceTarget = true;

            legPhysicsLayer = false;

            segments = 3;
            segmentMag = 0.5f;
            drawCell = drawBody = false;

            aiController = HugAI::new;

            deathExplosionEffect = Fx.none;

            weapons.add(
                    new Weapon(){{
                        x = y = 0;
                        shootY = 8;
                        shootCone = 180;
                        reload = 60;
                        mirror = alternate = false;
                        continuous = alwaysContinuous = true;
                        shootSound = Sounds.shootSublimate;

                        bullet = new ContinuousFlameBulletType(){{
                            damage = 8;
                            flareLength = 0;
                            length = 8;
                            rangeOverride = 40;
                            width = 1.5f;
                            knockback = pierceCap = 1;

                            colors = new Color[]{
                                    Color.valueOf("f9e1f343"),
                                    Color.valueOf("ee5de9a9"),
                                    Color.valueOf("ef85e3e3"),
                                    Color.valueOf("d22fee"),
                                    Color.white
                            };
                        }};
                    }}
            );
            immunities.addAll(MeldStatusEffects.aspectBurn);

            constructor = CrawlUnit::create;
        }};

        billa = new MeldUnitType("billa"){{
            health = 450;

            armor = 120;
            drag = 0.25f;
            accel = 0.35f;
            speed = 1.5f;

            hitSize = 32;
            rotateSpeed = 8;
            faceTarget = true;

            hovering = true;

            legCount = 6;
            legLength = 60;
            legMaxLength = 1;
            legGroupSize = 2;
            legSpeed = 1;
            legForwardScl = 3;
            legBaseOffset = 8;
            drawCell = false;
            legPhysicsLayer = true;

            deathExplosionEffect = Fx.none;

            weapons.add(
                    new Weapon(Meld.prefix("billa-crystals")){{
                        x = y = 0;
                        mirror = alternate = rotate = false;
                        reload = 14;

                        shootCone = 180;
                        recoil = 0;

                        shootY = 12.5f;
                        continuous = alwaysContinuous = true;
                        shootSound = Sounds.shootSublimate;

                        parts.addAll(
                                new RegionPart("-glow"){{
                                    outline = false;
                                    progress = PartProgress.warmup;
                                    color = Color.clear;
                                    colorTo = Color.white;
                                    blending = Blending.additive;
                                }}
                        );

                        shootStatus = MeldStatusEffects.sentry;
                        shootStatusDuration = 4;

                        bullet = new ContinuousFlameBulletType(){{
                            lifetime = 85;
                            damage = 13;
                            length = 75;
                            width = 1.5f;
                            knockback = 3;
                            pierceCap = 3;
                            flareLength = 0;
                            rangeOverride = 55;
                            recoil = 0.01f;
                            hitUnder = true;

                            colors = new Color[]{
                                    Color.valueOf("f9e1f343"),
                                    Color.valueOf("ee5de9a9"),
                                    Color.valueOf("ef85e3e3"),
                                    Color.valueOf("d22fee"),
                                    Color.white
                            };
                        }};
                    }},
                    new Weapon(){{
                        alternate = rotate = false;
                        mirror = true;
                        reload = 14;
                        x = 10;
                        y = 3.75f;
                        baseRotation = -45;

                        shootCone = 180;
                        recoil = 0;

                        shootY = 0;
                        continuous = alwaysContinuous = true;
                        shootSound = Sounds.shootSublimate;
                        parts.addAll(
                                new RegionPart("-glow"){{
                                    outline = false;
                                    progress = PartProgress.warmup;
                                    color = Color.clear;
                                    colorTo = Color.white;
                                    blending = Blending.additive;
                                }}
                        );

                        shootStatus = MeldStatusEffects.sentry;
                        shootStatusDuration = 4;

                        bullet = new ContinuousFlameBulletType(){{
                            lifetime = 85;
                            damage = 10;
                            length = 75;
                            width = 0.5f;
                            knockback = 3;
                            pierceCap = 3;
                            flareLength = 0;
                            rangeOverride = 55;
                            recoil = 0.01f;
                            hitUnder = true;

                            colors = new Color[]{
                                    Color.valueOf("f9e1f343"),
                                    Color.valueOf("ee5de9a9"),
                                    Color.valueOf("ef85e3e3"),
                                    Color.valueOf("d22fee"),
                                    Color.white
                            };
                        }};
                    }}
            );

            abilities.addAll(
                    new DeathBirthAbility(MeldUnits.jilla, 3),
                    new SpawnRushAbility()
            );

            constructor = LegsUnit::create;
        }};

        scorcher = new MeldUnitType("scorcher"){{
            speed = 0.4f;

            health = 200;
            drownTimeMultiplier = 0.35f;

            drag = 0.08f;
            accel = 0.15f;
            range = 40;

            hitSize = 12;
            rotateSpeed = 12;
            faceTarget = true;

            legCount = 4;
            legLength = 10;
            legMaxLength = 1.2f;
            legGroupSize = 2;
            legSpeed = 1;
            legForwardScl = 2;
            legBaseOffset = -1f;

            drawCell = false;
            legPhysicsLayer = false;


            weapons.add(
                new Weapon(Meld.prefix("scorcher-crystal")){{
                    x = y = 0;
                    mirror = alternate = rotate = false;
                    reload = 14;

                    shootCone = 15;
                    recoil = 0;

                    shootY = 8;
                    continuous = alwaysContinuous = true;
                    shootSound = Sounds.shootSublimate;
                    parts.addAll(
                            //TODO: wtf is this naming
                            new RegionPart("-shine"){{
                                outline = false;
                                progress = PartProgress.warmup;
                                color = Color.clear;
                                colorTo = Color.white;
                                blending = Blending.additive;
                            }}
                    );

                    shootStatus = MeldStatusEffects.sentry;
                    shootStatusDuration = 4;

                    bullet = new ContinuousFlameBulletType(){{
                        lifetime = 85;
                        damage = 13;
                        length = 35;
                        width = 3.5f;
                        knockback = 3;
                        pierceCap = 3;
                        flareLength = 8;
                        rangeOverride = 55;
                        recoil = -0.05f;
                        hitUnder = true;

                        colors = new Color[]{
                                Color.valueOf("f9e1f343"),
                                Color.valueOf("ee5de9a9"),
                                Color.valueOf("ef85e3e3"),
                                Color.valueOf("d22fee"),
                                Color.white
                        };
                    }};
                }}
            );

            constructor = LegsUnit::create;
        }};

        kathid = new MeldUnitType("kathid"){{
            shadowElevation = 0.1f;
            speed = 0.7f;

            health = 600;
            armor = 50;
            drownTimeMultiplier = 0.45f;

            drag = 0.12f;
            accel = 0.2f;

            hitSize = 16;
            rotateSpeed = 3.5f;

            useEngineElevation = false;
            faceTarget = true;

            legCount = 4;
            legLength = 15;
            lockLegBase = true;
            legContinuousMove = true;
            legGroupSize = 2;

            legMoveSpace = 3;
            allowLegStep = true;
            legPhysicsLayer = false;

            deathExplosionEffect = Fx.none;

            parts.addAll(
                    new RegionPart(){{
                        name = Meld.prefix("kathid-crystal-heat");
                        progress = PartProgress.warmup;
                        color = Color.valueOf("11111100");
                        colorTo = Color.valueOf("111111");
                        blending = Blending.additive;
                        outline = false;
                    }}
            );

            weapons.addAll(
                    new Weapon(){{
                        x = y = 0;
                        shootY = 8.5f;
                        mirror = alternate = false;
                        controllable = alwaysContinuous = true;
                        recoil = 0;
                        shootCone = 10;

                        shootStatus = MeldStatusEffects.sentry;
                        shootStatusDuration = 25;
                        shootSound = Sounds.shootSublimate;

                        bullet = new ContinuousFlameBulletType(){{
                            lifetime = 25;
                            damage = 13;
                            flareLength = 12;
                            length = 115;
                            width = 1.7f;
                            knockback = 1;
                            pierceCap = 2;

                            colors = new Color[]{
                                    Color.valueOf("f9e1f343"),
                                    Color.valueOf("ee5de9a9"),
                                    Color.valueOf("ef85e3e3"),
                                    Color.valueOf("d22fee"),
                                    Color.white
                            };
                        }};
                    }}
            );
            immunities.addAll(MeldStatusEffects.lacerated);

            constructor = LegsUnit::create;
        }};
    }

    public static Weapon craigWeapon(float wx, float wy){
        return new Weapon(Meld.prefix("craig-gun")){{
            x = wx;
            y = wy;
            shootY = 5;
            rotateSpeed = 15;
            reload = 15;
            shootCone = 60;
            rotate = true;
            shootStatus = MeldStatusEffects.sentry;
            shootStatusDuration = 7.5f;
            mirror = false;
            alternate = false;
            parentizeEffects = false;
            bullet = new BasicBulletType(){{
                sprite = Meld.prefix("glob");
                speed = 4;
                lifetime = 20;
                width = 6;
                height = 9;
                damage = 5;
                knockback = 1.5f;
                hitEffect = despawnEffect = Fx.none;
                impact = true;
            }};
        }};
    }

    //not quite sure why you'd want one of theese on another unit but it's there
    public static Weapon afraigWeapon(float wx, float wy){
        return new Weapon(Meld.prefix("afraig-spurter")){{
            x = wx;
            y = wy;
            shootY = 4.5f;
            rotateSpeed = 15;
            reload = 180;
            shootCone = 60;
            rotate = true;

            shootStatus = MeldStatusEffects.spurting;
            shootStatusDuration = 120;

            mirror = false;
            alternate = false;
            parentizeEffects = false;

            shoot = new ShootSpread(){{
                shots = 18;
                shotDelay = 5;
                spread = 0;
            }};

            bullet = new BulletType(){{
                lightRadius = 0;
                keepVelocity = false;
                damage = 9;
                speed = 5;
                lifetime = 15;
                knockback = 1.5f;
                impact = true;
                pierce = true;
                smokeEffect = Fx.none;
                recoil = 0.5f;

                despawnEffect = Fx.none;
                hitEffect = new ParticleEffect(){{
                    lifetime = 24;
                    particles = 3;
                    baseLength = 2;
                    length = 12;
                    line = true;
                    strokeFrom = 1.75f;
                    strokeTo = 0;
                    lenFrom = 3;
                    lenTo = 1;
                    colorFrom = MeldPal.flamePink;
                    colorTo = MeldPal.flamePinkDark.cpy().a(0);
                }};

                shootEffect = new MultiEffect(
                        new ParticleEffect(){{
                            cone = 25;
                            lifetime = 8;
                            line = true;

                            baseLength = 0;
                            particles = 8;

                            lenFrom = 2;
                            lenTo = 3.5f;
                            strokeFrom = 3;
                            strokeTo = 1.2f;

                            length = 35;

                            colorFrom = MeldPal.flamePink;
                            colorTo = MeldPal.flamePinkDark;
                            interp = Interp.pow5In;
                            sizeInterp = Interp.pow2In;
                            followParent = false;
                        }},
                        new ParticleEffect(){{
                            cone = 4;
                            lifetime = 15;
                            baseLength = 0;
                            particles = 14;
                            length = 65;
                            sizeFrom = 0;
                            sizeTo = 4.2f;
                            colorFrom = MeldPal.flamePink;
                            colorTo = MeldPal.flamePinkDark.cpy().a(0);
                            interp = Interp.pow3Out;
                            sizeInterp = Interp.pow2In;
                            followParent = false;
                        }},

                        new ParticleEffect(){{
                            cone = 5;
                            lifetime = 24;
                            baseLength = 0;
                            particles = 12;
                            length = 85;
                            sizeFrom = 3.3f;
                            sizeTo = 0;
                            colorFrom = MeldPal.flamePink;
                            colorTo = MeldPal.flamePinkDark.cpy().a(0);
                            interp = Interp.pow3Out;
                            sizeInterp = Interp.pow2In;
                            followParent = false;
                        }}
                );
            }};
        }};
    }

    public static Weapon braigWeapon(float wx, float wy){
        return new BraigWeapon(wx, wy);
    }
}
