package meld.graphics.part;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.HoverPart;

public class AdjustableHoverPart extends HoverPart {

    public PartProgress growProgress = PartProgress.warmup;
    public float radiusTo = 8;


    public void draw(DrawPart.PartParams params) {
        float z = Draw.z();
        if (layer > 0.0F) {
            Draw.z(layer);
        }

        if (under && turretShading) {
            Draw.z(z - 1.0E-4F);
        }

        Draw.z(Draw.z() + layerOffset);
        int len = mirror && params.sideOverride == -1 ? 2 : 1;
        Draw.color(color);

        for(int c = 0; c < circles; ++c) {
            float fin = (Time.time / phase + (float)c / (float)circles) % 1.0F;
            Lines.stroke((1.0F - fin) * stroke + minStroke);

            for(int s = 0; s < len; ++s) {
                int i = params.sideOverride == -1 ? s : params.sideOverride;
                float sign = ((i == 0 ? 1 : -1) * params.sideMultiplier);
                Tmp.v1.set(x * sign, y).rotate(params.rotation - 90.0F);
                float rx = params.x + Tmp.v1.x;
                float ry = params.y + Tmp.v1.y;
                Lines.poly(rx, ry, sides, Mathf.lerp(radius, radiusTo, growProgress.get(params)) * fin, params.rotation);
            }
        }

        Draw.reset();
        Draw.z(z);
    }
}
