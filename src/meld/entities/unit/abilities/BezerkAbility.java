package meld.entities.unit.abilities;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import meld.content.MeldStatusEffects;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;

public class BezerkAbility extends Ability {

    /**
        Yes this is stupid, @Link{Ability#created} only fires when a unit gets made though
     */
    protected boolean setup = false;

    //Time counts up while a unit is shooting, and counts down when the unit isn't/can't shoot
    protected float bezerkProgress;

    //Charge up until becoming BEZERK >:D
    protected float time;

    //Controls whether the unit can shoot or not, gets set to false once time = 0
    protected boolean bezerk;

    //Bezert under this hp threshold
    public float bezerkHealthf = 0.5f,
    //Time it takes to drop hp
            bezerkTime = 60;

    //Damage/sec based on bezerk%
    public float damage = 50/60;

    //Radius/damage for da boom boom prepper
    public float boomRadius = Vars.tilesize * 4, boomDamage = 200/60f;

    public float reloadMultiplier = 3, speedMultiplier = 1.2f, bezerkSpeedMulti = 2;

    public BulletType deathBomb;

    public Effect bezerkSmoke = Fx.generate;
    public Effect chargeEffect = Fx.lightningCharge;

    public float effectChance = 0.5f;


    public Seq<StatusEffect> clear = new Seq<>();
    public Seq<StatusEffect> sedated = new Seq<>();

    public BezerkAbility(){
        clear.add(MeldStatusEffects.sentry, MeldStatusEffects.impaled);
        sedated.addAll(MeldStatusEffects.lacerated);
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);

        //Dumb stupid
        if(!setup){
            setup = true;
            time = unit.healthf() < bezerkHealthf ? bezerkTime : 0;
        }

        if(unit.isShooting){
            unit.damageContinuousPierce(damage);
        }

        time = Mathf.clamp(time + (unit.healthf() < bezerkHealthf ? Time.delta : -Time.delta), 0, bezerkTime);

        //Let em know when to ZERKIN >:DDDDDD
        bezerk = time >= bezerkTime;

        sedated.each(u -> {
            //Disable zerk D:
            if(unit.hasEffect(u)) time = 0;
        });

        //Get more BEZERK the lower hp you have
        bezerkProgress = Mathf.clamp((1-unit.healthf())/bezerkHealthf, 0, 1);

        //AAAnd a bunch of random stuff going nuts
        if(bezerk){
            unit.disarmed = true;
            clear.each(unit::unapply);

            if(unit.buildOn() != null && unit.buildOn().team != unit.team || Units.nearEnemy(unit.team, unit.x, unit.y, boomRadius, boomRadius)){
                unit.damageContinuousPierce(boomDamage);
            }
        }

        if(bezerk && Mathf.chance(effectChance)){
            bezerkSmoke.at(unit.x + Mathf.random(-unit.hitSize, unit.hitSize), unit.y + Mathf.random(-unit.hitSize, unit.hitSize));
        }

        unit.reloadMultiplier *= Mathf.lerp(1, reloadMultiplier, bezerkProgress);
        unit.speedMultiplier *= Mathf.lerp(1, bezerk ? bezerkSpeedMulti : speedMultiplier, bezerkProgress);
    }

    @Override
    public void death(Unit unit) {
        if(bezerk && deathBomb != null) {
            deathBomb.create(null, unit.team, unit.x, unit.y, unit.vel.angle());
        }
        super.death(unit);
    }
}
