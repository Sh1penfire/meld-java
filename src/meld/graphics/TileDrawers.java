package meld.graphics;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.graphics.Pal;

//Class which exclusively draws 1x1 squares. yes.
public class TileDrawers {

    public static float tileRad = Vars.tilesize/2f;

    public static void drawFog(float x, float y, float radius){
        float offset =  x/4f + y;
        offset/=Vars.tilesize;
        Draw.color(Pal.gray, Pal.darkerGray, 0.5f + 0.5f * Mathf.sin(Time.globalTime/4f + offset, 1, 1));
        Draw.alpha(1);
        Fill.square(x, y, radius);
        Draw.reset();
    }

    public static void drawInvalid(float x, float y, float radius){
        Draw.color(Pal.health);
        Draw.alpha(0.5f);
        Fill.square(x, y, radius);
        Draw.reset();
    }
}
