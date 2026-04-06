package meld.fluid;

import arc.graphics.Color;
import mindustry.ctype.ContentType;
import mindustry.type.Liquid;

public class Aspect extends Liquid {
    public Aspect(String name) {
        super(name, Color.white);
        databaseCategory = "aspect";
    }
}
