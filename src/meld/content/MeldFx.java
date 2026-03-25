package meld.content;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Tmp;
import meld.Draww;
import meld.Meld;
import mindustry.entities.Effect;
import mindustry.gen.Healthc;
import mindustry.graphics.Layer;

public class MeldFx {

    public static Effect

        chain = new Effect(240, e -> {
            Draw.color(Color.red);
            if(e.data instanceof Position pos) {
                if(e.data instanceof Healthc health && health.dead()) return;
                Draw.z(Layer.blockOver);
                Tmp.v1.set(e.x, e.y);
                float s = Mathf.clamp(e.fin() * 8, 0, 1);
                float a = Mathf.clamp(e.fout() * 3, 0, 1);

                Tmp.v2.set(Tmp.v1).lerp(pos.getX(), pos.getY(), s);

                Draw.alpha(a);
                Draww.drawChain(Core.atlas.find(Meld.prefix("chain")), Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, 0.5f, 1, 0);
            }
        }){{
            followParent = false;
        }},

        anchored = new Effect(30, e -> {
            Draw.color(Color.red);

            for(int i = 0; i < 3; i++){
                final int j = i;
                e.scaled(i * 10 + 5, e1 -> {
                    Draw.alpha(e1.foutpow());
                    Lines.stroke(1 + 2 * j * e1.finpow());
                    Lines.circle(e.x, e.y, 7 + 3 * j + 24 * Interp.pow5Out.apply(e1.fin()));
                });
            }

            Draw.alpha(Interp.pow5Out.apply(e.fout()));
            float s = Interp.pow5Out.apply(e.fin());
            Draw.rect(Core.atlas.find(Meld.prefix("anchor")), e.x, e.y, 12 + s * 14, 12 + s * 14);
        });
}
