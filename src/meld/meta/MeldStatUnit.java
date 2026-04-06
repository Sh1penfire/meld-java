package meld.meta;

import mindustry.gen.Iconc;
import mindustry.world.meta.StatUnit;

public class MeldStatUnit {
    public static final StatUnit
            ticks = new StatUnit("ticks"),
            liquidUnitsThousand = new StatUnit("liquidUnits", "k [sky]" + Iconc.liquid + "[]")
        ;
}
