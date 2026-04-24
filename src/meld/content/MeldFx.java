package meld.content;

import arc.Core;
import arc.func.Floatp;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import meld.graphics.Draww;
import meld.Meld;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.Healthc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

import static mindustry.content.Fx.rand;

public class MeldFx {

    private static float percent = 0;

    public static Effect

        waterShear = new Effect(35, e -> {
            Draw.color(Color.white, e.color, e.finpow());

            e.scaled(15, e1 -> {

                Draw.z(Layer.groundUnit - 1);
                Draw.alpha(Mathf.clamp(e1.finpow() * 5));

                for(int i = 0; i < 2; i++){
                    Angles.randLenVectors(e1.id, 3, e1.fin() * Vars.tilesize * 8, e.rotation + 180 + 25 * Mathf.signs[i], 15, 4, (x, y) -> {
                        Fill.circle(e.x + x, e.y + y, 1.5f * e1.foutpowdown());
                    });
                }
            });

            Draw.z(Layer.groundUnit - 1);

            Draw.alpha(Mathf.clamp(e.finpow() * 5));
            for(int i = 0; i < 2; i++){
                Angles.randLenVectors(e.id, 3, e.fin() * Vars.tilesize * 6, e.rotation + 180 + 35 * Mathf.signs[i], 5, 4, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 3 * e.foutpowdown());
                });
            }
    }){{
        followParent = false;
    }},

    waterShearFollow = new Effect(35, e -> {
        Draw.color(Color.white, e.color, Color.clear, e.finpowdown());

        e.scaled(25, e1 -> {

            Draw.z(Layer.groundUnit - 1);
            Draw.alpha(Draw.getColorAlpha() * e1.finpow() * 5);

            for(int i = 0; i < 2; i++){
                Angles.randLenVectors(e1.id, 6, e1.fin() * Vars.tilesize * 3, e.rotation + 180 + 35 * Mathf.signs[i], 0, 4, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 1.5f * e1.foutpowdown());
                });
            }
        });

        Draw.z(Layer.groundUnit - 1);

        Draw.color(Color.white, e.color, e.finpowdown());
        Draw.alpha(e.finpow() * 5);

        for(int i = 0; i < 2; i++){
            Angles.randLenVectors(e.id, 3, e.fin() * Vars.tilesize * 3, e.rotation + 180 + 25 * Mathf.signs[i], 5, 4, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 3 * e.foutpowdown());
            });
        }

        e.scaled(15, e1 -> {;
            Draw.z(Layer.groundUnit + 1);
            Draw.alpha(Mathf.clamp(e1.finpow() * 5)/10);
            for(int i = 0; i < 2; i++){
                Angles.randLenVectors(e.id, 16, e1.fin() * Vars.tilesize * 3, e.rotation + 180 + 35 * Mathf.signs[i], 2, 3, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 1.5f * e1.foutpowdown());
                });
            }
        });
    }){{
        rotWithParent = true;
    }},

    chainLightning = new Effect(15, 500 * 500/2 * Vars.tilesize, e -> {
        if(!(e.data instanceof VisualLightningHolder)) return;
        VisualLightningHolder p = (VisualLightningHolder) e.data;

        Draw.blend(Blending.additive);

        int seed = e.id;
        //get the start and ends of the lightning, then the distance between them
        float tx = Tmp.v1.set(p.start()).x, ty = Tmp.v1.y, dst = Tmp.v1.dst(Tmp.v2.set(p.end()));

        //Get the direction towards the endpoint from the start
        Tmp.v3.set(p.end()).sub(p.start()).nor();
        float normx = Tmp.v3.x, normy = Tmp.v3.y;

        rand.setSeed(seed);

        //Set arc width before rand gets based on time
        float arcWidth = rand.range(dst * p.arc());

        seed = e.id - (int) (e.time * 2);

        float angle = Tmp.v1.angleTo(Tmp.v2);

        //How offset each point is from the line based on an arc
        Floatp arcX = () -> Mathf.sinDeg(percent * 180) * arcWidth;

        //range of lightning strike's vary depending on turret
        float range = p.segLength();
        int links = Mathf.ceil(dst / p.segLength());
        float spacing = dst / links;

        Lines.stroke(p.width() * e.fout());
        Draw.color(Color.white, e.color, e.finpow());
        Fill.circle(Tmp.v2.x, Tmp.v2.y, p.width() * e.fout()/2);

        //begin the line
        //Lines.beginLine();

        //Lines.linePoint(Tmp.v1.x, Tmp.v1.y);

        //Join the links together

        int coils = p.coils();
        for(int u = 0; u < coils; u++){
            int coil = u + 1;
            float coilSpacing = spacing/coil;

            //Make the lower numbered, less eratic coils travel quicker
            float travelPercent = Mathf.clamp(e.finpow() * (coils - coil + 1));


            int coilLinks = links * coil;

            float lastx = Tmp.v1.x, lasty = Tmp.v1.y;
            for(int i = 0; i < Mathf.ceil(coilLinks * travelPercent); i++){
                float nx, ny;
                //Only put an endpoint at the very end of the lightning, ending early shoudn't end it at the end point
                if(i == links * coil - 1){
                    //line at end
                    nx = Tmp.v2.x;
                    ny = Tmp.v2.y;
                }else{
                    float len = (i) * coilSpacing + rand.range(coilSpacing/2) + coilSpacing;
                    rand.setSeed(seed + i);

                    //Gets more random with each coil
                    Tmp.v3.trns(rand.random(360), range/2/(coils - coil + 1));
                    percent = ((float) (i + 1))/coilLinks;

                    nx = tx + normx * len + Tmp.v3.x + Tmp.v4.set(0, arcX.get()).rotate(angle).x;
                    ny = ty + normy * len + Tmp.v3.y + Tmp.v4.y;
                }

                Drawf.light(lastx, lasty, nx, ny, Lines.getStroke(), Draw.getColor(), Draw.getColor().a);

                //Using a quad instead of just a line so that the edges can join together
                Lines.line(lastx, lasty, nx, ny);

                lastx = nx;
                lasty = ny;
                //Lines.linePoint(nx, ny);
            }
        }


        //ines.endLine();
        Draw.blend();
    });

    public interface VisualLightningHolder{
        Position start();

        Position end();

        float width();

        float segLength();

        float arc();

        int coils();
    }

    public static float root2 = Mathf.sqrt(2);

    public static Effect

        gasTransfer = new Effect(60, e -> {
            Draw.z(Layer.blockUnder);
            Draw.color(e.color);
            Draw.alpha(e.finpowdown());
            Angles.randLenVectors(e.id, 2, e.fin() * Vars.tilesize * 1.5f, e.rotation,Vars.tilesize/4f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 2 * e.foutpowdown());
            });
        }),

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
        }),

        barrierShield = new Effect(15, e -> {
            Fill.light(e.x, e.y, 4, e.rotation * Vars.tilesize/root2, 45, Tmp.c2.set(e.color).a(0), Tmp.c1.set(e.color));
        });
}
