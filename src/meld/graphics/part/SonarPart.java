package meld.graphics.part;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import meld.graphics.Draww;
import meld.graphics.MeldLayers;
import meld.graphics.MeldPal;
import meld.graphics.MeldRegions;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

public class SonarPart extends DrawPart {
    public float x = 0, y = 0, layer = MeldLayers.sonar;
    public Color color = Pal.accent;

    public float radius,
            stroke = 2;

    public float radiusTo = -1, strokeTo = -1;

    public boolean setDefaults = true;

    public PartProgress progress = PartProgress.life;

    @Override
    public void load(String name) {
        super.load(name);
        if(setDefaults){
            if(radiusTo == -1) radiusTo = radius;
            if(strokeTo == -1) strokeTo = stroke;
        }
    }

    @Override
    public void draw(PartParams params) {
        Tmp.v1.set(x, y).rotate(params.rotation).add(params.x, params.y);
        Draww.drawSonar(Tmp.v1.x, Tmp.v1.y, Mathf.lerp(radius, radiusTo, progress.get(params)), Mathf.lerp(stroke, strokeTo, progress.get(params)), layer, color);

        Vec2 pos = new Vec2(Tmp.v1);
        Vars.renderer.lights.add(() -> {
            Fill.light(pos.x, pos.y, 100, radius, Tmp.c1.set(Pal.accent).a(0.1f), MeldPal.accentClear);
        });
    }
}
