package meld.graphics;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.core.World;

import static mindustry.Vars.player;

public class AboveOverlayRenderer {
    public static void draw(){

        int cursorX = tileX(Core.input.mouseX());
        int cursorY = tileY(Core.input.mouseY());

        if(player.isBuilder() && Vars.control.input.isPlacing()){
            int rot = Vars.control.input.block == null ? Vars.control.input.rotation : Vars.control.input.block.planRotation(Vars.control.input.rotation);

            if(Vars.control.input.block.rotate && Vars.control.input.block.drawArrow){
                Vars.control.input.drawArrow(Vars.control.input.block, cursorX, cursorY, rot);
            }
            Draw.color();
            boolean valid = Vars.control.input.validPlace(cursorX, cursorY, Vars.control.input.block, rot);
            Vars.control.input.block.drawPlace(cursorX, cursorY, rot, valid);
        }
    }

    static int tileX(float cursorX){
        Vec2 vec = Core.input.mouseWorld(cursorX, 0);
        if(Vars.control.input.selectedBlock()){
            vec.sub(Vars.control.input.block.offset, Vars.control.input.block.offset);
        }
        return World.toTile(vec.x);
    }

    static int tileY(float cursorY){
        Vec2 vec = Core.input.mouseWorld(0, cursorY);
        if(Vars.control.input.selectedBlock()){
            vec.sub(Vars.control.input.block.offset, Vars.control.input.block.offset);
        }
        return World.toTile(vec.y);
    }
}
