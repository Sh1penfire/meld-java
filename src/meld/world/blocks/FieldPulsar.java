package meld.world.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import meld.Meld;
import meld.content.MeldStatusEffects;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

/**
Basic functionality for pulsing blocks.
Reloads its uptime based on efficiency. When duration runs out && uptime is equal to pulseDuration
 -set uptime to zero
 -call PulsarBuild#pulse
 -set duration to pulseDuration

 PulseBuild#pulse is smooth interped towards its target based on warmupSpeed, used by other blocks
**/
public class FieldPulsar extends Block {

    //Warmup speed is smooth while shrink is fixed.
    public float warmupSpeed, shrinkSpeed, range;

    public float pulseDuration, graceDuration;

    public TextureRegion chain;
    
    public Color ringColor = Pal.accent;
    @Override
    public void load() {
        super.load();
        chain = Core.atlas.find(Meld.name + "-chain");

    }

    public FieldPulsar(String name) {
        super(name);
        update = true;
        solid = true;

        warmupSpeed = 1/60f;

        pulseDuration = 240;

        graceDuration = 30;
        range = 180;
        fogRadius = (int)(range/tilesize);
        liquidCapacity = 400;
        clipSize = range;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, ringColor);
    }

    public class PulsarBuild extends Building{

        public float smoothRadius;
        public float duration, uptime, grace;

        @Override
        public void update() {
            super.update();

            boolean active = active();
            //change this later to interp at different rates
            if(active()) {
                smoothRadius = Mathf.lerpDelta(smoothRadius, 1 * range, warmupSpeed);
            }
            else smoothRadius = Mathf.approachDelta(smoothRadius, 0, shrinkSpeed);
        }

        @Override
        public void updateTile() {
            //Charge up a pulse in the same time that it lasts
            uptime += Time.delta * efficiency;
            duration = Math.max(duration - Time.delta, 0);
            grace = Math.max(grace - Time.delta, 0);

            if(duration <= 0 && uptime >= pulseDuration){
                duration = pulseDuration;
                grace = graceDuration;
                uptime = 0;
                pulse();
            }
        }

        //Left empty for other blocks
        public void pulse(){

        }

        public boolean active(){
            return (grace + duration) > 0;
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, smoothRadius, ringColor);
        }

        @Override
        public void draw() {

            super.draw();

            Draw.z(Layer.effect);
            Draw.color(ringColor);

            //I literally slapped this in
            //Draww.drawChain(chain, x, y, Core.input.mouseWorldX(), Core.input.mouseWorldY(), 0);

            Draw.z(Layer.buildBeam);
            Draw.color(ringColor);
            Lines.stroke(tilesize/2);
            Lines.circle(x, y, smoothRadius);

            Lines.arc(x, y, range, uptime/pulseDuration);
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(smoothRadius);
            write.f(uptime);
            write.f(duration);
            write.f(grace);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            smoothRadius = read.f();
            uptime = read.f();
            duration = read.f();
            grace = read.f();
        }
    }
}
