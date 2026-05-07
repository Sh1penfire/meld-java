package meld.world.blocks.fluid;

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
import arc.math.geom.Point2;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import meld.content.MeldFx;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.draw.DrawLiquidTile;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

//PLEASEWORKPLEAEWORKAPSLEAOSKOPK
public class VisualAspectPipe extends AspectPipe {
    protected static final Rand rand = new Rand();

    public int sides = 12;
    public float px = 0, py = 0;
    public float alpha = 0.1f;
    public int particles = 12;
    public float particleRotation = 0, particleLife = 70f, particleRad = 4.2f, particleSize = 3f, fadeMargin = 0.4f, rotateScl = 3f;

    //Life that additional particles get drawn at
    public float flowingLife = 15;

    //Alpha which flowing particles get
    public float flowAlpha = 0.1f;

    //Distance which additional particles travel
    public float particleTravel = 16;
    public float cone = 45;

    //How long a pipe can go without transporting fluid while still maintaining the flow visual
    public float flowGrace = 5;

    public boolean reverse = false, poly = false;
    public Interp particleInterp = new Interp.PowIn(1.5f);
    public Interp particleSizeInterp = Interp.slope;
    public Blending blendMode = Blending.additive;

    public float effectChance = 0.1f;
    public float visualFlowSpeed = 10;

    public boolean debugDraw = false;

    public VisualAspectPipe(String name) {
        super(name);
    }

    public class VisualPipeBuild extends AspectPipeBuild{

        public float warmup = 0;
        public float flowWarmup = 0;
        public float noflowTime;
        public Building front;
        public float effectTimer = Mathf.random();

        @Override
        public void updateTile() {
            super.updateTile();

            warmup = Mathf.lerpDelta(warmup, liquids.currentAmount()/liquidCapacity, 0.05f);

            //Keep the flow visual high as possible for as long as flowGrace in points of no flow
            noflowTime += Time.delta;
            if(lastMoved > 0) noflowTime = 0;

            float flowWarmupTarget = Mathf.clamp(lastMoved/liquidCapacity * visualFlowSpeed);

            if(flowWarmupTarget > flowWarmup || noflowTime > flowGrace) flowWarmup = Mathf.lerpDelta(flowWarmup, flowWarmupTarget, 0.05f);
            else flowWarmup = Mathf.lerpDelta(flowWarmup, flowWarmupTarget, 0.01f);

            effectTimer += effectChance * lastMoved/liquidCapacity;

            if(Mathf.chance(effectTimer)){
                effectTimer = 0 ;

                //Stupid
                //Rotation 0 faces right so randomise particle spawn top and bottom
                Tmp.v1.set(-0.5f, Mathf.random(-0.5f, 0.5f)).rotate(rotation * 90).scl(tilesize/4f);

                MeldFx.gasTransfer.at(x  + Tmp.v1.x, y + Tmp.v1.y, rotation * 90, liquids.current().color);
            }
        }

        @Override
        public float warmup() {
            return Math.max(Interp.pow5Out.apply(warmup), flowWarmup);
        }

        @Override
        public void draw(){
            if(debugDraw){

                Draw.z(Layer.blockUnder);

                drawInbetween();
                Draw.reset();
                return;
            }

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

            Draw.z(Layer.blockUnder);

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
                float a2 = flowAlpha * Interp.pow5Out.apply(flowWarmup);

                Draw.blend(blendMode);
                Draw.color(current.color);

                float base = Time.time / particleLife;
                float flowBase = Time.time/flowingLife;

                rand.setSeed(id);

                for(int i = 0; i < particles; i++){
                    float finFlow = (rand.random(2f) + flowBase) % 1f;

                    float fin = (rand.random(2f) + base) % 1f;
                    if(reverse) fin = 1f - fin;
                    float fout = 1f - fin;

                    float straightness = fin * flowWarmup;

                    float particleAngle = rand.random(360f) + (Time.time / rotateScl) % 360f;
                    float len = particleRad * particleInterp.apply(fout);
                    //1. Calculate offsets for regular draw particles

                    Tmp.v1.set(len, 0).rotate(Mathf.clamp(particleAngle % 360, cone, 360-cone));

                    //2. calculate the stream offsets for steam particles
                    Tmp.v2.set(Tmp.v1);
                    Tmp.v2.y = -particleTravel + particleTravel * 2 * fin;

                    //Lerp between the two, then rotatie properly
                    Tmp.v3.set(Tmp.v1).lerp(Tmp.v2, straightness);
                    Tmp.v3.rotate((rotation - 1) * 90);


                    //3. Rotate them for block


                    Draw.alpha(a * (1f - Mathf.curve(fin, 1f - fadeMargin)));
                    if(poly){
                        Fill.poly(
                                x + Tmp.v3.x,
                                y + Tmp.v3.y,
                                sides,
                                particleSize * particleSizeInterp.apply(fin) * warmup(),
                                particleRotation
                        );
                    }else{
                        Fill.circle(
                                x + Tmp.v3.x,
                                y + Tmp.v3.y,
                                particleSize * particleSizeInterp.apply(fin) * warmup()
                        );
                    }
                    //Here we go on this trip again
                    Draw.alpha(a2 * (1 - Mathf.curve(finFlow, 1 - fadeMargin)));
                    if(poly){
                        Fill.poly(
                                x + Tmp.v3.x,
                                y + Tmp.v3.y,
                                sides,
                                particleSize * particleSizeInterp.apply(fin) * warmup(),
                                particleRotation
                        );
                    }else{
                        Fill.circle(
                                x + Tmp.v3.x,
                                y + Tmp.v3.y,
                                particleSize * particleSizeInterp.apply(fin) * warmup()
                        );
                    }
                }
            }
            Draw.blend();
            Draw.reset();
        }

        @Override
        public void drawLiquidLight(Liquid liquid, float amount) {
            if (amount > 0.01F) {
                Color color = liquid.lightColor;
                float fract = 1.0f;
                float opacity = color.a * fract;
                if (opacity > 0.001F) {
                    Drawf.light(this.x, this.y, (float)this.block.size * 30.0F * fract, color, opacity * amount);
                }
            }
        }
    }
}
