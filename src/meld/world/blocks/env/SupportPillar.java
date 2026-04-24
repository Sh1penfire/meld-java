package meld.world.blocks.env;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import meld.graphics.MeldLightRenderer;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.power.LightBlock;

public class SupportPillar extends Block {

    public SupportPillar(String name) {
        super(name);
        solid = true;
        hasShadow = false;
        update = true;
        drawTeamOverlay = false;
        clipSize = 5000;
        forceDark = true;
    }


    @Override
    public int minimapColor(Tile tile) {
        return mapColor.rgba();
    }

    @Override
    public boolean canBreak(Tile tile) {
        return Vars.state.isEditor();
    }

    public class SupportPillarBuild extends Building{
        public float radius = 0;
        public float outerShade = 0.1f;
        public int color, outerColor;

        @Override
        public void damage(float damage) {

        }

        @Override
        public void draw() {
            super.draw();
            MeldLightRenderer.thingo.shadow(() -> {
                Fill.light(x, y, 20, radius, Tmp.c1.set(color), Tmp.c2.set(outerColor));
            });
        }

        public void setColor(Color col){
            color = col.rgba();
        }

        public void setColorOuter(Color col){
            outerColor = col.rgba();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(radius);

            write.i(color);
            write.i(outerColor);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            radius = read.f();

            color = read.i();
            outerColor = read.i();
        }
    }
}
