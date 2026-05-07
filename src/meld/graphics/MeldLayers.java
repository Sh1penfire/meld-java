package meld.graphics;

import mindustry.graphics.Layer;

public class MeldLayers {
    public static float
    //Used to prevent layer collisions
    funnyNumber = 0.691337f,
    smokeLow = 50 + funnyNumber,
    smokeHigh = 105 + funnyNumber,
    overlayOver = Layer.fogOfWar + 2 + funnyNumber,
    sonar = Layer.fogOfWar + 1 + funnyNumber,
    sonarInside = Layer.fogOfWar + 3 + funnyNumber;
}
