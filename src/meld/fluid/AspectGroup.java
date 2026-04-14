package meld.fluid;

import arc.Core;
import arc.struct.*;
import mindustry.Vars;
import mindustry.type.Liquid;
import mindustry.world.blocks.Attributes;

//Just a centralised way for me to track what does what
public class AspectGroup {

    public static Seq<AspectGroup> groups = new Seq<>();
    public String name, localizedName;

    public AspectGroup(String name){
        //hgjkgvjgfh
        this.name = Vars.content.transformName(name);
        groups.add(this);
    }

    public static void loadAll(){
        groups.each(AspectGroup::load);
    }

    //Kept separate since I want to test some things ingame and be able to reset a single group's name over calling a load function on everything
    public void load(){
        localizedName = Core.bundle.get("aspect-group." + name, name);
    }

    public ObjectMap<Liquid, AspectStats> stats = new ObjectMap<>();

    public static void put(Liquid liquid, AspectGroup group, AspectStats stats){
        group.stats.put(liquid, stats);
    }

    public static AspectGroup
        aether = new AspectGroup("aether"),
        aspect = new AspectGroup("aspect"),
        outlet = new AspectGroup("outlet"),
        fumes = new AspectGroup("fumes");

    public float getDensity(Liquid liquid){
        AspectStats stat = stats.get(liquid);
        return stat == null ? 0 : stat.density;
    }

    public float getEfficiency(Liquid liquid){
        AspectStats stat = stats.get(liquid);
        return stat == null ? 0 : stat.efficiency;
    }

    public static class AspectStats{
        public AspectStats(float efficiency, float density){
            this.efficiency = efficiency;
            this.density = density;
        }

        //No idea if ill use this or not but I really just want something to work atp
        public AspectStats efficiency(float efficiency){
            this.efficiency = efficiency;
            return this;
        }

        public AspectStats density(float density){
            this.density = density;
            return this;
        }

        public float efficiency;
        public float density;
    }
}
