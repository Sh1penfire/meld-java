package meld.game;

import java.io.*;
import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.io.*;
import static mindustry.io.SaveVersion.*;

public final class DeadPool implements CustomChunk{
    private static final DeadPool instance = new DeadPool();
    private static final Seq<Death> deaths = new Seq<>();
    
    private DeadPool(){}
    
    public static void init(){
        SaveVersion.addCustomChunk("meld-dead-pool", instance);
        Events.on(UnitDestroyEvent.class, it -> addDeath(Death.from(it)));
    }
    
    public static void addDeath(Death death){
        deaths.add(death);
    }
    
    public static Seq<Death> deaths(){
        return deaths;
    }
    
    @Override
    public void read(DataInput stream) throws IOException{
        deaths.clear();
        
        var count = stream.readInt();
        for(var i = 0; i < count; i++){
            var death = readDeath(stream);
            deaths.add(death);
        }
    }
    
    private Death readDeath(DataInput stream) throws IOException{
        var unit = stream.readUTF();
        var x = stream.readFloat();
        var y = stream.readFloat();
        var time = stream.readLong();
        
        return new Death(unit, new Vec2(x, y), time);
    }
    
    @Override
    public void write(DataOutput stream) throws IOException{
        // TODO: might need to synchronize on the list or make a read-only clone
        stream.writeInt(deaths.size);
        for(var death: deaths){
            writeDeath(stream, death);
        }
    }
    
    private void writeDeath(DataOutput stream, Death death) throws IOException{
        stream.writeUTF(death.unit);
        stream.writeFloat(death.pos.getX());
        stream.writeFloat(death.pos.getY());
        stream.writeLong(death.time);
    }
    
    // TODO: this should be a record, but i can't get jabel to compile them correctly
    public static class Death{
        public final String unit;
        public final Position pos;
        public final long time;
        
        public Death(String u, Position p, long t){
            unit = u;
            pos = p;
            time = t;
        }
        
        public static Death from(UnitDestroyEvent event){
            return new Death(
                event.unit.type.name,
                event.unit,
                Time.millis()
            );
        }
    }
}
