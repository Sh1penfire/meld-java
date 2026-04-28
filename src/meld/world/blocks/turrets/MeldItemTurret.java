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

    @Override
    public void drawOverlay(float x, float y, int rotation) {
        super.drawOverlay(x, y, rotation);

        Draw.draw(Layer.fogOfWar + 2, () -> {
            Draw.z(Layer.fogOfWar + 2);
            Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);

            if(fogRadiusMultiplier < 0.99f && state.rules.fog){
                Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * fogRadiusMultiplier, Pal.lightishGray);
            }

            if(drawMinRange){
                Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, minRange, Pal.placing);
            }
        });
    }
}
