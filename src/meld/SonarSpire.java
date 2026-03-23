package meld;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.util.Time;
import meld.content.MeldContent;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

public class SonarSpire extends Block {

    public float duration = 60;

    public TextureRegion chain;

    @Override
    public void load() {
        super.load();
        chain = Core.atlas.find(Meld.name + "-chain");

    }

    public SonarSpire(String name) {
        super(name);
        update = true;
        fogRadius = 180/ Vars.tilesize;
        liquidCapacity = 400;
        clipSize = fogRadius * tilesize;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, fogRadius * tilesize, Pal.accent);
    }

    public class SpireBuild extends Building{

        public float lastRadius = 0f;

        public void ping(){
            consume();
        }

        @Override
        public void updateTile() {
            if(Math.abs(fogRadius() - lastRadius) >= 0.5f){
                Vars.fogControl.forceUpdate(team, this);
                lastRadius = fogRadius();
            }

            Units.nearby(team, x, y, fogRadius() * tilesize, (other) -> {
                other.apply(MeldContent.rally, duration);
            });
        }

        @Override
        public float fogRadius() {
            return efficiency * fogRadius;
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, fogRadius() * tilesize, Pal.accent);
        }

        @Override
        public void draw() {

            super.draw();

            Draw.z(Layer.effect);
            Draw.color(Pal.accent);

            //I literally slapped this in
            //Draww.drawChain(chain, x, y, Core.input.mouseWorldX(), Core.input.mouseWorldY(), 0);

            Draw.z(Layer.buildBeam);
            Draw.color(Pal.accent);
            Lines.stroke(tilesize/2);
            Lines.circle(x, y, fogRadius() * tilesize);

        }
    }
}
