package meld;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.Vars;

public class Draww {
    public static Vec2 tv1 = new Vec2();
    //draws a chain of sprites
    public static void drawChain(TextureRegion region, float x, float y, float endx, float endy, float drawRotation){
        float angleToEnd = Mathf.angle(endx - x, endy - y);
        float distance = Mathf.dst(x, y, endx, endy);

        //Distance converted back to WU
        float remainder = ((distance * 4) % region.height)/8;

        for (int i = 0; i < Math.floor((distance)/region.height * 4); i++) {
            tv1.trns(angleToEnd, distance - i * region.height/4 - region.height/8).add(x ,y);
            Draw.rect(region, tv1.x, tv1.y, drawRotation + angleToEnd - 90);
        }

        tv1.trns(drawRotation + angleToEnd, remainder);

        //Lines.line(x, y, x + tv1.x, y + tv1.y);
        Draw.rect(region, x + tv1.x, y + tv1.y, region.width/4, remainder * 2, drawRotation + angleToEnd - 90);
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