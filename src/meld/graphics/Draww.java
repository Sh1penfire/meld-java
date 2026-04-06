package meld.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.graphics.Pal;
import mindustry.world.Block;

public class Draww {
    public static Vec2 tv1 = new Vec2(), tv2 = new Vec2(), tv3 = new Vec2();
    //draws a chain of sprites
    public static void drawChain(TextureRegion region, float x, float y, float endx, float endy, float drawRotation){
        drawChain(region, x, y, endx, endy, 1, 1, drawRotation);
    }
    public static void drawChain(TextureRegion region, float x, float y, float endx, float endy, float wScale, float hScale, float drawRotation){
        float angleToEnd = Mathf.angle(endx - x, endy - y);
        float distance = Mathf.dst(x, y, endx, endy);

        //This is in WU
        float height = region.height/4f * hScale;
        float width = region.width/4f * wScale;

        //Distance converted back to WU
        float remainder = distance % height;

        for (int i = 0; i < Math.floor((distance)/(height)); i++) {
            tv1.trns(angleToEnd, distance - i * height - height/2).add(x ,y);
            Draw.rect(region, tv1.x, tv1.y, width, height, drawRotation + angleToEnd - 90);
        }

        tv1.trns(angleToEnd, remainder/2);

        Draw.rect(region, x + tv1.x, y + tv1.y, width, remainder, drawRotation + angleToEnd - 90);
    }

    public interface AlphaProv{
        float get(float in);
    }

    public static void drawChainAlpha(TextureRegion region, float x, float y, float endx, float endy, float wScale, float hScale, float drawRotation, Interp alphaProv){
        float angleToEnd = Mathf.angle(endx - x, endy - y);
        float distance = Mathf.dst(x, y, endx, endy);

        //This is in WU
        float height = region.height/4f * hScale;
        float width = region.width/4f * wScale;

        //Distance converted back to WU
        float remainder = distance % height;



        Tmp.c1.set(Draw.getColor());
        Tmp.c2.set(Draw.getColor());

        tv3.trns(angleToEnd + 90, width/2);

        for (int i = 0; i < Math.floor((distance)/(height)); i++) {
            tv1.trns(angleToEnd, distance - i * height - height);
            tv2.trns(angleToEnd, distance - i * height);

            float baseAlpha = alphaProv.apply(tv1.len());
            float endAlpha = alphaProv.apply(tv2.len());

            tv1.add(x, y);
            tv2.add(x, y);

            Tmp.c1.a(baseAlpha);
            Tmp.c2.a(endAlpha);
            Draw.quad(region,
                tv2.x + tv3.x, tv2.y + tv3.y, Tmp.c2.toFloatBits(),
                tv1.x + tv3.x, tv1.y + tv3.y, Tmp.c1.toFloatBits(),
                tv1.x - tv3.x, tv1.y - tv3.y, Tmp.c1.toFloatBits(),
                tv2.x - tv3.x, tv2.y - tv3.y, Tmp.c2.toFloatBits()
            );

            //Draw.rect(region, tv1.x, tv1.y, width, height, drawRotation + angleToEnd - 90);
            //Draw.quad(region, tv1.x, tv1.y, width, height, drawRotation + angleToEnd - 90);
        }

        tv1.trns(angleToEnd, remainder/2f);

        Draw.alpha(alphaProv.apply(1 - tv1.len()/distance));
        Draw.rect(region, x + tv1.x, y + tv1.y, width, remainder, drawRotation + angleToEnd - 90);
        Draw.alpha(1);

        Draw.color();
        Lines.stroke(Vars.tilesize);
    }

    public static void drawShine(TextureRegion region, float x, float y, float rotation, float alpha){
        if(!Vars.state.rules.lighting){
            Draw.alpha(alpha);
            Draw.rect(region, x, y, rotation);
            return;
        }
        Draw.alpha((1 - Vars.state.rules.ambientLight.a) * alpha);
        Draw.rect(region, x, y, rotation);
        Draw.alpha(1);
    }

    public static void spinSprite(TextureRegion region, float x, float y, float r, float w, float h) {
        float a = Draw.getColor().a;
        r = Mathf.mod(r, 90.0F);
        Draw.rect(region, x, y, w, h, r);
        Draw.alpha(r / 90.0F * a);
        Draw.rect(region, x, y, w, h, r - 90.0F);
        Draw.alpha(a);
    }
}