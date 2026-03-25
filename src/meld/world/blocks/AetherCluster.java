package meld.world.blocks;

import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Time;
import meld.content.MeldContent;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.liquid.LiquidRouter;

public class AetherCluster extends LiquidBlock {

    public LiquidStack outputLiquid;
    public int liquidTimer = timers++;

    public AetherCluster(String name) {
        super(name);
        liquidCapacity = 300;
        outputLiquid = new LiquidStack(MeldContent.aether, 1);
    }
    public TextureRegion[] icons() {
        return new TextureRegion[]{region};
    }


    public class AetherClusterBuild extends Building{
        @Override
        public void updateTile() {
            super.updateTile();
            liquids.add(outputLiquid.liquid, outputLiquid.amount * Time.delta);
            if(timer.get(liquidTimer, 5)){
                updateProximity();
                dumpLiquid(outputLiquid.liquid);
            }
        }

        @Override
        public void drawTeam() {
            super.drawTeam();
        }

        //I REALLY hate doing this but like it works:tm:
        @Override
        public void dumpLiquid(Liquid liquid, float scaling, int outputDir) {
            int dump = cdump;
            if (!(liquids.get(liquid) <= 1.0E-4F)) {

                //Hardocde the liuiiujhgjghkn
                for(Point2 offset: Edges.getEdges(size)){
                    Building other = Vars.world.build(tile.x + offset.x, tile.y + offset.y);
                    if(other == null) continue;

                    other = other.getLiquidDestination(this, liquid);
                    if (other != null && other.block.hasLiquids && canDumpLiquid(other, liquid) && other.liquids != null) {
                        float ofract = other.liquids.get(liquid) / other.block.liquidCapacity;
                        float fract = liquids.get(liquid) / block.liquidCapacity;
                        if (ofract < fract) {
                            transferLiquid(other, (fract - ofract) * block.liquidCapacity / scaling, liquid);
                        }
                    }
                }
            }
        }

        //This is so dam hacky but like it works
        @Override
        public void display(Table table) {
            Team og = team;
            team = Vars.player.team();
            super.display(table);
            team = og;
        }
    }
}
