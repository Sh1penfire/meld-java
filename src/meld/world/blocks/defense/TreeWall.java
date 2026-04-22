package meld.world.blocks.defense;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import meld.content.MeldStatusEffects;
import mindustry.gen.Unit;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.environment.StaticWall;

import static mindustry.Vars.tilesize;

public class TreeWall extends Wall {

    public TreeWall(String name) {
        super(name);
        drawTeamOverlay = false;
    }

    public class TreeBuild extends WallBuild{

        @Override
        public void unitOn(Unit unit) {
            super.unitOn(unit);
            unit.apply(MeldStatusEffects.stuck, 30);
        }

        public void draw(){
            TextureRegion reg = variants > 0 ? variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))] : region;

            TextureRegion r = Tmp.tr1;
            r.set(reg);
            int crop = (r.width - tilesize*4) / 2;
            float ox = 0;
            float oy = 0;

            for(int i = 0; i < 4; i++){
                if(tile.nearby(i) != null && tile.nearby(i).block().solid){

                    if(i == 0){
                        r.setWidth(r.width - crop);
                        ox -= crop /2f;
                    }else if(i == 1){
                        r.setY(r.getY() + crop);
                        oy -= crop /2f;
                    }else if(i == 2){
                        r.setX(r.getX() + crop);
                        ox += crop /2f;
                    }else{
                        r.setHeight(r.height - crop);
                        oy += crop /2f;
                    }
                }
            }
            Draw.rect(r, tile.drawx() + ox * Draw.scl, tile.drawy() + oy * Draw.scl);
        }
    }
}
