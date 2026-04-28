package meld.world.blocks.fluid;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Tmp;
import meld.Meld;
import meld.content.MeldLiquids;
import meld.entities.bullet.TransitionBulletType;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.graphics.Drawf;

public class AspectBomb extends VisualAspectPipe{
    public AspectBomb(String name) {
        super(name);
        buildTime = 0;
        rebuildable = false;

        destroyBullet = new TransitionBulletType(){{
            lifetime = 30;
            spawnBullets.addAll(
                    new TransitionBulletType(){{
                        fragBullets = 12;
                        fragLifeMin = 0.5f;
                        fragBullet = new BasicBulletType(3, 3, Meld.prefix("clump")){{
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
                    }},
                    new TransitionBulletType(){{
                        fragBullets = 24;
                        fragBullet = new BasicBulletType(8, 3, Meld.prefix("clump")){{
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
        destroyBulletSameTeam = false;
    }

    public float pressureDamage = 5f;
    public float explosionPressure = 0.5f;

    public class AspectBombBuild extends VisualPipeBuild{
        @Override
        public void updateTile() {
            super.updateTile();
            if(liquids.currentAmount()/liquidCapacity > explosionPressure) damageContinuousPierce(pressureDamage);
        }
    }
}
