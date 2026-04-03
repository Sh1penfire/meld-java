package meld.world.blocks.fluid;

import mindustry.gen.Building;

public class Pipebox extends ChannelValve{
    public Pipebox(String name) {
        super(name);
        enabledToggles = false;
    }

    public int[] dirMappings = new int[]{
        90, -90, 90, -90
    };

    public class PipeboxBuild extends ValveBuild{

        @Override
        public float addedRotation(Building source, int dir) {
            return dirMappings[dir] * (enabled ? 1 : -1);
        }
    }
}
