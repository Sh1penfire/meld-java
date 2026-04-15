package meld.world.blocks.fluid;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Position;
import arc.util.Log;
import meld.world.WorldUtil;
import mindustry.gen.Building;
import mindustry.type.Liquid;

public class ChannelDirector extends FlexibleSizeJunction{

    @Override
    public void load() {
        super.load();
    }
    public ChannelDirector(String name) {
        super(name);
        rotate = true;
        quickRotate = true;
        solid = false;
    }

    public class DirectorBuild extends FlexibleBuild{

        @Override
        public Building getLiquidDestination(Building source, Liquid liquid, Position otherOffset) {
            int dir = source.tile.relativeTo(tile);
            if(!acceptDirection(source, this, dir)) return this;

            return super.getLiquidDestination(source, liquid, otherOffset);
        }

        @Override
        public boolean acceptDirection(Building source, Building junction, int dir) {
            return dir != (rotation + 2) % 4;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y,  rotation * 90);
        }
    }
}
