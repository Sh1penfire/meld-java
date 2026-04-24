package meld.graphics;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import meld.Meld;

public class MeldRegions {

    public static TextureRegion chain, particle;

    public static void load(){
        chain = Core.atlas.find(Meld.prefix("chain"));
        particle = Core.atlas.find("particle");
    }
}
