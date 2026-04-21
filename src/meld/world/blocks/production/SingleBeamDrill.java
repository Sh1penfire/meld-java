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
import arc.util.io.Reads;
import arc.util.io.Writes;
import meld.content.MeldBullets;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.BeamDrill;
import mindustry.world.meta.BlockFlag;

//HAS TO be an odd number in size.
public class SingleBeamDrill extends Block {

    public int range = 10, tier = 4;

    public float minHealthf = 0.75f;
    //I know that sounds like a lot of items at once but well... yeah it's a lot of items at once
    public int baseProductivity = 50;

    //Additional items mined for each tile unused in the range
    public int distanceProductivity = 5;
    public float selfDamage;
    public float targetDamage = 2400;
    public ObjectFloatMap<Item> itemMultipliers = new ObjectFloatMap<>();
    public ObjectMap<Item, Item> transformItems = new ObjectMap<>();
    public float drillTime = 300;

    public BulletType bigExplosion;

    public Effect shootEffect = Fx.shootQuellPulse, trailEffect = Fx.explosion, hitEffect = Fx.massiveExplosion;

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
        bigExplosion = MeldBullets.pulsarBlast;
        selfDamage = 400;
    }

    public class SingleBeamBuild extends Building{
        public float time;

        @Override
        public boolean shouldConsume() {
            return super.shouldConsume() && healthf() >= minHealthf;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(healthf() >= minHealthf) time += edelta();
            if(time >= drillTime){
                drill();
                time %= drillTime;
            }

            dump();
        }

        public void drill(){
            damage(selfDamage);

            Item found = null;
            int unusedTiles = range + 1;

            int offset = (size + 1)/2;
            Point2 dir = Geometry.d4(rotation);

            int tx = tileX(), ty = tileY();
            Tile other = Vars.world.tile(tx + dir.x, ty + dir.y);
            Damage.dynamicExplosion(other.worldx(), other.worldy(), 5, 3, 90000, Vars.tilesize * 3, true, shootEffect);
            Damage.dynamicExplosion(other.worldx(), other.worldy(), 5, 5, 300, Vars.tilesize * 2, true, shootEffect);

            for(int i = 0; i < range; i++){
                unusedTiles--;
                int j = i + offset;
                other = Vars.world.tile(tx + dir.x * j, ty + dir.y * j);

                if(other.solid()){
                    if(bigExplosion == null){
                        Damage.dynamicExplosion(other.worldx(), other.worldy(), 5, 5, 30000, Vars.tilesize * 3, true, hitEffect);
                        Damage.dynamicExplosion(other.worldx(), other.worldy(), 5, 15, 0, Vars.tilesize * 2, true, hitEffect);
                    }
                    else bigExplosion.create(this, Team.derelict, other.worldx() - dir.x * Vars.tilesize, other.worldy() - dir.y * Vars.tilesize, 0);
                    if(other.build != null) other.build.damage(targetDamage);

                    Item drop = other.wallDrop();

                    if(drop == null) break;

                    if(drop.hardness <= tier) found = drop;

                    break;
                }
                else {
                    Damage.dynamicExplosion(other.worldx(), other.worldy(), 5, 2, 0, Vars.tilesize * 3, true, trailEffect);
                }

            }

            if(found == null) return;

            found = transformItems.get(found, found);

            int mined = (int)((baseProductivity + distanceProductivity * unusedTiles) * itemMultipliers.get(found, 1));

            mined = Mathf.ceilPositive(Math.min(itemCapacity - items.get(found), mined));
            items.add(found, mined);
        }

        @Override
        public float progress() {
            return time/drillTime;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(time);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            time = read.f();
        }
    }
}
