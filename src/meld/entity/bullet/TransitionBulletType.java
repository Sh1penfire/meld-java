package meld.entity.bullet;

import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;

public class TransitionBulletType extends BulletType {
    public TransitionBulletType(){
        super();
        keepVelocity = false;
        instantDisappear = true;
        collides = absorbable = reflectable = hittable = false;
        shootEffect = chargeEffect = despawnEffect = hitEffect = Fx.none;
    }
}
