package meld.world.blocks;

import arc.func.*;
import arc.util.Log;
import meld.*;
import meld.core.*;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.world.blocks.storage.CoreBlock;

//Like a core but spawns melting overlays near it on destruction
public class MeldNode extends CoreBlock {
    public MeldNode(String name) {
        super(name);
    }

    public class MeldNodeBuild extends CoreBuild{

        @Override
        public void killed() {
            Meld.melting.start(tile);
            super.killed();
        }
    }
}
