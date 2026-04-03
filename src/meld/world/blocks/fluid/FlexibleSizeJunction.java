package meld.world.blocks.fluid;

import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Position;
import arc.util.Log;
import arc.util.Tmp;
import meld.world.WorldUtil;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidJunction;

public class FlexibleSizeJunction extends LiquidJunction {
    public boolean enabledToggles = true;

    public FlexibleSizeJunction(String name) {
        super(name);
    }

    public boolean usePassedOffset = true;

    public class FlexibleBuild extends LiquidJunctionBuild{

        @Override
        public Building getLiquidDestination(Building source, Liquid liquid){
            return getLiquidDestination(source, liquid, null);
        }

        //Subclasses can change this
        public float addedRotation(Building source, int dir){
            return 0;
        }

        public Building getLiquidDestination(Building source, Liquid liquid, Position otherOffset){
            if(!enabled && enabledToggles) return this;

            //Find which direction the offset should go
            int dir = WorldUtil.relativeTo(source.tile.x, source.tile.y, tile.x, tile.y);
            Point2 direction = Geometry.d4(dir);

            //Direction offset vector
            Tmp.v1.set(direction.x, direction.y).scl(size);

            //Tile offset vector, can grab from previous junction if it's already been calculated
            int tolerance = size/2;
            if(otherOffset != null){
                Tmp.v2.set(otherOffset).scl(size);
            }
            else {
                //Store the unclamped offset for later
                Tmp.v2.set(source.tile.x, source.tile.y).sub(tile.x, tile.y);
            }


            //Start with the direction offset vector
            Tmp.v3.set(Tmp.v1);

            //If usePassedOffset is true, grab the offset from the previous junction where possible, and clamp it
            if(usePassedOffset) Tmp.v3.add(Tmp.v4.set(Tmp.v2).clamp(-tolerance, -tolerance, tolerance, tolerance));

            //Rotate the offsets where aplicable
            float rotationOffset = addedRotation(source, dir);
            if(rotationOffset != 0){
                Tmp.v3.rotate(addedRotation(source, dir));
                Tmp.v2.rotate(addedRotation(source, dir));
            }

            //Scale the offsets, add them to the tile's world coords
            Tmp.v3.scl(Vars.tilesize);
            Tmp.v3.add(tile.worldx(), tile.worldy());


            Building next = Vars.world.buildWorld(Tmp.v3.x, Tmp.v3.y);

            //Prioritise sending down the corosponding output where possible
            if(next instanceof FlexibleBuild b){
                return b.getLiquidDestination(this, liquid, Tmp.v2.scl(1f/size));
            }

            //If next is null then attempt to pas fluid ont othe next valid junction
            if(next == null || (!next.acceptLiquid(this, liquid) && !(next.block instanceof LiquidJunction)) && next != this){

                //Try to find a flexible size junction in the direction we're routing to

                //Find the direction counter clockwise of the direction we're routing to
                Point2 sidewaysDir = Geometry.d4(dir + 1);
                int tolerancePlus = tolerance + 1;
                for(int i = 0; i < size; i++){
                    int j = i - tolerance;
                    //We want a tile outside of the block in the direction we're routing to, then all the tiles along that face
                    Building junction = Vars.world.build(tile.x + direction.x * tolerancePlus + sidewaysDir.x * j, tile.y + direction.y * tolerancePlus + sidewaysDir.y * j);

                    //If we find a junction, pass along the offset
                    if(junction instanceof FlexibleBuild b){
                        return b.getLiquidDestination(this, liquid, Tmp.v2.scl(1f/size));
                    }
                }

                return this;
            }


            //If all else fails, just do the default behaviour
            return next.getLiquidDestination(this, liquid);
        }
    }
}
