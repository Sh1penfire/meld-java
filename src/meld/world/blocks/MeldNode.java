package meld.world.blocks;

import mindustry.content.Blocks;
import mindustry.world.blocks.storage.CoreBlock;

//Like a core but spawns melting overlays near it on destruction
public class MeldNode extends CoreBlock {
    public MeldNode(String name) {
        super(name);
    }

    public class MeldNodeBuild extends CoreBuild{
        @Override
        public void killed() {
            super.killed();
            //Spawn melting overlays on all tiles touching the edges of this block
            eachEdge(t -> {
                t.setOverlay(Blocks.pebbles);
            });
        }
    }
}
