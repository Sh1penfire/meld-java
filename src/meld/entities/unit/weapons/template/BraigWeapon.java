package meld.entities.unit.weapons.template;

import meld.Meld;
import meld.content.MeldStatusEffects;
import meld.entities.unit.weapons.BaseWeapon;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.pattern.ShootSpread;

public class BraigWeapon extends BaseWeapon {
    public BraigWeapon(float wx, float wy){
        super(Meld.prefix("braig-cannon"));
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
            speed = 4;
            lifetime = 75;
            width = 10;
            height = 12;
            damage = 10;
            splashDamage = 25;
            splashDamageRadius = 25;
            knockback = 6;
            shootEffect = Fx.shootBig;
            hitEffect = Fx.explosion;
            impact = true;
        }};
    }
}
