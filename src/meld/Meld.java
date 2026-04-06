package meld;

import arc.*;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import meld.content.*;
import meld.graphics.MeldRegions;
import meld.meta.MeldStatUnit;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.core.GameState;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.input.DesktopInput;
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

    public static float timeSpeed = 1, timeSlowed = 0.1f;

    public static float slowProg = 0, slowFactor;

    public static Unit prev;

    public Meld(){
        Events.on(EventType.ClientLoadEvent.class, e -> {
            MeldRegions.load();
            Time.setDeltaProvider(() -> Math.min(Core.graphics.getDeltaTime() * 60 * slowFactor, 3 * slowFactor));
        });

        Events.on(EventType.UnitControlEvent.class, u -> {
            Groups.unit.remove(u.unit);
            if(prev != null) Groups.unit.add(prev);
            prev = u.unit;
        });

        Events.run(Trigger.update, () -> {

            if(Vars.state.isPaused()) Vars.state.set(GameState.State.playing);
            slowProg = Mathf.lerp(slowProg, Core.input.keyDown(KeyCode.space) ? 1 : 0, 0.1f);
            slowFactor = Mathf.lerp(timeSpeed, timeSlowed, slowProg);

            float original = Time.delta;

            Time.delta = Core.graphics.getDeltaTime();
            if(prev != null){
                if(!prev.isAdded() || prev.dead) prev = null;
                prev.update();
            }


            //Update everything another time to make up for the decrease in delta time
            float remainder = Core.graphics.getDeltaTime() * (slowProg);
            //Log.info(remainder);
            Time.delta = remainder;

            //"What if I simply... updated Vars.input"


            Vars.control.input.update();
            if(Vars.player.unit() != null) Vars.player.unit().update();

            /*
            DesktopInput input = (DesktopInput)Vars.control.input;
            input.panScale = 0.005f/slowFactor;
            input.panSpeed = 4.5f/slowFactor;
            input.panBoostSpeed = 15f/slowFactor;
             */

            /*
            float dest = Mathf.clamp(Mathf.round(Vars.renderer.targetscale, 0.5f), Vars.renderer.minScale(), Vars.renderer.maxScale());
            Vars.renderer.camerascale = Mathf.lerp(Vars.renderer.camerascale, dest, 0.1f * slowProg);

             */
            Time.delta = original;
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
        MeldBullets.load();
        MeldUnits.load();
        MeldItems.load();
        MeldLiquids.load();
        MeldBlocks.load();
        MeldEnvironment.load();

        Vars.content.items().each(c -> {
                c.stats.add(Stat.buildCost, c.cost, MeldStatUnit.ticks);
        });
        Vars.content.blocks().each(b -> {
            if(b.minfo.mod != null && b.minfo.mod.name.equals("meld")){
                b.deconstructDropAllLiquid = true;
            }
        });


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
