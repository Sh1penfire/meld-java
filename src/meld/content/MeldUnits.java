package meld.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import meld.*;
import meld.unit.MeldUnitType;
import meld.unit.abilities.DeathBirthAbility;
import meld.unit.abilities.SlipstreamHullAbility;
import meld.unit.abilities.SolidSpeedAbility;
import meld.unit.weapons.DeathWeapon;
import meld.unit.weapons.ShadowVisualWeapon;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.abilities.RegenAbility;
import mindustry.entities.abilities.StatusFieldAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.HoverPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootSpread;
import mindustry.gen.ElevationMoveUnit;
import mindustry.gen.LegsUnit;
import mindustry.gen.Sounds;
import mindustry.gen.TimedKillUnit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;

public class MeldUnits {
    //player units
    public static UnitType
    bulbhead, shark,

    bulbheadOverseer,

    //enemy units
    shotgunEnemy, armoredEnemy, craig, braig;

    public static void load(){
        bulbheadOverseer = new UnitType("bulbhead-overseer"){{
            float IR = 80;

            health = 240;
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

            rotateSpeed = 7;
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
            constructor = UnitEdgeWaterMove::new;

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

            speed = 2.5f;
            health = 520;
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
                                layerOffset = -0.001f;
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
                                layerOffset = -0.001f;
                                minStroke = 0.5f;
                                color = MeldPal.shark;
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

        shotgunEnemy = new UnitType("afraig"){{
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
                                        colorFrom = MeldPal.blobPink.a(0.54f);
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

        craig = new UnitType("craig"){{
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

        braig = new UnitType("braig"){{
            speed = 0.8f;

            health = 950;
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
                    braigWeapon(6, -3),
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
    };

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
    };
    public static Weapon braigWeapon(float wx, float wy){
        return new Weapon(Meld.prefix("braig-cannon")){{
            x = wx;
            y = wy;
            shootY = 5;
            reload = 360;
            shootCone = 60;
            rotate = true;
            mirror = false;
            alternate = false;
            parentizeEffects = false;
            shootStatus = MeldStatusEffects.sentry;
            shootStatusDuration = 180;

            shoot = new ShootSpread(){{
                shots = 3;
                shotDelay = 7;
                spread = 1;
            }};

            inaccuracy = 5;

            bullet = new BasicBulletType(){{
                sprite = Meld.prefix("clump");
                pierce = true;
                pierceCap = 2;
                speed = 4;
                lifetime = 75;
                width = 6;
                height = 16;
                damage = 10;
                splashDamage = 25;
                splashDamageRadius = 25;
                knockback = 6;
                shootEffect = Fx.shootBig;
                hitEffect = Fx.explosion;
                impact = true;
            }};
        }};
    };
}
