package meld.world.blocks;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

//PLEASEWORKPLEAEWORKAPSLEAOSKOPK
public class VisualAspectPipe extends AspectPipe{
    protected static final Rand rand = new Rand();

    public int sides = 12;
    public float px = 0, py = 0;
    public float alpha = 0.1f;
    public int particles = 12;
    public float particleRotation = 0, particleLife = 70f, particleRad = 4.2f, particleSize = 3f, fadeMargin = 0.4f, rotateScl = 3f;
    public boolean reverse = false, poly = false;
    public Interp particleInterp = new Interp.PowIn(1.5f);
    public Interp particleSizeInterp = Interp.slope;
    public Blending blendMode = Blending.additive;

    public VisualAspectPipe(String name) {
        super(name);
    }

    public class VisualPipeBuild extends AspectPipeBuild{

        float warmup = 0;

        @Override
        public void updateTile() {
            super.updateTile();
            warmup = Mathf.lerpDelta(warmup, liquids.currentAmount()/liquidCapacity, 0.1f);
        }

        @Override
        public float warmup() {
            return Interp.pow5Out.apply(warmup);
        }

        @Override
        public void draw(){
            int r = this.rotation;

            //draw extra conduits facing this one for tiling purposes
            //WHY WHY WHY WHY WHYYYYYYYYYYYYYYYYYYYYYYYYYYYY
            Draw.z(Layer.blockUnder);
            for(int i = 0; i < 4; i++){
                if((blending & (1 << i)) != 0){
                    int dir = r - i;
                    drawAt(x + Geometry.d4x(dir) * tilesize*0.75f, y + Geometry.d4y(dir) * tilesize*0.75f, 0, i == 0 ? r : dir, i != 0 ? SliceMode.bottom : SliceMode.top);
                }
            }

            Draw.z(Layer.block -0.001f);

            drawInbetween();
            Draw.z(Layer.block);

            Draw.scl(xscl, yscl);
            drawAt(x, y, blendbits, r, SliceMode.none);
            Draw.reset();

            if(capped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg());
            if(backCapped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg() + 180);
        }

        @Override
        protected void drawAt(float x, float y, int bits, int rotation, SliceMode slice){
            float angle = rotation * 90f;
            Draw.color(botColor);

            Draw.z(Layer.blockUnder - 0.1f);
            Draw.rect(sliced(botRegions[bits], slice), x, y, angle);

            int offset = yscl == -1 ? 3 : 0;

            int frame = liquids.current().getAnimationFrame();
            int gas = liquids.current().gas ? 1 : 0;
            float ox = 0f, oy = 0f;
            int wrapRot = (rotation + offset) % 4;
            TextureRegion liquidr = bits == 1 && padCorners ? rotateRegions[wrapRot][gas][frame] : renderer.fluidFrames[gas][frame];

            if(bits == 1 && padCorners){
                ox = rotateOffsets[wrapRot][0];
                oy = rotateOffsets[wrapRot][1];
            }

            Draw.z(Layer.blockUnder);

            //the drawing state machine sure was a great design choice with no downsides or hidden behavior!!!
            float xscl = Draw.xscl, yscl = Draw.yscl;
            Draw.scl(1f, 1f);
            Drawf.liquid(sliced(liquidr, slice), x + ox, y + oy, smoothLiquid, liquids.current().color.write(Tmp.c1).a(1f));
            Draw.scl(xscl, yscl);

            Draw.z(Layer.block - 0.01f);
            Draw.rect(sliced(topRegions[bits], slice), x, y, angle);
        }

        public void drawInbetween(){
            Draw.z(Layer.blockUnder + 0.01f);

            Liquid current = liquids.current();
            if(warmup() > 0f && current != null){
                float a = alpha * warmup();

                Draw.blend(blendMode);
                Draw.color(current.color);

                float base = Time.time / particleLife;
                rand.setSeed(id);

                for(int i = 0; i < particles; i++){
                    float fin = (rand.random(2f) + base) % 1f;
                    if(reverse) fin = 1f - fin;
                    float fout = 1f - fin;
                    float angle = rand.random(360f) + (Time.time / rotateScl) % 360f;
                    float len = particleRad * particleInterp.apply(fout);

                    Draw.alpha(a * (1f - Mathf.curve(fin, 1f - fadeMargin)));
                    if(poly){
                        Fill.poly(
                                px + x + Angles.trnsx(angle, len),
                                py + y + Angles.trnsy(angle, len),
                                sides,
                                particleSize * particleSizeInterp.apply(fin) * warmup(),
                                particleRotation
                        );
                    }else{
                        Fill.circle(
                                px + x + Angles.trnsx(angle, len),
                                py + y + Angles.trnsy(angle, len),
                                particleSize * particleSizeInterp.apply(fin) * warmup()
                        );
                    }
                }

                Draw.blend();
                Draw.reset();
            }
        }
    }
}
