package meld.world.blocks.turrets;

import arc.graphics.g2d.Draw;
import arc.util.Log;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.ItemTurret;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class MeldItemTurret extends ItemTurret {

    public float layer = 0;

    public MeldItemTurret(String name) {
        super(name);
    }
}
