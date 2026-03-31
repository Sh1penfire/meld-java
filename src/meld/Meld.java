package meld;

import arc.*;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import meld.content.*;
import meld.core.*;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.EventType.*;
import mindustry.gen.Call;
import mindustry.mod.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import rhino.ImporterTopLevel;
import rhino.NativeJavaPackage;

public class Meld extends Mod{
    public static final String name = "meld";

    public static NativeJavaPackage p = null;

    public static Melting melting;
    
    public Meld(){
    }

    public static String prefix(String in){
        return name + "-" + in;
    }

    @Override
    public void init() {
        super.init();
        
        melting = new Melting();
        
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
        MeldStatusEffects.load();
        MeldUnits.load();
        MeldItems.load();
        MeldContent.load();
        MeldEnvironment.load();
    }
}
