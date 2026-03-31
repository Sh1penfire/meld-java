package meld.world.blocks;

import arc.func.*;
import meld.*;
import meld.core.*;
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
            /// start melting before killing
            /// otherwise {@link mindustry.world.Tile#getLinkedTiles(Cons)} won't work
            Meld.melting.start(tileOn());
            
            super.killed();
        }
    }
}
