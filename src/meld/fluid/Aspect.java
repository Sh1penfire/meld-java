package meld.fluid;

import arc.graphics.Color;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import meld.meta.MeldStatUnit;
import meld.meta.MeldStats;
import mindustry.ctype.ContentType;
import mindustry.type.Liquid;

public class Aspect extends Liquid {

    public boolean setDefaults = true;
    public float lightOpacity = 0.5f;

    public Aspect(String name) {
        super(name, Color.white);
        databaseCategory = "aspect";
    }

    @Override
    public void init() {
        super.init();
        if(lightColor.equals(Color.clear) && setDefaults) lightColor = new Color(color).a(lightOpacity);
    }

    @Override
    public void setStats() {
        super.setStats();
        MeldStats.aspectStats(this, stats);
    }
}
