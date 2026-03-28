package meld.type.unit;

import arc.util.Log;
import mindustry.entities.EntityCollisions;
import mindustry.gen.UnitWaterMove;
import mindustry.world.Tile;

//Bandaid fix for multiplayer units dying randomly because I legit have no idea how to fix it otherwise
public class UnitEdgeWaterMove extends UnitWaterMove {

    //skip all the logic involved and just give rotation
    @Override
    public float prefRotation() {
        return this.moving() && this.type.omniMovement ? this.vel().angle() : this.rotation;
    }

    @Override
    public boolean canShoot() {
        return super.canShoot();
    }

    @Override
    public void update() {
        Tile tile = this.tileOn();

        if (tile != null && !this.canPassOn()) {
            for(int i = 0; i < 4; i++){
                Tile other = tile.nearby(i);
                if(!EntityCollisions.waterSolid(other.x, other.y)){
                    x = other.worldx();
                    y = other.worldy();

                    Log.info("SAVED");
                }
            }
        }
        super.update();

    }
}
