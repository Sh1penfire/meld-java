package meld.world.blocks.fluid;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.world.blocks.power.BeamNode;

//When detecting nearby fluids, turn nearby valves off. Can be inverted.
public class ValveController extends FlexibleSizeJunction{

    public int updateTimer = timers++;
    public float minPressure = 0.1f;

    public Sound clickSound = Sounds.click;

    public TextureRegion[] signRegions = new TextureRegion[2];

    @Override
    public void load() {
        super.load();
        signRegions[0] = Core.atlas.find(name + "-on", "clear");
        signRegions[1] = Core.atlas.find(name + "-off", "clear");
    }

    public ValveController(String name) {
        super(name);
        configurable = true;
        update = true;
        drawDisabled = false;
        autoResetEnabled = false;
        configureSound = Sounds.none;

        rotate = true;
        quickRotate = true;

        config(Boolean.class, (ValveControllerBuild entity, Boolean b) -> entity.invert = b);
    }

    float total;

    public class ValveControllerBuild extends FlexibleBuild{

        public boolean invert = false;

        @Override
        public void updateTile() {
            super.updateTile();
            enablingLogic();
        }

        public void enablingLogic(){

            total = 0;
            proximity.each(b -> {
                if(b.liquids != null) total += b.liquids.currentAmount()/b.block.liquidCapacity;
            });

            boolean enable = total >= minPressure;

            if(invert) enable = !enable;

            Building b = tile.nearbyBuild(rotation);
            if(b == null) return;

            if(b.block.configurable) b.configure(enable);
            else b.enabled = enable;
        }

        @Override
        public boolean configTapped(){
            configure(!invert);
            clickSound.at(this);
            return false;
        }

        @Override
        public Boolean config(){
            return enabled;
        }

        @Override
        public void draw(){
            super.draw();

            Draw.rect(signRegions[invert ? 1 : 0], x, y, rotation * 90);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.bool(invert);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            invert = read.bool();
        }
    }
}
