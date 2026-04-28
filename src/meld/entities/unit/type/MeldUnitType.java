package meld.entities.unit.type;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import meld.graphics.MeldPal;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class MeldUnitType extends UnitType {


    public MeldUnitType(String name) {
        super(name);
        outlineColor = Color.clear;
        healColor = MeldPal.flamePinkDark;
    }

    //Ported from Frostscape
    public static class ActivationEngine extends UnitEngine {
        public float from, to, threshold, target;

        public ActivationEngine(float x, float y, float radius, float rotation, float from, float to, float threshold, float target){
            super(x, y, radius, rotation);
            this.from = from;
            this.to = to;
            this.threshold = threshold;
            this.target = target;
        }

        public float activation(Unit unit){
            float dif = 1;

            float turnRot = Mathf.mod(unit.rotation * 2 - unit.vel.angle(), 360);

            float engineRot = Mathf.mod(unit.rotation + rotation - 90 + 180, 360);

            float alignment = 1 - Mathf.mod(Math.min(Angles.forwardDistance(turnRot, engineRot), Angles.backwardDistance(turnRot, engineRot)), 360)/360;


            float activation = Mathf.lerp(from, to, Mathf.clamp(Mathf.maxZero(unit.vel().len2() - threshold) / target, 0, 1)) * dif * alignment;

            return activation;
        }

        public void draw(Unit unit) {
            //take the smaller difference of the two
            //float dif = 1 - Math.min(Angles.forwardDistance(unit.vel.angle(), unit.rotation), Angles.backwardDistance(unit.vel.angle(), unit.rotation))/360;


            float iradius = radius;

            radius *= activation(unit);

            UnitType type = unit.type;
            float scale = type.useEngineElevation ? unit.elevation : 1f;

            if(scale <= 0.0001f) return;

            float rot = unit.rotation - 90;
            Color color = type.engineColor == null ? unit.team.color : type.engineColor;

            Tmp.v1.set(x, y).rotate(rot);
            float ex = Tmp.v1.x, ey = Tmp.v1.y;

            Draw.color(color);
            Fill.circle(
                    unit.x + ex,
                    unit.y + ey,
                    (radius + Mathf.absin(Time.time, 2f, radius / 4f)) * scale
            );
            Draw.color(type.engineColorInner);
            Fill.circle(
                    unit.x + ex - Angles.trnsx(rot + rotation, 1f),
                    unit.y + ey - Angles.trnsy(rot + rotation, 1f),
                    (radius + Mathf.absin(Time.time, 2f, radius / 4f)) / 2f  * scale
            );

            radius = iradius;
        }

    }
        }
