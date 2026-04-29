package meld.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.util.Tmp;
import meld.Meld;
import meld.entities.bullet.RicochetBulletType;
import meld.entities.bullet.TransitionBulletType;
import meld.graphics.MeldPal;
import meld.world.blocks.fluid.AspectBomb;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.graphics.Drawf;

public class MeldBullets {

    //Bullets from non weapon/turret sources
    public static BulletType

            pulsarBlast, pulsarShrapnel,
            bombShrapnel, bombShrapnelAspect, aspectBombExplosion;

    //Weapon bullets
    public static BulletType

            sunderDebris, sunderGlass,
            shredDebris, shredSilver,
            vincaQuartz;



    public static void load(){

        pulsarShrapnel = new BasicBulletType(){{
            sprite = Meld.prefix("clump");
            shrinkX = 1;
            shrinkY = 0.2f;

            width = 5;
            height = 12;
            shrinkInterp = Interp.pow2In;
            speed = 12;
            drag = 0.02f;

            lifetime = 30;
            damage = 5;
            pierce = true;
            pierceBuilding = true;
            pierceDamageFactor = 0.5f;
            lightRadius = 0;

            lightningDamage = 5;
            lightning = 1;
            lightningLength = 12;

            setDefaults = false;
            despawnEffect = hitEffect = Fx.none;
            despawnHit = false;

            collideTerrain = true;
        }};

        pulsarBlast = new ExplosionBulletType(){{
            lightningDamage = 2;
            lightning = 12;
            lightningLength = 48;

            fragOffsetMin = fragOffsetMax = 0;
            fragLifeMin = 0.5f;

            fragBullets = 120;
            fragSpread = 3;
            fragRandomSpread = 0;
            fragBullet = pulsarShrapnel;
            killShooter = false;
        }};

        sunderDebris = new FlakBulletType(){{
            collidesGround = true;

            scaleLife = true;

            speed = 6;
            damage = 2;
            lifetime = 30;
            width = 6;
            height = 12;
            splashDamage = 15;
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
            fragBullet = new BasicBulletType(8, 3){{
                lightRadius = 0;
                lifetime = 20;

                shrinkY = 1;
                shrinkX = 1;
                shrinkInterp = Interp.pow5In;

                sticky = true;
                stickyExtraLifetime = 60;
                pierce = true;
                pierceCap = 2;
                splashDamage = 1;
                splashDamageRadius = 2;

                width = 3;
                height = 8;

                collidesGround = true;
                hitEffect = despawnEffect = Fx.none;
            }};
        }};

        sunderGlass = new FlakBulletType(6, 1){{
            sprite = Meld.prefix("clump");

            frontColor = MeldPal.glassMallowsFront;
            backColor = MeldPal.glassMallowsBack;
            collidesGround = true;
            scaleLife = true;

            ammoMultiplier = 1;

            reloadMultiplier = 2;
            rangeChange = -80;

            damage = 0.5f;
            lifetime = 15;
            width = 3;
            height = 8;

            knockback = 1;

            despawnEffect = Fx.none;
            hitEffect = new MultiEffect(
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

            fragBullet = new BasicBulletType(6, 8, Meld.prefix("clump")){{
                frontColor = MeldPal.glassMallowsFront;
                backColor = MeldPal.glassMallowsBack;
                lightRadius = 0;
                speed = 8;
                lifetime = 20;

                shrinkY = 1;
                shrinkX = 1;
                shrinkInterp = Interp.pow5In;

                sticky = true;
                stickyExtraLifetime = 60;
                splashDamage = 1;
                splashDamageRadius = 2;

                width = 8;
                height = 12;

                collidesGround = true;
                hitEffect = despawnEffect = Fx.none;
            }};
        }};

        shredDebris = new BasicBulletType(){{
            damage = 2;
            lifetime = 5;
            speed = 12;
            sprite = Meld.prefix("clump");
            width = 8;
            height = 10;
            shrinkY = 0;

            despawnHit = true;

            fragLifeMin = 0.6f;
            ammoMultiplier = 2;

            fragRandomSpread = 5;
            fragBullets = 3;

            fragBullet = new BasicBulletType(12, 6, Meld.prefix("clump")){{
                lifetime = 21;
                drag = 0.01f;
                width = 6;
                height = 12;
                shrinkX = 0.7f;
                shrinkY = 0.2f;

                knockback = 0.25f;
                impact = true;

                hitEffect = Fx.none;
                despawnEffect = Fx.none;
                setDefaults = false;
                despawnHit = false;
                fragOnHit = true;

                pierce = true;
                pierceCap = 2;

                fragBullets = 3;
                fragRandomSpread = 60;

                bulletInterval = 2;

                status = MeldStatusEffects.impaled;
                statusDuration = 30;

                fragBullet = new BasicBulletType(9, 4, Meld.prefix("clump")){{
                    speed = 9;
                    damage = 0.5f;
                    lifetime = 8;
                    drag = 0.002f;
                    width = 1;
                    height = 6;
                    shrinkY = 0.2f;
                    shrinkX = 1;

                    lightRadius = 0;

                    hitEffect = Fx.none;
                    despawnEffect = Fx.none;
                    despawnHit = false;

                    sticky = true;
                    stickyExtraLifetime = 120;

                    status = MeldStatusEffects.impaled;
                    statusDuration = 5;
                }};
            }};
        }};

        shredSilver = new TransitionBulletType(){{

            fragLifeMin = 0.8f;
            reloadMultiplier = 2f;

            fragRandomSpread = 5;
            fragBullets = 1;
            ammoMultiplier = 1;

            fragBullet = new RicochetBulletType(8, 10, "shell"){{
                frontColor = Color.white;
                backColor = MeldPal.aspect;

                lifetime = 40;
                drag = 0.01f;
                width = 8;
                height = 8;
                shrinkX = 1;
                shrinkY = 1f;
                shrinkInterp = Interp.pow10Out;

                lightRadius = 0;

                splashDamage = 5;
                splashDamageRadius = 20;

                //For some reason impact + knockback on bouncy bullets doesn't work how I want it to...
                knockback = 5;

                bounceEffect = Fx.none;
                hitEffect = Fx.none;
                despawnEffect = Fx.none;
                setDefaults = false;
            }};
        }};

        vincaQuartz = new RailBulletType(){{
            hitShake = 0.5f;
            length = 120;

            lifetime = 10;
            damage = 12;
            pierceArmor = true;
            pierceDamageFactor = 1;

            setDefaults = false;
            despawnHit = false;
            hittable = absorbable = false;
            pointEffectSpace = 8;
            hitEffect = Fx.none;
            pierceEffect = Fx.none;

            status = MeldStatusEffects.impaled;
            statusDuration = 5;

            fragBullets = 2;
            fragRandomSpread = 35;
            fragVelocityMin = 0.9f;

            fragBullet = new BasicBulletType(){{
                speed = 8;

                shrinkX = shrinkY = 1;
                lightRadius = 0;
                damage = 5;
                splashDamage = 8;
                splashDamageRadius = 8;

                sprite = Meld.prefix("diamond");
                sticky = true;

                knockback = 2;
                impact = true;
                hitEffect = despawnEffect = Fx.none;

                fragBullets = 3;
                fragBullet = new BasicBulletType(){{
                    pierce = true;
                    pierceCap = 2;
                    speed = 8;
                    lifetime = 10;

                    shrinkX = shrinkY = 1;
                    lightRadius = 0;
                    damage = 5;

                    sprite = Meld.prefix("diamond");

                    knockback = 2;
                    impact = true;
                    hitEffect = despawnEffect = Fx.none;

                }};
            }};
        }};

        bombShrapnel = new BasicBulletType(8, 3, Meld.prefix("clump")){{
            lifetime = 12;

            width = 4;
            height = 18;

            hitEffect = despawnEffect = Fx.none;

            lightRadius = 0;
            fragBullets = 2;
            fragBullet = new BasicBulletType(8, 7, Meld.prefix("clump")){{
                lifetime = 12;

                width = 2;
                height = 12;

                shrinkX = 1;
                shrinkY = 0.2f;

                hitEffect = despawnEffect = Fx.none;


                sticky = true;

                stickyExtraLifetime = 600;

                lightRadius = 0;
            }};
        }};

        bombShrapnelAspect = new BasicBulletType(3, 3, Meld.prefix("clump")){{
            lifetime = 30;

            width = 8;
            height = 10;
            shrinkX = 1;
            shrinkY = 0.2f;

            hitEffect = despawnEffect = Fx.none;

            lightRadius = 0;
            fragBullets = 2;

            fragBullet = new LiquidBulletType(MeldLiquids.stormingAspect);
        }};

        aspectBombExplosion = new TransitionBulletType(){{
            lifetime = 30;
            spawnBullets.addAll(
                    new TransitionBulletType(){{
                        fragBullets = 12;
                        fragLifeMin = 0.5f;
                        fragBullet = bombShrapnelAspect;
                    }},
                    new TransitionBulletType(){{
                        fragBullets = 24;
                        fragBullet = bombShrapnel;
                    }}
            );
            splashDamage = 500;
            splashDamageRadius = 32;
            incendAmount = 50;

            hitEffect = new Effect(24, e -> {
                Draw.color(MeldLiquids.stormingAspect.color);
                Draw.alpha(e.foutpowdown() * 0.3f);
                Fill.light(e.x, e.y, 100, e.finpow() * 32, Tmp.c1.set(Tmp.c2.set(MeldLiquids.stormingAspect.color)).a(e.fout()), Tmp.c2.a(0));
                //Fill.circle(e.x, e.y, e.finpow() * 85);

                Drawf.light(e.x, e.y, e.finpow() * 14, MeldLiquids.stormingAspect.color, e.fout());

                e.scaled(8, e1 -> {
                    Draw.color(Color.white);
                    Draw.alpha(e1.foutpowdown() * 0.35f);
                    Lines.stroke(e1.fin() * 12 + 2);
                    Lines.circle(e1.x, e1.y, e1.fin() * 18 + 8);
                });
            });

            fragBullets = 4;
            fragBullet = new LiquidBulletType(MeldLiquids.stormingAspect);
        }};
    }
}
