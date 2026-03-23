package meld;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.struct.Seq;
import arc.util.Time;
import meld.content.MeldContent;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

public class MovementAnchor extends Block {

    public float range, reload, statusDuration;
    public StatusEffect status;
    public int targets;

    //Priority given to units who already have the status effect
    public float stackPriority = -1000;

    public MovementAnchor(String name) {
        super(name);
        update = true;
        reload = 90;
        status = MeldContent.anchored;
        statusDuration = 240;
        targets = 2;
    }


    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.accent);
    }

    public class MovementAnchorBuild extends Building {

        public float reloadTime;
        private final Seq<Unit> targetList = new Seq<Unit>();

        @Override
        public void updateTile() {
            super.updateTile();
            reloadTime += Time.delta;
            if(reloadTime >= reload){
                targetList.clear();
                Units.nearbyEnemies(team, x, y, range, u -> {
                    targetList.add(u);
                });
                //Target fast units, then nearby units, and avoid targeting units already slowed
                targetList.sort(u -> 1 - dst(u)/range + u.type.speed + (u.hasEffect(status) ? u.getDuration(status)/statusDuration * stackPriority : 0));
                for(int i = 0; i < targets; i++){
                    if(targetList.isEmpty()) continue;
                    Unit u = targetList.pop();
                    u.apply(status, statusDuration);
                    MeldFx.chain.at(x, y, 0, u);
                    MeldFx.anchored.at(u.x, u.y, 0, u);
                }
                reloadTime %= reload;
            }
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, Pal.accent);
        }

        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.effect);
            Lines.arc(x, y, range, reloadTime/reload);
        }
    }
}
