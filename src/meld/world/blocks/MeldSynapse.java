package meld.world.blocks;

import mindustry.world.blocks.storage.CoreBlock;

public class MeldSynapse extends CoreBlock {
    public MeldSynapse(String name) {
        super(name);
    }

    public class SynapseBuild extends CoreBuild{
        @Override
        public void killed() {
            proximity.each(b -> {
                if(b instanceof CrystalBarrier.CrystalBarrierBuild c){
                    c.beginShattering();
                }
            });
            super.killed();
        }
    }
}
