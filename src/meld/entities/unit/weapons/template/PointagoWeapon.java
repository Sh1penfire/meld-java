package meld.entities.unit.weapons.template;

import meld.Meld;
import meld.content.MeldStatusEffects;
import meld.entities.unit.weapons.BaseWeapon;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootSpread;
import mindustry.graphics.Layer;

public class PointagoWeapon extends BaseWeapon {
    public PointagoWeapon(float wx, float wy){
        super(Meld.prefix("pointago-sniper"));

        x = wx;
        y = wy;

        shootY = 8;
        rotateSpeed = 2.5f;
        reload = 600;

        shootCone = 60;
        rotate = true;
        mirror = alternate = false;
        shootStatus = MeldStatusEffects.sentry;
        shootStatusDuration = 180;

        shoot = new ShootSpread(){{
            shots = 3;
            shotDelay = 5;
            spread = 1;
        }};

        bullet = new BasicBulletType(3.5f, 65, Meld.prefix("clump")){{
            layer = Layer.fogOfWar + 1;
            drag = -0.02f;
            lifetime = 72;
            rangeOverride = 300;

            pierce = true;
            pierceCap = 2;

            width = 5;
            height = 24;
            knockback = 25;
            impact = true;
            trailEffect = Fx.smoke;
            shootEffect = Fx.shootBig;
            hitEffect = Fx.explosion;
        }};

        parts.addAll(
                new RegionPart("-barrel"){{
                    progress = PartProgress.reload;
                    moveY = -3;
                    moves.addAll(
                            new PartMove(PartProgress.reload, 0, -2, 0)
                    );
                }},
                new RegionPart()
        );
    }
}
