package meld;

import arc.*;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import meld.content.*;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import rhino.ImporterTopLevel;
import rhino.NativeJavaPackage;

public class Meld extends Mod{


    public static final String name = "meld";

    public static NativeJavaPackage p = null;

    public Meld(){
    }

    public static String prefix(String in){
        return name + "-" + in;
    }

    @Override
    public void init() {
        super.init();
        Vars.mods.getScripts().runConsole(
                "function buildWorldP(){return Vars.world.buildWorld(Vars.player.x, Vars.player.y)}");
        ImporterTopLevel scope = (ImporterTopLevel) Vars.mods.getScripts().scope;

        Seq<String> packages = Seq.with(
                "meld",
                "meld.content",
                "meld.world"
        );

        packages.each(name -> {

            p = new NativeJavaPackage(name, Vars.mods.mainLoader());

            p.setParentScope(scope);

            scope.importPackage(p);
        });
    }

    public static float delay, delayTime;

    @Override
    public void loadContent(){
        MeldStatusEffects.load();
        MeldUnits.load();
        MeldItems.load();
        MeldLiquids.load();
        MeldBlocks.load();
        MeldEnvironment.load();


        delayTime = 5;
        Events.run(Trigger.update, () -> {
            if (!Vars.state.isGame() || Vars.state.isPaused() || Vars.state.isEditor()) return;
            if(delay < delayTime){
                delay += Time.delta;
                return;
            }
            delay %= delayTime;
            step();
        });
    }


    public static final IntSeq activeBuffer = IntSeq.with();
    public static int[] toMelt;

    //NOT optimized, it just works
    public static void step(){

        Floor activeOverlay = (Floor) Blocks.pebbles, stable = MeldEnvironment.meldCrystal, unstable = MeldEnvironment.meldCrystalScattered, melted = MeldEnvironment.meldSwampland;

        int width = Vars.world.width();
        int height = Vars.world.height();

        int s = Vars.world.width() * Vars.world.height();



        activeBuffer.clear();

        Vars.world.tiles.each((x, y) -> {

            Tile current = Vars.world.tile(x, y);

            if(current == null) return;

            //if(current.floor() == stable || current.floor() == unstable) Fx.fire.at(current.worldx(), current.worldy());

            if(current.floor() == melted || current.overlay() != activeOverlay) return;

            for(Point2 o: Geometry.d4){
                Tile t = Vars.world.tile(current.x + o.x, current.y + o.y);
                if(t == null) continue;

                if(current.overlay() == activeOverlay && (t.floor() == unstable || (current.floor() == stable && t.floor() == stable))){
                    activeBuffer.add(t.pos());
                }
            };

            current.setOverlay(Blocks.air);
            current.setFloor(melted);
            Fx.smoke.at(x * Vars.tilesize, y * Vars.tilesize);
            if(current.build != null) current.build.kill();
        });

        activeBuffer.each(i -> {
            Vars.world.tile(i).setOverlay(activeOverlay);
        });
    }

}
