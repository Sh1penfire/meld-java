package meld.world.blocks;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import meld.content.MeldContent;
import meld.content.MeldLiquids;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidRouter;

public class AspectPipe extends Conduit {
    public AspectPipe(String name) {
        super(name);
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
        return super.blends(tile, rotation, otherx, othery, otherrot, otherblock) || (otherblock.hasLiquids && !(otherblock instanceof Conduit || otherblock instanceof LiquidRouter));
    }

    public class AspectPipeBuild extends ConduitBuild{

        public void updateTile() {
            this.smoothLiquid = Mathf.lerpDelta(this.smoothLiquid, this.liquids.currentAmount() / liquidCapacity, 0.05F);
            if (this.liquids.currentAmount() > 1.0E-4F && this.timer(timerFlow, 1.0F)) {
                this.moveLiquidForward(leaks, this.liquids.current());
                this.noSleep();

                //Attempt to push liquids to the sides
                for(int i = 0; i < 2; i++){
                    //get the tile above and below the current pipe, accounting for rotation
                    Tile t = Vars.world.tile(tile.x + Geometry.d4(1 + i * 2 + rotation).x, tile.y + Geometry.d4(1 + i * 2 + rotation).y);

                    if(t == null || t.build == null || !t.build.block.hasLiquids || t.build.liquids == null || t.build.block.consumers.length == 0) continue;
                    moveLiquid(t.build, liquids.current());
                }

            } else {
                this.sleep();
            }

        }

        //oh my god please just AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA.
        @Override
        public float moveLiquid(Building other, Liquid liquid) {
            float total = 0;
            if(!Mathf.zero(liquids.get(liquid))){

                this.incrementDump(this.proximity.size);
                other = other.getLiquidDestination(this, liquid);

                if (other == null || !other.block.hasLiquids || other.liquids == null) return 0;

                //Transfer regular liquid
                if(this.canDumpLiquid(other, liquid)) {
                    float ofract = other.liquids.get(liquid) / other.block.liquidCapacity;
                    float fract = this.liquids.get(liquid) / this.block.liquidCapacity;
                    if (ofract < fract) {
                        float amount = (fract - ofract) * this.block.liquidCapacity;


                        float flow = Math.min(other.block.liquidCapacity - other.liquids.get(liquid), amount);
                        if (other.acceptLiquid(this, liquid)) {
                            other.handleLiquid(this, liquid, flow);
                            this.liquids.remove(liquid, flow);
                            total += amount;
                        }
                    }
                }

                //At the same time try dumping aspect
                if(liquid == MeldLiquids.aether && this.canDumpLiquid(other, MeldLiquids.aspect)) {
                    Liquid original = liquid;
                    liquid = MeldLiquids.aspect;
                    float ofract = other.liquids.get(liquid) / other.block.liquidCapacity;
                    float fract = this.liquids.get(original) / this.block.liquidCapacity;

                    //Transfer liquid code
                    if (ofract < fract) {
                        float amount = (fract - ofract) * this.block.liquidCapacity;

                        float flow = Math.min(other.block.liquidCapacity - other.liquids.get(liquid), amount) * 10;
                        if (other.acceptLiquid(this, liquid)) {
                            other.handleLiquid(this, liquid, flow);
                            this.liquids.remove(original, flow/10);
                            total += amount;
                        }

                    }
                }
            }
            return total;
        }

        @Override
        public void transferLiquid(Building next, float amount, Liquid liquid) {
            transferLiquid(next, amount, liquid, false);
        }

        //Translating original liquid to the pipe's target liquid;
        public void transferLiquid(Building next, float amount, Liquid liquid, boolean translating){

            if(translating){
                Liquid original = liquid;
                liquid = MeldLiquids.aspect;

                float flow = Math.min(next.block.liquidCapacity - next.liquids.get(liquid), amount);
                if (next.acceptLiquid(this, liquid)) {
                    next.handleLiquid(this, liquid, flow);
                    this.liquids.remove(original, flow/10);
                }

                return;
            }

            float flow = Math.min(next.block.liquidCapacity - next.liquids.get(liquid), amount);
            if (next.acceptLiquid(this, liquid)) {
                next.handleLiquid(this, liquid, flow);
                this.liquids.remove(liquid, flow);
            }
        }
    }
}
