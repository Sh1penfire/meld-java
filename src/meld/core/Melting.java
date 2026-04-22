package meld.core;

import java.util.*;
import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import meld.content.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

// TODO: fix melting stopping when leaving world
// TODO: make a way to add more melting patterns
public class Melting implements Runnable{
    private static final Floor stable = MeldEnvironment.meldCrystalFloor;
    private static final Floor unstable = MeldEnvironment.meldCrystalScattered;
    private static final Floor melted = MeldEnvironment.meldSwampland;
    
    private boolean running = false;
    private final ObjectSet<Tile> toMelt = new ObjectSet<>();
    private final ObjectSet<Tile> toSpread = new ObjectSet<>();
    
    public Melting(){
        Events.on(EventType.ResetEvent.class, _it -> running = false);
    }
    
    public void start(Tile start){
        Log.info("Starting!");
        start.getLinkedTiles(toSpread::add);
        
        if(!running){
            running = true;
            run();
        }
    }
    
    @Override
    public void run(){
        toMelt
            // TODO: this check might not be necessary
            .select(it -> it.floor() != melted)
            .each(it -> {
                it.setFloor(melted);
                Fx.smoke.at(it);
                
                var build = it.build;
                if(build != null) build.kill();
            })
        ;
        
        toMelt.clear();
        toMelt.addAll(toSpread);
        toSpread.clear();
        
        toMelt.each(it ->
            toSpread.addAll(
                nearby(it).select(n -> isValid(it, n))
            )
        );
        
        if(toMelt.isEmpty() && toSpread.isEmpty()) running = false;
        if(running) Time.run(5, this);
    }
    
    private Seq<Tile> nearby(Tile tile){
        return Seq.with(0, 1, 2, 3)
            .map(tile::nearby)
            .select(Objects::nonNull)
        ;
    }
    
    private boolean isValid(Tile current, Tile nearby){
        if(nearby.floor() == unstable) return true;
        return nearby.floor() == stable && current.floor() == stable;
    }
}
