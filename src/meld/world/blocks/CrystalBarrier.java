package meld.world.blocks;

import arc.audio.Sound;
import arc.util.Log;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import meld.content.MeldFx;
import mindustry.content.Fx;
import mindustry.gen.Sounds;
import mindustry.world.blocks.defense.Wall;

/**Makes nearby buildings regenerate health at a rapid pace

kills nearby buildings on destruction and sets a shatter timer on nearby crystal barriers, making them die on a delay.

 **/
public class CrystalBarrier extends Wall {

    public float lingerTime = 15;
    public float repairSpeed;
    public Sound shatterSound = Sounds.none;

    public int repairTimer = timers++;

    public CrystalBarrier(String name) {
        super(name);
        repairSpeed = 10;
        update = true;
        chanceDeflect = 50;
    }

    public class CrystalBarrierBuild extends WallBuild{
        public float shatterTime = 0;
        public boolean shattering = false;

        @Override
        public void updateTile() {
            super.updateTile();
            if(shattering) {
                shatterTime += Time.delta;
                if(shatterTime >= lingerTime){
                    kill();
                }
            }

            if(timer.get(repairTimer, 5)) {
                proximity.each(b -> {
                    if (b.damaged()) {
                        b.heal(repairSpeed * Time.delta);
                        MeldFx.barrierShield.at(b.x, b.y, b.block.size);
                    }
                });
            }
        }

        @Override
        public void killed() {
            proximity.each(b -> {
                if(b instanceof CrystalBarrierBuild barrier){
                    shattering = true;
                    barrier.beginShattering();
                    return;
                }
                b.kill();
            });
            super.killed();
        }

        public void beginShattering(){
            if(shattering) return;
            shatterSound.at(x, y);
            shattering = true;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.bool(shattering);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            shattering = read.bool();
        }
    }
}
