package meld.entities.bullet;

import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.type.StatusEffect;

public class TransitionBulletType extends BulletType {
    public StatusEffect spawnStatus;
    public float spawnStatusDuration = 0;

    public TransitionBulletType(){
        super();
        speed = 0.00001f;
        keepVelocity = false;
        instantDisappear = true;
        collides = absorbable = reflectable = hittable = false;
        shootEffect = chargeEffect = despawnEffect = hitEffect = Fx.none;
    }

    @Override
    public void createUnits(Bullet b, float x, float y) {
        super.createUnits(b, x, y);
    }
}
