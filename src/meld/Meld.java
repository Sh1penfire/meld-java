package meld;

import arc.*;
import arc.graphics.Color;
import arc.graphics.Colors;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import meld.content.*;
import meld.core.*;
import meld.entities.unit.abilities.BezerkAbility;
import meld.fluid.AspectGroup;
import meld.graphics.MeldRegions;
import meld.meta.MeldStatUnit;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import rhino.ImporterTopLevel;
import rhino.NativeJavaPackage;


public class Meld extends Mod{
    public static final String name = "meld";

    public static NativeJavaPackage p = null;

    public static Melting melting;

    public Meld(){
        Events.on(EventType.ClientLoadEvent.class, e -> {
            MeldRegions.load();
        });
    }

    public static String prefix(String in){
        return name + "-" + in;
    }
    private static final StringBuilder b = new StringBuilder();

    public static String gradient(String in, Color... colors){
        if(colors.length == 1) return "[#" + colors[0].toString().substring(0, 6) + "]" + in + "[]";

        b.setLength(0);
        b.trimToSize();
        int length = in.length();
        int spaces = 0;

        for(int i = 0; i < length; i++){
            char ind = in.charAt(i);
            if(Character.isWhitespace(ind)){
                spaces++;
                b.append(' ');
                continue;
            }
            b.append("[#").append(Tmp.c1.set(colors[0]).lerp(colors, (float) i / (length - spaces)).toString(), 0, 6).append("]").append(ind).append("[]");
        }

        return b.toString();
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
                "meld.content",
                "meld.world",
                "meld.fluid"
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
        MeldBullets.load();
        MeldUnits.load();
        MeldItems.load();
        MeldLiquids.load();
        MeldBlocks.load();
        MeldEnvironment.load();

        //Just loads localised names
        AspectGroup.loadAll();

        Vars.content.items().each(c -> {
                c.stats.add(Stat.buildCost, c.cost, MeldStatUnit.ticks);
        });
        
        Vars.content.blocks().each(b -> {
            if(b.minfo.mod != null && b.minfo.mod.name.equals("meld")){
                b.deconstructDropAllLiquid = true;
            }
        });

        Vars.content.liquids().each(l -> {
            Colors.put(l.name, l.color);
        });
    }
}
