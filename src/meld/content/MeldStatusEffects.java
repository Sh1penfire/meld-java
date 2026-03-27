package meld.content;

import arc.math.Interp;
import arc.math.Mathf;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class MeldStatusEffects {
    public static StatusEffect amplified, rally, anchored, aspectBurn, sentry, spurting, newborn, interference, drenched, stunned;


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

        anchored = new StatusEffect("anchored"){

            @Override
            public void update(Unit unit, StatusEntry entry) {

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

        sentry = new StatusEffect("sentry"){{
            damage = 0.1f;
            reloadMultiplier = 2;
            speedMultiplier = 0.3f;
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
                                Mathf.clamp(Math.min(entry.time, 30)/(30))
                        )
                );
            }{
            speedMultiplier = 0.01f;
            dragMultiplier = 0.3f;
            disarm = true;
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
    }
}
