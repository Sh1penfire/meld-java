package meld;

import arc.*;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import meld.content.MeldContent;
import meld.content.MeldEnvironment;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.world.Block;
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
                "meld.content"
        );

        packages.each(name -> {

            p = new NativeJavaPackage(name, Vars.mods.mainLoader());

            p.setParentScope(scope);

            scope.importPackage(p);
        });
    }

    @Override
    public void loadContent(){
        MeldContent.load();
        MeldEnvironment.load();


        Events.run(Trigger.update, () -> {
            if (!Vars.state.isGame() || Vars.state.isPaused()) return;
            step();
        });
    }


    public static final IntSeq activeBuffer = IntSeq.with();

    //NOT optimized, it just works
    public static void step(){

        Block activeOverlay = Blocks.oreCopper, stable = MeldEnvironment.meldCrystal, unstable = MeldEnvironment.meldCrystalScattered, melted = MeldEnvironment.meldSwampland;

        int width = Vars.world.width();
        int height = Vars.world.height();

        int s = Vars.world.width() * Vars.world.height();


        for(int i = 0; i < s; i++){

            int j = i + width + 2;

            int x = j % width;
            int y = Mathf.floor(j/(float)height) % height;

            Tile current = Vars.world.tile(x, y);

            if(current == null) continue;

            //if(current.floor() == stable || current.floor() == unstable) Fx.fire.at(current.worldx(), current.worldy());

            if(current.floor() == melted || current.overlay() != activeOverlay) continue;

            for(Point2 o: Geometry.d4){
                Tile t = Vars.world.tile(current.x + o.x, current.y + o.y);
                if(t == null) continue;

                if(current.overlay() == activeOverlay && (t.floor() == unstable || current.floor() == t.floor())){
                    activeBuffer.add(t.pos());
                }
            };

            current.setFloor((Floor) melted);
            current.setOverlay(Blocks.air);
        };

        activeBuffer.each(t -> {
            Vars.world.tile(t).setOverlay(activeOverlay);
        });
        activeBuffer.clear();
    }

}
