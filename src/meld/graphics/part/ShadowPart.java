package meld.graphics.part;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.util.Tmp;
import meld.graphics.MeldLightRenderer;
import mindustry.entities.part.DrawPart;

public class ShadowPart extends DrawPart {
    @Override
    public void draw(PartParams params) {
        float x = params.x, y = params.y;

        MeldLightRenderer.thingo.shadow(() -> {
            Fill.light(x, y, 100, 100, Color.white, Tmp.c1.set(Color.white).a(0));
        });
    }
}
