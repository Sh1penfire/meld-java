package meld.entities.bullet;

import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;

//Blasts sticky bullets out of units, has a special effect when doing so
public class OutflowBulletType extends BasicBulletType {

    public BulletType outflowBullet;
    public float outflowDamage = 10;

    @Override
    public void init() {
        super.init();
        if(setDefaults && outflowBullet == null) outflowBullet = fragBullet;
    }

    @Override
    public void despawned(Bullet b) {
        super.despawned(b);
        outflow(b, b.x, b.y);
    }

    @Override
    public void hit(Bullet b) {
        super.hit(b);
    }

    @Override
    public void createFrags(Bullet b, float x, float y) {
        super.createFrags(b, x, y);
    }

    public void outflow(Bullet b, float x, float y){

        Groups.bullet.intersect(x, y, splashDamageRadius, splashDamageRadius, bullet -> {
            if(bullet.stickyTarget != null){
                this.outflowBullet.create(b, x, y, bullet.rotation(), Mathf.random(this.fragVelocityMin, this.fragVelocityMax), Mathf.random(this.fragLifeMin, this.fragLifeMax));
                bullet.remove();
            }
        });

    }
}
