package meld.content;

import arc.math.Interp;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class MeldStatusEffects {
    public static StatusEffect interference, drenched, stuck, stunned;
    public static StatusEffect lacerated, impaled;

    //Player effects
    public static StatusEffect rally, anchored, aspectBurn, omnimoveCompensate;

    //Meld effects
    public static StatusEffect amplified, sentry, spurting, newborn, rush;

    //Swamp effects
    public static StatusEffect refreshed, slippery, infested, oozed;

    public static StatusEffect boosting, boostingIframes;


    public static void load(){

        stunned = new StatusEffect("stunned"){{
            speedMultiplier = 0.1f;
            reloadMultiplier = 0.5f;
        }};

        amplified = new StatusEffect("amplified"){
        @Override
        public void update(Unit unit, StatusEntry entry) {

            unit.speedMultiplier /= speedMultiplier;
            //Start the falloff at 60 secconds remaining, gets MUCH quicker with time
            unit.speedMultiplier *= Mathf.lerp(
                    1, speedMultiplier,

                    Interp.pow5.apply(
                            Mathf.clamp(Math.min(entry.time, 3600)/(3600))
                    )
            );
        }
        {
            healthMultiplier = 2;
            speedMultiplier = 1.5f;
            reloadMultiplier = 2;
        }};

        rally = new StatusEffect("rally"){
            @Override
            public void update(Unit unit, StatusEntry entry) {

                unit.speedMultiplier /= speedMultiplier;
                //Start the falloff at 60 ticks remaining
                unit.speedMultiplier *= Mathf.lerp(
                        1, speedMultiplier,

                        Interp.pow2.apply(
                                Mathf.clamp(Math.min(entry.time, 60)/(60))
                        )
                );
            }
            {
            speedMultiplier = 1.25f;
            reloadMultiplier = 2;
        }};

        rush = new StatusEffect("rush"){
            @Override
            public void update(Unit unit, StatusEntry entry) {

                unit.speedMultiplier /= speedMultiplier;
                //Start the falloff at 180 ticks remaining
                unit.speedMultiplier *= Mathf.lerp(
                        1, speedMultiplier,

                        Interp.pow3.apply(
                                Mathf.clamp(Math.min(entry.time, 180)/(180))
                        )
                );
            }
            {
                damage = -1;
                healthMultiplier = 2;
                speedMultiplier = 2;
                disarm = true;
            }};

        boosting = new StatusEffect("boosting"){

            @Override
            public void onRemoved(Unit unit) {
                super.onRemoved(unit);
                unit.apply(rush, 15);
            }

            @Override
            public void update(Unit unit, StatusEntry entry) {
                unit.vel.trns(unit.rotation, 7);
                Fx.explosion.at(unit.x, unit.y);
            }

            {
                disarm = true;
                speedMultiplier = 0;
                dragMultiplier = 0;
            }
        };

        boostingIframes = new StatusEffect("boosting-iframes");

        anchored = new StatusEffect("anchored"){

        @Override
        public void update(Unit unit, StatusEntry entry) {

            unit.unapply(spurting);
            unit.speedMultiplier /= speedMultiplier;
            //Start the falloff at 150 ticks remaining
            unit.speedMultiplier *= Mathf.lerp(
                    1, speedMultiplier,

                    Interp.pow2.apply(
                            Mathf.clamp(Math.min(entry.time, 150)/(150))
                    )
            );
        }
        {

            speedMultiplier = 0.3f;
            dragMultiplier = 3;
        }};

        sentry = new StatusEffect("sentry"){{
            damage = 0.2f;
            reloadMultiplier = 2;
            speedMultiplier = 0.15f;
        }};

        spurting = new StatusEffect("spurting"){
            @Override
            public void update(Unit unit, StatusEntry entry) {
                super.update(unit, entry);
                unit.speedMultiplier /= speedMultiplier;
                //Start the falloff at 150 ticks remaining
                unit.speedMultiplier *= Mathf.lerp(
                        1, speedMultiplier,

                        Interp.pow2.apply(
                                Mathf.clamp(Math.min(entry.time, 60)/(60))
                        )
                );
            }{

            damage = 0.2f;
            speedMultiplier = 0.05f;
            dragMultiplier = 0.3f;
        }};

        newborn = new StatusEffect("newborn"){
            @Override
            public void update(Unit unit, StatusEntry entry) {
                super.update(unit, entry);
                unit.speedMultiplier /= speedMultiplier;
                //Start the falloff at 150 ticks remaining
                unit.speedMultiplier *= Mathf.lerp(
                        1, speedMultiplier,

                        Interp.pow2.apply(
                                Mathf.clamp(Math.min(entry.time, 30)/(30))
                        )
                );
            }{

            damage = -2;
            healthMultiplier = 2;
            speedMultiplier = 0.5f;
        }};

        interference = new StatusEffect("interference"){
            @Override
            public void update(Unit unit, StatusEntry entry) {
                super.update(unit, entry);
                unit.reloadMultiplier /= reloadMultiplier;
                //Start the falloff at 60 ticks remaining
                unit.reloadMultiplier *= Mathf.lerp(
                        1, reloadMultiplier,

                        Interp.pow2.apply(
                                Mathf.clamp(Math.min(entry.time, 60)/(30))
                        )
                );
            }{

            reloadMultiplier = 0.5f;
        }};

        drenched = new StatusEffect("drenched"){
            @Override
            public void update(Unit unit, StatusEntry entry) {
                super.update(unit, entry);
                unit.speedMultiplier /= speedMultiplier;

                //Speed multiplier based on duration. 10 ticks -> 0.1 less multi
                unit.speedMultiplier *= Mathf.clamp(1 - entry.time/100, 0.1f, 1);
            }{

                speedMultiplier = 0.5f;

        }};

        stuck = new StatusEffect("stuck"){
            @Override
            public void update(Unit unit, StatusEntry entry) {
                super.update(unit, entry);
                unit.dragMultiplier /= dragMultiplier;

                //Speed multiplier based on duration. 10 ticks -> 0.1 less multi
                unit.dragMultiplier *= Mathf.clamp(3 - entry.time/300, 1f, 3);
            }{
                dragMultiplier = 3;
        }};

        //Only affects unarmored
        lacerated = new StatusEffect("lacerated"){{
            damage = -0.2f;
            speedMultiplier = 0.45f;
            reloadMultiplier = 0.5f;
            buildSpeedMultiplier = 0.5f;
        }};


        //Only affects armored
        aspectBurn = new StatusEffect("aspect-burn"){
            @Override
            public void update(Unit unit, StatusEntry entry) {
                super.update(unit, entry);
                if(unit.armor > 0) unit.damageContinuousPierce(unit.armor/60);
            }
            {
                damage = 0.1f;
                healthMultiplier = 0.5f;
            }
        };

        impaled = new StatusEffect("impaled"){{
            damage = 1f;
            speedMultiplier = 0.65f;
            buildSpeedMultiplier = 0.5f;
        }};

        //For bulbhead when omni movement is turned on
        omnimoveCompensate = new StatusEffect("omnimove-compensate"){{
            dragMultiplier = 0.5f;
        }};

        //Swamp effects

        refreshed = new StatusEffect("refreshed"){{
            damage = -1;
            reloadMultiplier = 2;
        }};

        slippery = new StatusEffect("slippery"){{
            speedMultiplier = 1.25f;
            dragMultiplier = 0.15f;
        }};

        infested = new StatusEffect("infested"){{
            damage = 0.5f;
            healthMultiplier = 0.5f;
            reloadMultiplier = 2;
        }};

        oozed = new StatusEffect("oozed"){{
            speedMultiplier = 0.75f;
            dragMultiplier = 3;
        }};
    }
}
