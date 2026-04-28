package meld.graphics;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import meld.Meld;

public class MeldRegions {

    public static TextureRegion chain, particle;

    public static TextureRegion[] chargeRegions;

    public static void load(){
        chain = Core.atlas.find(Meld.prefix("chain"));
        particle = Core.atlas.find("particle");

        chargeRegions = new TextureRegion[5];
        for(int i = 0; i < 5; i++){
            chargeRegions[i] = Core.atlas.find(Meld.prefix("fabricator-battery" + (i + 1)));
        }
    }
}
