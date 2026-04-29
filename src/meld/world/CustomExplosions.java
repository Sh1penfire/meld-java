package meld.world;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import meld.content.MeldBullets;
import meld.content.MeldItems;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Teamc;

public class CustomExplosions {
    public static void load(){

        Events.on(EventType.UnitDestroyEvent.class, e -> {
            //We do a lil aspect bombing
            if(e.unit.item() == MeldItems.aspectBomb){
                aspectBomb(e.unit.x, e.unit.y, e.unit.stack.amount, e.unit);
            }
        });

        Events.on(EventType.BlockDestroyEvent.class, e -> {
            //We do a lil aspect bombing
            Building b = e.tile.build;
            if(b.items != null && b.items.has(MeldItems.aspectBomb)){
                aspectBomb(b.x, b.y, b.items.get(MeldItems.aspectBomb), b);
            }
        });
    }

    public static void aspectBomb(float x, float y, float amount, Teamc owner){
        for(int i = 0; i < Mathf.floor(amount/50); i++){
            MeldBullets.aspectBombExplosion.create(owner, Team.derelict, x, y, 0);
        }
        for(int i = 0; i < Math.min(amount * 3, 24); i++){
            MeldBullets.bombShrapnel.create(owner, Team.derelict, x, y, Mathf.random(360));
        }
        for(int i = 0; i < Math.min(amount * 2, 32); i++){
            MeldBullets.bombShrapnelAspect.create(owner, Team.derelict, x, y, Mathf.random(360));
        }
    }
}
