package meld.graphics;

import arc.graphics.Color;
import mindustry.graphics.Pal;

public class MeldPal {
    public static Color
    shark = Color.valueOf("a393feff"),
    darkShark = Color.valueOf("665c9f"),
    accentClear = Pal.accent.cpy().a(0),
    blobPink = Color.valueOf("e5aed7"),
    blobPinkClear = blobPink.cpy().a(0),
    flamePink = Color.valueOf("feafea"),
    flamePinkDark = Color.valueOf("d26ab1"),
    shockwaveGray = Color.valueOf("7f7f7f"),
    sparkOrange = Color.valueOf("f6e096");
}
