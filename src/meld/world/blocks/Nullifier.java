package meld.world.blocks;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.tilesize;

//Basically a glorified turret
public class Nullifier extends Block {


    public float range,
            reload,
    //Non instakill damage, only falls off with distance
            damage,
    //Targets for the instakill damage
            targets;

    public Nullifier(String name) {
        super(name);
        update = true;
        reload = 7 * 60;
        range = 15 * tilesize;
        damage = 6000;
        targets = 3;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.accent);
    }

    public class NullifierBuild extends Building {
        Seq<Building> targetList = new Seq();
        public float reloadTime;

        @Override
        public void updateTile() {
            super.updateTile();
            reloadTime += Time.delta;
            if(reloadTime >= reload){
                Units.nearbyBuildings(x, y, range, b -> {
                    targetList.add(b);
                });
                targetList.sort(b -> b.block.flags.contains(BlockFlag.core) ? 100 : 0 + dst(b)/range);
                for(int i = 0; i < targets; i++){
                    if(targetList.isEmpty()) continue;
                    targetList.pop().kill();
                }
                targetList.each(b -> b.damage(damage * (1 - dst(b)/range)));
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
