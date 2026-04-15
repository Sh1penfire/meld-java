package meld.world.blocks.fluid;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Log;
import meld.content.MeldLiquids;
import meld.world.blocks.crafting.ModularCrafter;
import meld.world.blocks.crafting.RecipeCrafter;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.DuctBridge;
import mindustry.world.blocks.liquid.ArmoredConduit;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidRouter;

import static meld.content.MeldLiquids.*;

public class AspectPipe extends Conduit {

    Seq<Liquid> liquidSeq = new Seq<>();
    static final float rotatePad = 6, hpad = rotatePad / 2f / 4f;
    static final float[][] rotateOffsets = {{hpad, hpad}, {-hpad, hpad}, {-hpad, -hpad}, {hpad, -hpad}};

    public AspectPipe(String name) {
        super(name);
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
        return super.blends(tile, rotation, otherx, othery, otherrot, otherblock) || (otherblock.hasLiquids && !(otherblock instanceof Conduit || otherblock instanceof LiquidRouter) && outletMapping.values().toSeq().find(l -> otherblock.consumesLiquid(l)) != null);
    }

    public class AspectPipeBuild extends ConduitBuild{

        public float lastMoved = 0;

        public void updateTile() {
            lastMoved = 0;
            this.smoothLiquid = Mathf.lerpDelta(this.smoothLiquid, this.liquids.currentAmount() / liquidCapacity, 0.05F);
            if (this.liquids.currentAmount() > 1.0E-4F && this.timer(timerFlow, 1.0F)) {
                lastMoved = moveLiquidForward(leaks, this.liquids.current());
                this.noSleep();

                //Attempt to push liquids to the sides
                for(int i = 0; i < 2; i++){
                    //get the tile above and below the current pipe, accounting for rotation
                    Tile t = Vars.world.tile(tile.x + Geometry.d4(1 + i * 2 + rotation).x, tile.y + Geometry.d4(1 + i * 2 + rotation).y);

                    if(t == null || t.build == null || !t.build.block.hasLiquids || t.build.liquids == null || (!(t.build.block instanceof RecipeCrafter || t.build.block instanceof ModularCrafter) && t.build.block.consumers.length == 0)) continue;
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

                incrementDump(this.proximity.size);
                other = other.getLiquidDestination(this, liquid);
                if (other == null || !other.block.hasLiquids || other.liquids == null) return 0;

                //Transfer regular liquid
                if(canDumpLiquid(other, liquid)) {
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

                Liquid outletProduct = outletMapping.get(liquid);
                //At the same time try dumping aspect
                if(outletProduct != null && !(other instanceof ConduitBuild || other instanceof LiquidRouter.LiquidRouterBuild) && this.canDumpLiquid(other, outletProduct)) {
                    Liquid original = liquid;
                    liquid = outletProduct;
                    float ofract = other.liquids.get(liquid) / other.block.liquidCapacity;
                    float fract = this.liquids.get(original) / this.block.liquidCapacity;

                    //Transfer liquid code
                    if (ofract < fract) {
                        float amount = (fract - ofract) * this.block.liquidCapacity;

                        float flow = Math.min(other.block.liquidCapacity - other.liquids.get(liquid), amount) * 10;
                        if (other.acceptLiquid(this, liquid)) {
                            other.handleLiquid(this, liquid, flow);
                            this.liquids.remove(original, flow/aetherDensities.get(liquid, 1)/10);
                            total += amount;
                        }

                    }
                }
            }
            return total;
        }


        //TODO: Figure out why this even has to be changed to get aspect pipes routing backwards when vanilla conduits don't
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            noSleep();
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2f)
                    && (tile == null || source == this || (source.relativeTo(tile.x, tile.y) + 2) % 4 != rotation || source instanceof AspectPipeBuild);
        }
    }
}
