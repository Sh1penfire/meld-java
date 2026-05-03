package meld.graphics;

import arc.graphics.Color;
import mindustry.graphics.Pal;

public class MeldPal {
    public static Color

    meldFloorGlow = Color.valueOf("d26ab1").a(0.3f),
    meldFloorGlowDeep = Color.valueOf("ca3fba").a(0.15f),
    meldFloorGlowHadal = Color.valueOf("ca3fba").a(0.10f),

    aspect = Color.valueOf("f0f5fe"),

    shark = Color.valueOf("a393feff"),
    darkShark = Color.valueOf("665c9f"),
    accentClear = Pal.accent.cpy().a(0),
    accentTranslucent = Pal.accent.cpy().a(0.01f),

    blobPink = Color.valueOf("e5aed7"),
    blobPinkClear = blobPink.cpy().a(0),
    flamePink = Color.valueOf("feafea"),
    flamePinkDark = Color.valueOf("d26ab1"),

    shockwaveGray = Color.valueOf("7f7f7f"),
    sparkOrange = Color.valueOf("f6e096"),
    resoShardFront = Color.valueOf("a3efba"),
    resoShardBack = Color.valueOf("40a547"),
    glassMallowsFront = Color.valueOf("ede0d4"),
    glassMallowsBack = Color.valueOf("cd9d70");
}
