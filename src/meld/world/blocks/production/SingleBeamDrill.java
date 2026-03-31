package meld.world.blocks.production;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.EnumSet;
import arc.struct.IntMap;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.BeamDrill;
import mindustry.world.meta.BlockFlag;

//HAS TO be an odd number in size.
public class SingleBeamDrill extends Block {

    public int range = 5, tier = 4;

    //I know that sounds like a lot of items at once but well... yeah it's a lot of items at once
    public int baseProductivity = 30;
    public float selfDamage = 400;
    public float targetDamage = 2400;
    public ObjectFloatMap<Item> itemMultipliers = new ObjectFloatMap<>();
    public float drillTime = 300;

    public SingleBeamDrill(String name) {
        super(name);
        hasItems = true;
        rotate = true;
        update = true;
        solid = true;
        drawArrow = false;
        regionRotated1 = 1;
        ignoreLineRotation = true;
        ambientSoundVolume = 0.05F;
        ambientSound = Sounds.loopMineBeam;
        envEnabled |= 2;
        flags = EnumSet.of(new BlockFlag[]{BlockFlag.drill});
    }

    public class SingleBeamBuild extends Building{
        public float time;

        @Override
        public void updateTile() {
            super.updateTile();
            time += efficiency;
            if(time >= drillTime){
                drill();
                time %= drillTime;
            }

            dump();
        }

        public void drill(){
            damage(selfDamage);

            Item found = null;

            int offset = (size + 1)/2;
            Point2 dir = Geometry.d4(rotation);

            int tx = tileX(), ty = tileY();
            for(int i = 0; i < range; i++){
                int j = i + offset;
                Tile other = Vars.world.tile(tx + dir.x * j, ty + dir.y * j);

                if(other.solid()){
                    Damage.dynamicExplosion(other.worldx(), other.worldy(), 5, 5, 30000, Vars.tilesize * 3, true);
                    Damage.dynamicExplosion(other.worldx(), other.worldy(), 5, 15, 0, Vars.tilesize * 2, true);
                    if(other.build != null) other.build.damage(targetDamage);

                    Item drop = other.wallDrop();

                    if(drop == null) break;

                    if(drop.hardness <= tier) found = drop;

                    break;
                }
                else {
                    Damage.dynamicExplosion(other.worldx(), other.worldy(), 5, 2, 0, Vars.tilesize * 3, true);
                }

            }

            if(found == null) return;

            int mined = (int)(baseProductivity * itemMultipliers.get(found, 1));

            mined = Mathf.ceilPositive(Math.min(itemCapacity - items.get(found), mined));
            items.add(found, mined);
        }

        @Override
        public float progress() {
            return time/drillTime;
        }
    }
}
