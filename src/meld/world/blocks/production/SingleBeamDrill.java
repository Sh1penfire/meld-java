package meld.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.EnumSet;
import arc.struct.IntMap;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import meld.content.MeldBullets;
import meld.graphics.Draww;
import meld.graphics.TextModifiers;
import meld.graphics.TileDrawers;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.BeamDrill;
import mindustry.world.blocks.production.Drill;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.BlockFlag;

import static meld.graphics.TileDrawers.tileRad;
import static mindustry.Vars.iconSmall;

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

    @Override
    public void drawOverlay(float x, float y, int rotation) {
        float multiplier = 1;

        Tile start = Vars.world.tileWorld(x, y);
        if(start != null){
            if(Vars.control.input.block == null && start.build instanceof SingleBeamBuild build && start== build.tile && start.block() == this){
                multiplier = build.boostAmount;
            }
        }

        Item found = null;
        int unusedTiles = range + 1;

        int offset = (size + 1)/2;
        Point2 dir = Geometry.d4(rotation);

        int tx = (int) x/Vars.tilesize, ty = (int) y/Vars.tilesize;
        Tile other = null;

        float drawy = y + Vars.tilesize * size/2f;

        for(int i = 0; i < range; i++) {
            unusedTiles--;
            int j = i + offset;
            other = Vars.world.tile(tx + dir.x * j, ty + dir.y * j);
            if(!Vars.fogControl.isDiscovered(Vars.player.team(), other.x, other.y)){
                TileDrawers.drawFog(other.worldx(), other.worldy(), tileRad);
                Draww.drawTextUnderlined(TextModifiers.glitchyEntry("overlay.in-fog", 3, 0.15f, 2), x, drawy, Tmp.c1.set(Pal.lightishGray).lerp(Pal.gray, Mathf.absin(Time.globalTime/10f, 1, 1)));

                Drawf.dashLine(Pal.accent, x, y, x + dir.x * (range + offset) * Vars.tilesize, y + dir.y * (range + offset) * Vars.tilesize);
                return;
            }

            if(other.solid()){
                found = other.block().itemDrop;
                int mined = (int)((baseProductivity + distanceProductivity * unusedTiles) * itemMultipliers.get(found, 1));

                if(found != null){
                    Item current = transformItems.get(found, found);
                    Draww.itemText(mined/drillTime * 60 * multiplier+ " " + current.localizedName + "/s", x, drawy, current);
                    if(found != current){
                        drawy = y - Vars.tilesize * size/2f;
                        float s = iconSmall / 4f;
                        float textWidth = Draww.drawTextUnderlined("->", x, drawy, Pal.accent);
                        Draw.mixcol(Color.darkGray, 1f);
                        Draw.rect(found.fullIcon, x - textWidth, drawy - 1, s, s);
                        Draw.rect(current.fullIcon, x + textWidth, drawy - 1, s, s);
                        Draw.mixcol();
                        Draw.rect(found.fullIcon, x - textWidth, drawy, s, s);
                        Draw.rect(current.fullIcon, x + textWidth, drawy, s, s);
                    }

                    float drawx = x - (size * Vars.tilesize)/2f - 2;
                    drawy = y;

                    if(multiplier > 1){
                        Draww.drawTextUnderlined(multiplier + "x ", drawx, drawy, Pal.accent);
                    }
                }
                else if(other.build != null && other.build.team == Vars.player.team()){
                    Draww.drawTextUnderlined(Core.bundle.get("overlay.friendly-fire"), x, drawy, Pal.remove);
                }
                else {
                    Draww.drawTextUnderlined(Core.bundle.get("overlay.drop-missing"), x, drawy, Pal.accent);
                }

                Lines.stroke(3);
                Draw.color(Pal.gray);
                Lines.square(other.worldx(), other.worldy(), Vars.tilesize/2f + 2 + 1);
                Lines.stroke(1);
                Draw.color(Pal.accent);
                Lines.square(other.worldx(), other.worldy(), Vars.tilesize/2f + 2);

                Drawf.dashLine(Pal.accent, x, y, other.worldx(), other.worldy());
                return;
            }
            TileDrawers.drawInvalid(other.worldx(), other.worldy(), tileRad);
        }
        //We havn't found anything to mine
        Draww.drawTextUnderlined(Core.bundle.get("overlay.ore-missing"), x, drawy, Pal.lightishGray);
        Drawf.dashLine(Pal.accent, x, y, x + dir.x * (range + offset) * Vars.tilesize, y + dir.y * (range + offset) * Vars.tilesize);
    }

    public class SingleBeamBuild extends Building{
        public float time;
        public float boostAmount = 1;

        @Override
        public boolean shouldConsume() {
            return super.shouldConsume() && healthf() >= minHealthf;
        }

        @Override
        public float edelta() {
            return super.edelta() * boostAmount;
        }

        @Override
        public void updateTile() {
            super.updateTile();

            boostAmount = 1;
            for (Consume consumer : consumers) {
                if(consumer.efficiency(this) > 0) boostAmount *= consumer.efficiencyMultiplier(this);
            }

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
                    if(other.build != null) other.build.damage(targetDamage);
                    //BOOMY! :D
                    float boomx = other.worldx(), boomy = other.worldy();
                    Time.run(5, () -> {
                        if(bigExplosion == null){
                            Damage.dynamicExplosion(boomx, boomy, 5, 5, 30000, Vars.tilesize * 3, true, hitEffect);
                            Damage.dynamicExplosion(boomx, boomy, 5, 15, 0, Vars.tilesize * 2, true, hitEffect);
                        }
                        else bigExplosion.create(this, Team.derelict, boomx - dir.x * Vars.tilesize, boomy - dir.y * Vars.tilesize, 0);

                    });

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
