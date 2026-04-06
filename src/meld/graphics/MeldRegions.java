package meld.graphics;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import meld.Meld;

public class MeldRegions {

    public static TextureRegion chain;

    public static void load(){
        chain = Core.atlas.find(Meld.prefix("chain"));
    }
}
