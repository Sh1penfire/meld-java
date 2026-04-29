package meld.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.g2d.*;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import meld.SettingKeys;
import meld.math.Math3D;
import meld.world.WeatherMath;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Fonts;
import mindustry.world.Block;

import static meld.ui.MeldSettings.connectedSonar;
import static mindustry.Vars.*;

public class Draww {

    public static Vec2 tv;
    /*This conssiders
    -X and Y of position
    -Wind
    -Parallax
     */

    //Weights for different materials
    public static float
            sparkWeight = 0.1f,
            smokeWeight = 0.005f,
            heavySmokeWeight = 0.015f;

    //Height gain/second for different materials
    public static float
            smokeDrift = 0.75f/60;

    public static void speckOffset(float x, float y, float height, float magnitude, float weight, Vec2 out){
        Vec2 w = new Vec2();
        windOffset(magnitude, weight, w);
        w.add(x,y);
        parallaxOffset(w.x, w.y, height, out);
    }

    public static void windOffset(float magnitude, float weight, Vec2 out){
        out.set(WeatherMath.wind);
        out.scl(Core.settings.getInt("frostscape-wind-visual-force"));
        float length = Math.max(out.len() - weight, 0) * magnitude;
        out.setLength(length);
    }

    public static void parallaxOffset(float x, float y, float height, Vec2 out){
        float px = Math3D.xCamOffset2D(x, height), py = Math3D.yCamOffset2D(y, height);
        out.set(x + px, y + py);
    }

    public static void drawSonar(float x, float y, float radius, float thickness){
        drawSonar(x, y, radius, thickness, MeldLayers.sonar, Pal.accent);
    }

    public static void drawSonar(float x, float y, float radius, float thickness, float layer, Color color){
        if(connectedSonar){
            float z = Draw.z();
            Draw.z(layer);

            Draw.color(color);
            Fill.light(x, y, (int) (radius/4) + 12, radius + thickness/2, Tmp.c1.set(color).a(0), Tmp.c1.a(1));

            Draw.z(layer + 0.01f);
            Fill.light(x, y, (int) (radius/4) + 12, radius - thickness/2, Tmp.c1.set(Color.red), Tmp.c1);
            Draw.z(z);
        }
        else {
            Draw.color(color);
            Draw.z(layer);
            Lines.stroke(thickness);
            Lines.circle(x, y, radius);
        }
    }

    //Draw place text from blocks but well... yeah
    //Uses the draw colour instead of an input one

    public static void itemText(String text, float x, float y, Item item){
        float offset = drawTextUnderlined(text, x, y, item.color)/2;
        Draw.mixcol(Color.darkGray, 1f);
        float s = iconSmall / 4f;
        Draw.rect(item.fullIcon, x + offset + s/2, y - 1, s, s);
        Draw.mixcol();
        Draw.rect(item.fullIcon, x + offset + s/2, y, s, s);
    }

    public static float drawTextUnderlined(String text, float x, float y, Color col){
        if(renderer.pixelate) return 0;

        Font font = Fonts.outline;
        GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        boolean ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        font.getData().setScale(1f / 4f / Scl.scl(1f));
        layout.setText(font, text);

        float width = layout.width;

        font.setColor(col);

        font.draw(text, x, y + layout.height + 1, Align.center);
        y -= 1f;
        Lines.stroke(2f, Color.darkGray);
        Lines.line(x - layout.width / 2f - 2f, y, x + layout.width / 2f + 1.5f, y);
        Lines.stroke(1f, col);
        Lines.line(x - layout.width / 2f - 2f, y, x + layout.width / 2f + 1.5f, y);

        font.setUseIntegerPositions(ints);
        font.setColor(Color.white);
        font.getData().setScale(1f);
        Draw.reset();
        Pools.free(layout);

        return width;
    }

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