package meld;

import arc.math.Mathf;
import arc.util.Log;
import meld.content.MeldContent;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidRouter;

public class AspectPipe extends Conduit {
    public AspectPipe(String name) {
        super(name);
    }

    public class AspectPipeBuild extends ConduitBuild{

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
                if(liquid == MeldContent.aether && this.canDumpLiquid(other, MeldContent.aspect)) {
                    Liquid original = liquid;
                    liquid = MeldContent.aspect;
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
                liquid = MeldContent.aspect;

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
