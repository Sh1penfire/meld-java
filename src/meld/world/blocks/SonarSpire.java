package meld.world.blocks;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;

import static mindustry.Vars.tilesize;

//A field pulsar with fow scouting
public class SonarSpire extends FieldPulsar {

    public StatusEffect status;
    public float statusDuration;

    public SonarSpire(String name) {
        super(name);
        status = StatusEffects.none;
        statusDuration = 60;
    }

    public class SonarSpireBuild extends PulsarBuild {
        public float lastRadius = 0f;

        @Override
        public void updateTile() {
            super.updateTile();

            if(duration >= 0){
                Units.nearby(team, x, y, smoothRadius, (other) -> {
                    other.apply(status, statusDuration);
                });
            }

            if (Math.abs(fogRadius() - lastRadius) >= 0.5f) {
                Vars.fogControl.forceUpdate(team, this);
                lastRadius = fogRadius();
            }
        }
        @Override
        public float fogRadius() {
            return smoothRadius/tilesize;
        }

        @Override
        public void draw() {
            super.draw();
            float charge = duration/pulseDuration;
            float chargeInv = 1 - charge;

            Draw.z(Layer.floor + 0.1f);
            Lines.stroke(2 + charge * 30);
            Draw.alpha(Interp.pow10In.apply(charge));

            Lines.circle(x, y, Interp.pow10Out.apply(chargeInv) * smoothRadius);

            Draw.alpha(Interp.pow2In.apply(charge));
            Lines.stroke(1 + charge * 3);
            Lines.circle(x, y, chargeInv * smoothRadius);
        }
    }
}
